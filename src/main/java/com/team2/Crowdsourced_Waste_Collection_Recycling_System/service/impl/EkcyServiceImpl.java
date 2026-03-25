package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EkcyClassifyRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EkcyLivenessRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EkcyOcrBackRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EkcyOcrFrontRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EkcyOcrFullRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EkcyUploadRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.EkcyFullFlowResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.EkycExtractedProfileResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.EkycSessionResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.integration.vnpt.VnptEkycClient;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.EkycSession;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.EkycSessionRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.CloudinaryService;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.EkcyService;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.util.FileUpLoadUtil;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.util.VnptImagePreprocessUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class EkcyServiceImpl implements EkcyService {
    private final VnptEkycClient vnptEkycClient;
    private final ObjectMapper objectMapper;
    private final EkycSessionRepository ekycSessionRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public String upload(EkcyUploadRequest request, MultipartFile file, boolean enhance) {
        FileUpLoadUtil.assertAllowedImage(file);
        String title = request == null ? null : request.getTitle();
        String description = request == null ? null : request.getDescription();
        if (!enhance) {
            return vnptEkycClient.upload(file, title, description);
        }
        byte[] bytes = VnptImagePreprocessUtil.preprocess(file);
        return vnptEkycClient.uploadBytes(bytes, "file.jpg", title, description);
    }

    @Override
    public JsonNode classify(EkcyClassifyRequest request) {
        return vnptEkycClient.classify(toJsonNode(request));
    }

    @Override
    public JsonNode liveness(EkcyLivenessRequest request) {
        return vnptEkycClient.liveness(toJsonNode(request));
    }

    @Override
    public JsonNode ocrFront(EkcyOcrFrontRequest request) {
        return vnptEkycClient.ocrFront(toJsonNode(request));
    }

    @Override
    public JsonNode ocrBack(EkcyOcrBackRequest request) {
        return vnptEkycClient.ocrBack(toJsonNode(request));
    }

    @Override
    public JsonNode ocrFull(EkcyOcrFullRequest request) {
        return vnptEkycClient.ocrFull(toJsonNode(request));
    }

    @Override
    public EkcyFullFlowResponse fullFlow(MultipartFile frontFile, MultipartFile backFile, String clientSession, String token, Integer type, Boolean validatePostcode, String cropParam, boolean enhance) {
        FileUpLoadUtil.assertAllowedImage(frontFile);
        FileUpLoadUtil.assertAllowedImage(backFile);

        if (clientSession == null || clientSession.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "clientSession không hợp lệ");
        }
        if (token != null && token.length() > 2000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "token không hợp lệ");
        }

        String session = clientSession == null || clientSession.isBlank() ? "UNKNOWN_SESSION" : clientSession.trim();
        int normalizedType = type == null ? -1 : type;
        boolean normalizedValidatePostcode = validatePostcode == null || validatePostcode;
        String normalizedCropParam = cropParam == null ? null : cropParam.trim();

        EkycSession ekycSession = new EkycSession();
        ekycSession.setClientSession(session);
        ekycSession.setToken(token);
        ekycSession.setType(normalizedType);
        ekycSession.setValidatePostcode(normalizedValidatePostcode);
        ekycSession.setCropParam(normalizedCropParam);
        ekycSession.setEnhance(enhance);
        ekycSession.setStatus("PENDING");
        ekycSession = ekycSessionRepository.save(ekycSession);

        String hashFront;
        String hashBack;
        byte[] frontBytes = null;
        byte[] backBytes = null;
        if (!enhance) {
            try {
                var frontUploaded = cloudinaryService.uploadImage(frontFile, "ekyc");
                ekycSession.setFrontImageUrl(frontUploaded.getUrl());
                ekycSession.setFrontImagePublicId(frontUploaded.getPublicId());
                var backUploaded = cloudinaryService.uploadImage(backFile, "ekyc");
                ekycSession.setBackImageUrl(backUploaded.getUrl());
                ekycSession.setBackImagePublicId(backUploaded.getPublicId());
                ekycSession = ekycSessionRepository.save(ekycSession);
            } catch (RuntimeException ignored) {
            }
            hashFront = vnptEkycClient.upload(frontFile, "front", "ekyc_front");
            hashBack = vnptEkycClient.upload(backFile, "back", "ekyc_back");
        } else {
            frontBytes = VnptImagePreprocessUtil.preprocess(frontFile);
            backBytes = VnptImagePreprocessUtil.preprocess(backFile);
            try {
                var frontUploaded = cloudinaryService.uploadImageBytes(frontBytes, "front.jpg", "ekyc");
                ekycSession.setFrontImageUrl(frontUploaded.getUrl());
                ekycSession.setFrontImagePublicId(frontUploaded.getPublicId());
                var backUploaded = cloudinaryService.uploadImageBytes(backBytes, "back.jpg", "ekyc");
                ekycSession.setBackImageUrl(backUploaded.getUrl());
                ekycSession.setBackImagePublicId(backUploaded.getPublicId());
                ekycSession = ekycSessionRepository.save(ekycSession);
            } catch (RuntimeException ignored) {
            }
            hashFront = vnptEkycClient.uploadBytes(frontBytes, "front.jpg", "front", "ekyc_front");
            hashBack = vnptEkycClient.uploadBytes(backBytes, "back.jpg", "back", "ekyc_back");
        }
        ekycSession.setHashFront(hashFront);
        ekycSession.setHashBack(hashBack);
        ekycSession = ekycSessionRepository.save(ekycSession);

        try {
            JsonNode classify = vnptEkycClient.classify(toJsonNode(EkcyClassifyRequest.builder()
                    .imgCard(hashFront)
                    .clientSession(session)
                    .token(token)
                    .build()));
            ekycSession.setClassifyRaw(writeJson(classify));
            ekycSession.setClassifyCode(extractMessageCode(classify));
            ekycSession.setClassifyOk(isOk(classify));
            ekycSession = ekycSessionRepository.save(ekycSession);

            JsonNode liveness = vnptEkycClient.liveness(toJsonNode(EkcyLivenessRequest.builder()
                    .img(hashFront)
                    .clientSession(session)
                    .build()));
            ekycSession.setLivenessRaw(writeJson(liveness));
            ekycSession.setLivenessCode(extractMessageCode(liveness));
            ekycSession.setLivenessOk(isOk(liveness));
            ekycSession = ekycSessionRepository.save(ekycSession);

            JsonNode ocrFront = vnptEkycClient.ocrFront(toJsonNode(EkcyOcrFrontRequest.builder()
                    .imgFront(hashFront)
                    .clientSession(session)
                    .type(normalizedType)
                    .validatePostcode(normalizedValidatePostcode)
                    .token(token)
                    .build()));
            ekycSession.setOcrFrontRaw(writeJson(ocrFront));
            ekycSession.setOcrFrontCode(extractMessageCode(ocrFront));
            ekycSession.setOcrFrontOk(isOk(ocrFront));
            ekycSession = ekycSessionRepository.save(ekycSession);

            JsonNode ocrBack = vnptEkycClient.ocrBack(toJsonNode(EkcyOcrBackRequest.builder()
                    .imgBack(hashBack)
                    .clientSession(session)
                    .type(normalizedType)
                    .token(token)
                    .build()));
            ekycSession.setOcrBackRaw(writeJson(ocrBack));
            ekycSession.setOcrBackCode(extractMessageCode(ocrBack));
            ekycSession.setOcrBackOk(isOk(ocrBack));
            ekycSession = ekycSessionRepository.save(ekycSession);

            EkycExtractedProfileResponse profile = extractProfile(ocrFront, ocrBack);
            applyProfile(ekycSession, profile);

            JsonNode ocrFull = vnptEkycClient.ocrFull(toJsonNode(EkcyOcrFullRequest.builder()
                    .imgFront(hashFront)
                    .imgBack(hashBack)
                    .clientSession(session)
                    .type(normalizedType)
                    .cropParam(normalizedCropParam)
                    .validatePostcode(normalizedValidatePostcode)
                    .token(token)
                    .build()));
            ekycSession.setOcrFullRaw(writeJson(ocrFull));
            ekycSession.setOcrFullCode(extractMessageCode(ocrFull));
            ekycSession.setOcrFullOk(isOk(ocrFull));

            ekycSession.setStatus("COMPLETED");
            ekycSession.setErrorMessage(null);
            ekycSession = ekycSessionRepository.save(ekycSession);

            return EkcyFullFlowResponse.builder()
                    .ekycSessionId(ekycSession.getId())
                    .status(ekycSession.getStatus())
                    .hashFront(hashFront)
                    .hashBack(hashBack)
                    .frontImageUrl(ekycSession.getFrontImageUrl())
                    .frontImagePublicId(ekycSession.getFrontImagePublicId())
                    .backImageUrl(ekycSession.getBackImageUrl())
                    .backImagePublicId(ekycSession.getBackImagePublicId())
                    .profile(profile)
                    .classify(classify)
                    .liveness(liveness)
                    .ocrFront(ocrFront)
                    .ocrBack(ocrBack)
                    .ocrFull(ocrFull)
                    .build();
        } catch (ResponseStatusException ex) {
            ekycSession.setStatus("FAILED");
            String reason = ex.getReason();
            if (reason != null && reason.length() > 500) {
                reason = reason.substring(0, 500);
            }
            ekycSession.setErrorMessage(reason);
            ekycSessionRepository.save(ekycSession);
            throw ex;
        }

    }

    private JsonNode toJsonNode(Object value) {
        if (value == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request không hợp lệ");
        }
        return objectMapper.valueToTree(value);
    }

    private String writeJson(JsonNode node) {
        try {
            return node == null ? null : objectMapper.writeValueAsString(node);
        } catch (Exception e) {
            return node == null ? null : node.toString();
        }
    }

    private String extractMessageCode(JsonNode node) {
        if (node == null || !node.isObject()) return null;
        JsonNode msg = node.get("message");
        if (msg == null || !msg.isTextual()) return null;
        String value = msg.asText().trim();
        return value.isBlank() ? null : value;
    }

    private boolean isOk(JsonNode node) {
        String code = extractMessageCode(node);
        return "IDG-00000000".equalsIgnoreCase(code);
    }

    @Override
    public EkycSessionResponse getSession(String id) {
        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ekycSessionId không hợp lệ");
        }
        EkycSession session = ekycSessionRepository.findById(id.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy eKYC session"));

        return EkycSessionResponse.builder()
                .id(session.getId())
                .userId(session.getUser() != null ? session.getUser().getId() : null)
                .status(session.getStatus())
                .errorMessage(session.getErrorMessage())
                .clientSession(session.getClientSession())
                .token(session.getToken())
                .type(session.getType())
                .validatePostcode(session.getValidatePostcode())
                .cropParam(session.getCropParam())
                .enhance(session.getEnhance())
                .hashFront(session.getHashFront())
                .hashBack(session.getHashBack())
                .frontImageUrl(session.getFrontImageUrl())
                .frontImagePublicId(session.getFrontImagePublicId())
                .backImageUrl(session.getBackImageUrl())
                .backImagePublicId(session.getBackImagePublicId())
                .profile(EkycExtractedProfileResponse.builder()
                        .idNumber(session.getIdNumber())
                        .citizenId(session.getCitizenId())
                        .fullName(session.getFullName())
                        .birthDay(session.getBirthDay())
                        .gender(session.getGender())
                        .nationality(session.getNationality())
                        .originLocation(session.getOriginLocation())
                        .recentLocation(session.getRecentLocation())
                        .issueDate(session.getIssueDate())
                        .issuePlace(session.getIssuePlace())
                        .typeId(session.getTypeId())
                        .backTypeId(session.getBackTypeId())
                        .cardType(session.getCardType())
                        .build())
                .classifyOk(session.getClassifyOk())
                .classifyCode(session.getClassifyCode())
                .classify(readJsonOrNull(session.getClassifyRaw()))
                .livenessOk(session.getLivenessOk())
                .livenessCode(session.getLivenessCode())
                .liveness(readJsonOrNull(session.getLivenessRaw()))
                .ocrFrontOk(session.getOcrFrontOk())
                .ocrFrontCode(session.getOcrFrontCode())
                .ocrFront(readJsonOrNull(session.getOcrFrontRaw()))
                .ocrBackOk(session.getOcrBackOk())
                .ocrBackCode(session.getOcrBackCode())
                .ocrBack(readJsonOrNull(session.getOcrBackRaw()))
                .ocrFullOk(session.getOcrFullOk())
                .ocrFullCode(session.getOcrFullCode())
                .ocrFull(readJsonOrNull(session.getOcrFullRaw()))
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }

    private EkycExtractedProfileResponse extractProfile(JsonNode ocrFront, JsonNode ocrBack) {
        JsonNode frontObj = ocrFront != null ? ocrFront.get("object") : null;
        JsonNode backObj = ocrBack != null ? ocrBack.get("object") : null;

        String idNumber = firstNonBlank(text(frontObj, "id"), text(frontObj, "citizen_id"));
        String citizenId = text(frontObj, "citizen_id");
        String fullName = firstNonBlank(text(frontObj, "name"), extractNameFromLabel(text(frontObj, "name_label")));
        String birthDay = text(frontObj, "birth_day");
        String gender = text(frontObj, "gender");
        String nationality = text(frontObj, "nationality");
        String originLocation = text(frontObj, "origin_location");
        String recentLocation = text(frontObj, "recent_location");
        Integer typeId = intValue(frontObj, "type_id");
        String cardType = text(frontObj, "card_type");

        String issueDate = text(backObj, "issue_date");
        String issuePlace = text(backObj, "issue_place");
        Integer backTypeId = intValue(backObj, "back_type_id");

        return EkycExtractedProfileResponse.builder()
                .idNumber(idNumber)
                .citizenId(citizenId)
                .fullName(fullName)
                .birthDay(birthDay)
                .gender(gender)
                .nationality(nationality)
                .originLocation(originLocation)
                .recentLocation(recentLocation)
                .issueDate(issueDate)
                .issuePlace(issuePlace)
                .typeId(typeId)
                .backTypeId(backTypeId)
                .cardType(cardType)
                .build();
    }

    private void applyProfile(EkycSession session, EkycExtractedProfileResponse profile) {
        if (session == null || profile == null) return;
        session.setIdNumber(trimToLength(profile.getIdNumber(), 32));
        session.setCitizenId(trimToLength(profile.getCitizenId(), 32));
        session.setFullName(trimToLength(profile.getFullName(), 255));
        session.setBirthDay(trimToLength(profile.getBirthDay(), 32));
        session.setGender(trimToLength(profile.getGender(), 32));
        session.setNationality(trimToLength(profile.getNationality(), 64));
        session.setOriginLocation(trimToLength(profile.getOriginLocation(), 255));
        session.setRecentLocation(trimToLength(profile.getRecentLocation(), 255));
        session.setIssueDate(trimToLength(profile.getIssueDate(), 32));
        session.setIssuePlace(trimToLength(profile.getIssuePlace(), 255));
        session.setTypeId(profile.getTypeId());
        session.setBackTypeId(profile.getBackTypeId());
        session.setCardType(trimToLength(profile.getCardType(), 255));
        ekycSessionRepository.save(session);
    }

    private static String text(JsonNode node, String field) {
        if (node == null || field == null) return null;
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) return null;
        if (!value.isTextual()) return null;
        String s = value.asText().trim();
        return s.isBlank() || "-".equals(s) ? null : s;
    }

    private static Integer intValue(JsonNode node, String field) {
        if (node == null || field == null) return null;
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) return null;
        if (value.isInt()) return value.asInt();
        if (value.isTextual()) {
            String s = value.asText().trim();
            if (s.isBlank()) return null;
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) return a;
        if (b != null && !b.isBlank()) return b;
        return null;
    }

    private static String trimToLength(String value, int maxLen) {
        if (value == null) return null;
        String s = value.trim();
        if (s.isBlank()) return null;
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen);
    }

    private static String extractNameFromLabel(String label) {
        if (label == null) return null;
        String s = label.trim();
        if (s.isBlank()) return null;
        int idx = s.indexOf(':');
        if (idx >= 0 && idx + 1 < s.length()) {
            String after = s.substring(idx + 1).trim();
            return after.isBlank() ? null : after;
        }
        return s;
    }

    private JsonNode readJsonOrNull(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return objectMapper.readTree(raw);
        } catch (Exception e) {
            return objectMapper.createObjectNode().put("raw", raw);
        }
    }
}

package com.team2.Crowdsourced_Waste_Collection_Recycling_System.integration.vnpt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.config.VnptEkycProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class VnptEkycClient {
    private final RestTemplate vnptEkycRestTemplate;
    private final VnptEkycProperties properties;
    private final ObjectMapper objectMapper;

    public String upload(MultipartFile file, String title, String description) {
        assertConfigured();
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File không hợp lệ");
        }

        String url = buildUrl("/file-service/v1/addFile");

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new MultipartFileResource(file));
            if (title != null) body.add("title", title);
            if (description != null) body.add("description", description);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = vnptEkycRestTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            String payload = response.getBody();
            if (payload == null || payload.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "VNPT eKYC upload trả về rỗng");
            }
            return extractHash(payload);
        } catch (HttpStatusCodeException ex) {
            throw toExternalApiException("VNPT eKYC upload thất bại", ex);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không đọc được file upload");
        }
    }

    public String uploadBytes(byte[] bytes, String filename, String title, String description) {
        assertConfigured();
        if (bytes == null || bytes.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File không hợp lệ");
        }
        String url = buildUrl("/file-service/v1/addFile");
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new BytesResource(bytes, filename));
            if (title != null) body.add("title", title);
            if (description != null) body.add("description", description);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = vnptEkycRestTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            String payload = response.getBody();
            if (payload == null || payload.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "VNPT eKYC upload trả về rỗng");
            }
            return extractHash(payload);
        } catch (HttpStatusCodeException ex) {
            throw toExternalApiException("VNPT eKYC upload thất bại", ex);
        }
    }

    public JsonNode classify(JsonNode requestBody) {
        return postJson("/ai/v1/classify/id", requestBody, "VNPT eKYC classify");
    }

    public JsonNode liveness(JsonNode requestBody) {
        return postJson("/ai/v1/card/liveness", requestBody, "VNPT eKYC liveness");
    }

    public JsonNode ocrFront(JsonNode requestBody) {
        return postJson("/ai/v1/ocr/id/front", requestBody, "VNPT eKYC ocr front");
    }

    public JsonNode ocrBack(JsonNode requestBody) {
        return postJson("/ai/v1/ocr/id/back", requestBody, "VNPT eKYC ocr back");
    }

    public JsonNode ocrFull(JsonNode requestBody) {
        return postJson("/ai/v1/ocr/id", requestBody, "VNPT eKYC ocr full");
    }

    private JsonNode postJson(String path, JsonNode requestBody, String prefix) {
        assertConfigured();
        String url = buildUrl(path);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);

            ResponseEntity<String> response = vnptEkycRestTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            String payload = response.getBody();
            if (payload == null || payload.isBlank()) {
                return objectMapper.createObjectNode();
            }
            return readJsonLenient(payload);
        } catch (HttpStatusCodeException ex) {
            throw toExternalApiException(prefix, ex);
        } catch (JsonProcessingException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi serialize request eKYC");
        }
    }

    private String buildUrl(String path) {
        String base = properties.getBaseUrl() == null ? "" : properties.getBaseUrl().trim();
        if (base.endsWith("/")) base = base.substring(0, base.length() - 1);
        if (!path.startsWith("/")) path = "/" + path;
        return base + path;
    }

    private void assertConfigured() {
        String baseUrl = properties.getBaseUrl();
        if (baseUrl == null || baseUrl.isBlank()
                || properties.getAccessToken() == null || properties.getAccessToken().isBlank()
                || properties.getTokenId() == null || properties.getTokenId().isBlank()
                || properties.getTokenKey() == null || properties.getTokenKey().isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Thiếu cấu hình VNPT eKYC");
        }
    }

    private ResponseStatusException toExternalApiException(String prefix, HttpStatusCodeException ex) {
        String rawBody = ex.getResponseBodyAsString(StandardCharsets.UTF_8);
        String message = prefix + " (" + ex.getStatusCode().value() + ")";
        if (rawBody != null && !rawBody.isBlank()) {
            message = prefix + " (" + ex.getStatusCode().value() + "): " + extractErrorMessage(rawBody);
        } else if (ex.getStatusText() != null && !ex.getStatusText().isBlank()) {
            message = prefix + " (" + ex.getStatusCode().value() + "): " + ex.getStatusText().trim();
        }
        return new ResponseStatusException(HttpStatus.BAD_GATEWAY, message);
    }

    private String extractHash(String payload) {
        String trimmed = payload.trim();
        if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) {
            return trimmed.replace("\"", "");
        }
        JsonNode json = readJsonLenient(trimmed);

        JsonNode messageNode = json.get("message");
        if (messageNode != null && messageNode.isTextual()) {
            String code = messageNode.asText().trim();
            if (!code.isBlank() && !"IDG-00000000".equalsIgnoreCase(code)) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "VNPT eKYC upload thất bại: " + extractErrorMessage(trimmed));
            }
        }

        JsonNode hash = json.get("hash");
        if (hash != null && !hash.isNull() && !hash.asText().isBlank()) {
            return hash.asText();
        }
        JsonNode object = json.get("object");
        if (object != null && object.isObject()) {
            JsonNode objectHash = object.get("hash");
            if (objectHash != null && objectHash.isTextual() && !objectHash.asText().isBlank()) {
                return objectHash.asText();
            }
        }
        JsonNode data = json.get("data");
        if (data != null && data.isTextual() && !data.asText().isBlank()) {
            return data.asText();
        }
        if (data != null && data.isObject()) {
            JsonNode dataHash = data.get("hash");
            if (dataHash != null && dataHash.isTextual() && !dataHash.asText().isBlank()) {
                return dataHash.asText();
            }
        }
        JsonNode result = json.get("result");
        if (result != null && result.isTextual() && !result.asText().isBlank()) {
            return result.asText();
        }

        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "VNPT eKYC upload response không có hash");
    }

    private String extractErrorMessage(String rawBody) {
        JsonNode json = readJsonLenient(rawBody);
        if (json.isObject()) {
            JsonNode errors = json.get("errors");
            if (errors != null && errors.isArray() && !errors.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (JsonNode e : errors) {
                    if (e != null && e.isTextual() && !e.asText().isBlank()) {
                        if (!sb.isEmpty()) sb.append("; ");
                        sb.append(e.asText().trim());
                    }
                }
                if (!sb.isEmpty()) {
                    return sb.toString();
                }
            }
            JsonNode message = json.get("message");
            if (message != null && message.isTextual() && !message.asText().isBlank()) {
                return message.asText();
            }
            JsonNode error = json.get("error");
            if (error != null && error.isTextual() && !error.asText().isBlank()) {
                return error.asText();
            }
            JsonNode msg = json.get("msg");
            if (msg != null && msg.isTextual() && !msg.asText().isBlank()) {
                return msg.asText();
            }
        }
        String compact = rawBody.replaceAll("\\s+", " ").trim();
        if (compact.length() > 300) compact = compact.substring(0, 300);
        return compact;
    }

    private JsonNode readJsonLenient(String payload) {
        try {
            return objectMapper.readTree(payload);
        } catch (JsonProcessingException e) {
            return objectMapper.createObjectNode().put("raw", payload);
        }
    }

    static class MultipartFileResource extends org.springframework.core.io.ByteArrayResource {
        private final String filename;

        MultipartFileResource(MultipartFile file) throws IOException {
            super(file.getBytes());
            this.filename = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
        }

        @Override
        public String getFilename() {
            return filename;
        }
    }

    static class BytesResource extends org.springframework.core.io.ByteArrayResource {
        private final String filename;

        BytesResource(byte[] bytes, String filename) {
            super(bytes);
            String value = filename == null ? "" : filename.trim();
            this.filename = value.isBlank() ? "file.jpg" : value;
        }

        @Override
        public String getFilename() {
            return filename;
        }
    }
}

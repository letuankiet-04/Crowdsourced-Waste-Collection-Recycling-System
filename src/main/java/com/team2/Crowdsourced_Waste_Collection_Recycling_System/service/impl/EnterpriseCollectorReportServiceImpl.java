package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EnterpriseCollectorReportRewardRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.CollectorReportResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.EnterpriseCollectorReportRewardResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.WasteCategoryResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Citizen;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectorReport;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectorReportImage;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectorReportItem;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Enterprise;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.PointTransaction;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.User;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteReport;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.UserRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorReportImageRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorReportItemRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorReportRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.enterprise.EnterpriseRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.profile.CitizenRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.reward.PointTransactionRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.EnterpriseCollectorReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnterpriseCollectorReportServiceImpl implements EnterpriseCollectorReportService {
    private static final String EARN_TRANSACTION_TYPE = "EARN";

    private final CollectorReportRepository collectorReportRepository;
    private final CollectorReportImageRepository collectorReportImageRepository;
    private final CollectorReportItemRepository collectorReportItemRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final CitizenRepository citizenRepository;
    private final UserRepository userRepository;
    private final PointTransactionRepository pointTransactionRepository;

    @Override
    public List<CollectorReportResponse> getCollectorReports(Integer enterpriseId) {
        validateEnterprise(enterpriseId);

        List<CollectorReport> reports = collectorReportRepository
                .findByCollectionRequest_Enterprise_IdOrderByCreatedAtDesc(enterpriseId);

        List<Integer> reportIds = reports.stream()
                .map(CollectorReport::getId)
                .toList();

        Map<Integer, List<String>> imageUrlsByReportId = reportIds.isEmpty()
                ? Collections.emptyMap()
                : collectorReportImageRepository.findByCollectorReport_IdIn(reportIds).stream()
                .collect(Collectors.groupingBy(
                        img -> img.getCollectorReport().getId(),
                        Collectors.mapping(CollectorReportImage::getImageUrl, Collectors.toList())
                ));

        Map<Integer, List<WasteCategoryResponse>> categoriesByReportId = reportIds.isEmpty()
                ? Collections.emptyMap()
                : collectorReportItemRepository.findWithCategoryByCollectorReportIdIn(reportIds).stream()
                .collect(Collectors.groupingBy(
                        item -> item.getCollectorReport().getId(),
                        Collectors.collectingAndThen(Collectors.toList(), this::toWasteCategoryResponses)
                ));

        return reports.stream()
                .map(r -> CollectorReportResponse.builder()
                        .id(r.getId())
                        .reportCode(r.getReportCode())
                        .collectionRequestId(r.getCollectionRequest().getId())
                        .collectorId(r.getCollector().getId())
                        .status(r.getStatus())
                        .collectorNote(r.getCollectorNote())
                        .totalPoint(r.getTotalPoint())
                        .collectedAt(r.getCollectedAt())
                        .latitude(r.getLatitude())
                        .longitude(r.getLongitude())
                        .createdAt(r.getCreatedAt())
                        .imageUrls(imageUrlsByReportId.getOrDefault(r.getId(), List.of()))
                        .categories(categoriesByReportId.getOrDefault(r.getId(), List.of()))
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public EnterpriseCollectorReportRewardResponse reward(Integer enterpriseId, String actorEmail, Integer collectorReportId, EnterpriseCollectorReportRewardRequest request) {
        validateEnterprise(enterpriseId);
        if (collectorReportId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu collectorReportId");
        }

        Double verificationRate = request != null ? request.getVerificationRate() : null;
        if (verificationRate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu verificationRate");
        }

        CollectorReport collectorReport = collectorReportRepository.findById(collectorReportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CollectorReport không tồn tại"));

        Integer ownerId = collectorReport.getCollectionRequest() != null
                && collectorReport.getCollectionRequest().getEnterprise() != null
                ? collectorReport.getCollectionRequest().getEnterprise().getId()
                : null;
        if (ownerId == null || !ownerId.equals(enterpriseId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền trên collector report này");
        }

        Integer collectionRequestId = collectorReport.getCollectionRequest() != null ? collectorReport.getCollectionRequest().getId() : null;
        if (collectionRequestId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CollectorReport thiếu collectionRequest");
        }
        if (pointTransactionRepository.existsByCollectionRequestIdAndTransactionType(collectionRequestId, EARN_TRANSACTION_TYPE)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Điểm đã được cộng cho collectionRequest này");
        }

        WasteReport wasteReport = collectorReport.getCollectionRequest().getReport();
        Integer citizenId = wasteReport != null && wasteReport.getCitizen() != null ? wasteReport.getCitizen().getId() : null;
        if (citizenId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không xác định được citizen từ collectionRequest");
        }

        List<CollectorReportItem> items = collectorReportItemRepository.findByCollectorReport_Id(collectorReportId);
        if (items == null || items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CollectorReport chưa có item");
        }

        int earnedPoints = 0;
        for (CollectorReportItem item : items) {
            BigDecimal quantity = item.getQuantity();
            BigDecimal pointPerUnit = item.getPointPerUnitSnapshot();

            int basePoints = 0;
            if (quantity != null && pointPerUnit != null) {
                basePoints = quantity.multiply(pointPerUnit).intValue();
            }
            int adjusted = (int) (basePoints * (verificationRate / 100.0));
            item.setTotalPoint(adjusted);
            earnedPoints += adjusted;
        }

        if (earnedPoints <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không có điểm để cộng");
        }

        collectorReportItemRepository.saveAll(items);
        collectorReport.setTotalPoint(earnedPoints);
        collectorReportRepository.save(collectorReport);

        Citizen citizen = citizenRepository.findByIdForUpdate(citizenId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Citizen không tồn tại"));

        int currentPoints = citizen.getTotalPoints() != null ? citizen.getTotalPoints() : 0;
        int balanceAfter = currentPoints + earnedPoints;
        citizen.setTotalPoints(balanceAfter);
        citizenRepository.save(citizen);

        User actor = requireUser(actorEmail);
        LocalDateTime now = LocalDateTime.now();

        PointTransaction tx = new PointTransaction();
        tx.setCitizen(citizen);
        tx.setCollectionRequest(collectorReport.getCollectionRequest());
        tx.setReport(wasteReport);
        tx.setPoints(earnedPoints);
        tx.setTransactionType(EARN_TRANSACTION_TYPE);
        tx.setDescription("Thưởng điểm sau xác thực thu gom (verificationRate=" + verificationRate + "%)");
        tx.setBalanceAfter(balanceAfter);
        tx.setCreatedBy(actor);
        tx.setCreatedAt(now);

        PointTransaction saved = pointTransactionRepository.save(tx);

        return EnterpriseCollectorReportRewardResponse.builder()
                .transactionId(saved.getId())
                .collectorReportId(collectorReportId)
                .collectionRequestId(collectionRequestId)
                .reportId(wasteReport != null ? wasteReport.getId() : null)
                .citizenId(citizenId)
                .points(earnedPoints)
                .verificationRate(verificationRate)
                .balanceAfter(balanceAfter)
                .createdAt(saved.getCreatedAt())
                .build();
    }

    private List<WasteCategoryResponse> toWasteCategoryResponses(List<CollectorReportItem> items) {
        Map<Integer, WasteCategoryResponse> byCategoryId = new LinkedHashMap<>();
        for (CollectorReportItem item : items) {
            if (item.getWasteCategory() == null || item.getWasteCategory().getId() == null) {
                continue;
            }
            Integer categoryId = item.getWasteCategory().getId();
            WasteCategoryResponse existing = byCategoryId.get(categoryId);
            if (existing == null) {
                byCategoryId.put(categoryId, WasteCategoryResponse.builder()
                        .id(categoryId)
                        .name(item.getWasteCategory().getName())
                        .unit(item.getUnitSnapshot() != null ? item.getUnitSnapshot().name() : null)
                        .pointPerUnit(item.getPointPerUnitSnapshot())
                        .quantity(item.getQuantity())
                        .build());
            } else {
                if (existing.getQuantity() == null) {
                    existing.setQuantity(item.getQuantity());
                } else if (item.getQuantity() != null) {
                    existing.setQuantity(existing.getQuantity().add(item.getQuantity()));
                }
            }
        }
        return List.copyOf(byCategoryId.values());
    }

    private User requireUser(String email) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Thiếu email trong token");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User không tồn tại"));
    }

    private Enterprise validateEnterprise(Integer enterpriseId) {
        if (enterpriseId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User hiện tại không phải Enterprise");
        }
        return enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enterprise không tồn tại"));
    }
}

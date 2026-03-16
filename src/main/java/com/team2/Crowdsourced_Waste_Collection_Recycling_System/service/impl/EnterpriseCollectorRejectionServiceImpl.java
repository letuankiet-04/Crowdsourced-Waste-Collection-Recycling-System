package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.EnterpriseCollectorRejectionResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.EnterpriseWasteReportResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.WasteCategoryResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectionRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.ReportImage;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteReport;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteReportItem;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectionRequestRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.enterprise.EnterpriseRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.ReportImageRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.WasteReportItemRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.EnterpriseCollectorRejectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EnterpriseCollectorRejectionServiceImpl implements EnterpriseCollectorRejectionService {
    private final CollectionRequestRepository collectionRequestRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final ReportImageRepository reportImageRepository;
    private final WasteReportItemRepository wasteReportItemRepository;

    @Override
    public List<EnterpriseCollectorRejectionResponse> getCollectorRejections(Integer enterpriseId) {
        if (enterpriseId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User hiện tại không phải Enterprise");
        }
        if (!enterpriseRepository.existsById(enterpriseId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Enterprise không tồn tại");
        }

        List<CollectionRequest> requests = collectionRequestRepository.findCollectorRejectedRequests(enterpriseId);

        List<Integer> reportIds = requests.stream()
                .map(CollectionRequest::getReport)
                .filter(Objects::nonNull)
                .map(WasteReport::getId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Integer, List<String>> imageUrlsByReportId = new HashMap<>();
        if (!reportIds.isEmpty()) {
            for (ReportImage image : reportImageRepository.findByReport_IdIn(reportIds)) {
                if (image == null || image.getReport() == null || image.getReport().getId() == null) {
                    continue;
                }
                imageUrlsByReportId
                        .computeIfAbsent(image.getReport().getId(), k -> new ArrayList<>())
                        .add(image.getImageUrl());
            }
        }

        Map<Integer, List<WasteReportItem>> itemsByReportId = new HashMap<>();
        if (!reportIds.isEmpty()) {
            for (WasteReportItem item : wasteReportItemRepository.findWithCategoryByReportIdIn(reportIds)) {
                if (item == null || item.getReport() == null || item.getReport().getId() == null) {
                    continue;
                }
                itemsByReportId
                        .computeIfAbsent(item.getReport().getId(), k -> new ArrayList<>())
                        .add(item);
            }
        }

        return requests.stream()
                .map(request -> toResponse(request, imageUrlsByReportId, itemsByReportId))
                .toList();
    }

    private EnterpriseCollectorRejectionResponse toResponse(
            CollectionRequest request,
            Map<Integer, List<String>> imageUrlsByReportId,
            Map<Integer, List<WasteReportItem>> itemsByReportId
    ) {
        WasteReport report = request.getReport();
        EnterpriseWasteReportResponse wasteReport = report == null ? null : toWasteReportResponse(
                report,
                imageUrlsByReportId.getOrDefault(report.getId(), List.of()),
                itemsByReportId.getOrDefault(report.getId(), List.of())
        );
        return EnterpriseCollectorRejectionResponse.builder()
                .requestId(request.getId())
                .requestCode(request.getRequestCode())
                .requestStatus(request.getStatus() != null ? request.getStatus().name() : null)
                .rejectionReason(request.getRejectionReason())
                .updatedAt(request.getUpdatedAt())
                .wasteReport(wasteReport)
                .build();
    }

    private EnterpriseWasteReportResponse toWasteReportResponse(
            WasteReport report,
            List<String> imageUrls,
            List<WasteReportItem> items
    ) {
        List<WasteCategoryResponse> categories = toWasteCategoryResponses(items);

        return EnterpriseWasteReportResponse.builder()
                .id(report.getId())
                .reportCode(report.getReportCode())
                .status(report.getStatus() != null ? report.getStatus().name() : null)
                .submitBy(resolveSubmitBy(report))
                .wasteType(report.getWasteType())
                .description(report.getDescription())
                .address(report.getAddress())
                .latitude(report.getLatitude())
                .longitude(report.getLongitude())
                .images(report.getImages())
                .imageUrls(imageUrls)
                .categories(categories)
                .createdAt(report.getCreatedAt())
                .build();
    }

    private String resolveSubmitBy(WasteReport report) {
        if (report == null || report.getCitizen() == null) {
            return null;
        }
        var citizen = report.getCitizen();
        if (citizen.getUser() != null) {
            if (citizen.getUser().getFullName() != null && !citizen.getUser().getFullName().isBlank()) {
                return citizen.getUser().getFullName();
            }
            if (citizen.getUser().getEmail() != null && !citizen.getUser().getEmail().isBlank()) {
                return citizen.getUser().getEmail();
            }
        }
        if (citizen.getFullName() != null && !citizen.getFullName().isBlank()) {
            return citizen.getFullName();
        }
        if (citizen.getEmail() != null && !citizen.getEmail().isBlank()) {
            return citizen.getEmail();
        }
        return null;
    }

    private List<WasteCategoryResponse> toWasteCategoryResponses(List<WasteReportItem> items) {
        Map<Integer, WasteCategoryResponse> byCategoryId = new LinkedHashMap<>();
        for (WasteReportItem item : items) {
            if (item.getWasteCategory() == null || item.getWasteCategory().getId() == null) {
                continue;
            }
            Integer categoryId = item.getWasteCategory().getId();
            WasteCategoryResponse existing = byCategoryId.get(categoryId);
            if (existing == null) {
                byCategoryId.put(categoryId, WasteCategoryResponse.builder()
                        .id(categoryId)
                        .name(item.getWasteCategory().getName())
                        .unit(item.getUnitSnapshot() != null ? item.getUnitSnapshot().name()
                                : (item.getWasteCategory().getUnit() != null ? item.getWasteCategory().getUnit().name() : null))
                        .pointPerUnit(item.getWasteCategory().getPointPerUnit())
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
}

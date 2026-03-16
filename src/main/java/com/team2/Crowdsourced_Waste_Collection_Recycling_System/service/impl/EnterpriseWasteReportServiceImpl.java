package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.EnterpriseWasteReportResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.WasteCategoryResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectionRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Enterprise;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.ReportImage;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteReport;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteReportItem;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.WasteReportStatus;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectionRequestRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.enterprise.EnterpriseRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.ReportImageRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.WasteReportItemRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.WasteReportRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.EnterpriseWasteReportService;
import org.springframework.data.domain.Sort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.util.AddressMatchUtil;

@Service
@RequiredArgsConstructor
public class EnterpriseWasteReportServiceImpl implements EnterpriseWasteReportService {

    private final WasteReportRepository wasteReportRepository;
    private final CollectionRequestRepository collectionRequestRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final ReportImageRepository reportImageRepository;
    private final WasteReportItemRepository wasteReportItemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EnterpriseWasteReportResponse> getReports(Integer enterpriseId, String status) {
        Enterprise enterprise = validateEnterprise(enterpriseId);

        WasteReportStatus statusFilter = null;
        if (status != null && !status.isBlank()) {
            try {
                statusFilter = WasteReportStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status không hợp lệ");
            }
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<WasteReport> reports = statusFilter != null
                ? wasteReportRepository.findByStatus(statusFilter, sort)
                : wasteReportRepository.findAll(sort);

        WasteReportStatus finalStatusFilter = statusFilter;
        List<WasteReport> filteredReports = reports.stream()
                .filter(report -> finalStatusFilter == null || report.getStatus() == finalStatusFilter)
                .filter(report -> AddressMatchUtil.isInServiceArea(report.getAddress(), enterprise.getServiceWards(), enterprise.getServiceCities()))
                .toList();
        return toResponses(filteredReports);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnterpriseWasteReportResponse> getPendingReports(Integer enterpriseId) {
        Enterprise enterprise = validateEnterprise(enterpriseId);

        List<WasteReport> pendingReports = wasteReportRepository.findByStatus(WasteReportStatus.PENDING);

        List<WasteReport> filteredReports = pendingReports.stream()
                .filter(report -> AddressMatchUtil.isInServiceArea(report.getAddress(), enterprise.getServiceWards(), enterprise.getServiceCities()))
                .toList();
        return toResponses(filteredReports);
    }

    private List<EnterpriseWasteReportResponse> toResponses(List<WasteReport> reports) {
        if (reports == null || reports.isEmpty()) {
            return List.of();
        }

        List<Integer> reportIds = reports.stream()
                .map(WasteReport::getId)
                .toList();

        Map<Integer, List<String>> imageUrlsByReportId = reportImageRepository.findByReport_IdIn(reportIds).stream()
                .collect(Collectors.groupingBy(
                        ri -> ri.getReport().getId(),
                        Collectors.mapping(ReportImage::getImageUrl, Collectors.toList())
                ));

        Map<Integer, List<WasteReportItem>> itemsByReportId = wasteReportItemRepository.findWithCategoryByReportIdIn(reportIds).stream()
                .collect(Collectors.groupingBy(i -> i.getReport().getId()));

        Map<Integer, Integer> requestIdByReportId = collectionRequestRepository.findByReport_IdIn(reportIds).stream()
                .collect(Collectors.toMap(
                        cr -> cr.getReport().getId(),
                        CollectionRequest::getId,
                        (a, b) -> a
                ));

        return reports.stream()
                .map(report -> toResponse(
                        report,
                        imageUrlsByReportId.getOrDefault(report.getId(), List.of()),
                        itemsByReportId.getOrDefault(report.getId(), List.of()),
                        requestIdByReportId.get(report.getId())
                ))
                .toList();
    }

    private EnterpriseWasteReportResponse toResponse(
            WasteReport report,
            List<String> imageUrls,
            List<WasteReportItem> items,
            Integer requestId
    ) {
        List<WasteCategoryResponse> categories = toWasteCategoryResponses(items);
        return EnterpriseWasteReportResponse.builder()
                .id(report.getId())
                .reportCode(report.getReportCode())
                .collectionRequestId(requestId)
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

    @Override
    @Transactional(readOnly = true)
    public EnterpriseWasteReportResponse getReportById(Integer enterpriseId, Integer reportId) {
        Enterprise enterprise = validateEnterprise(enterpriseId);
        WasteReport report = validateReport(reportId);

        if (!AddressMatchUtil.isInServiceArea(report.getAddress(), enterprise.getServiceWards(), enterprise.getServiceCities())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Báo cáo không tồn tại");
        }

        List<String> imageUrls = reportImageRepository.findByReport_Id(report.getId()).stream()
                .map(ReportImage::getImageUrl)
                .toList();
        List<WasteReportItem> items = wasteReportItemRepository.findWithCategoryByReportId(report.getId());
        Integer requestId = collectionRequestRepository.findByReport_Id(report.getId())
                .map(CollectionRequest::getId)
                .orElse(null);
        return toResponse(report, imageUrls, items, requestId);
    }

    @Override
    @Transactional
    public void acceptReport(Integer enterpriseId, Integer reportId) {
        WasteReport report = validateReport(reportId);
        report.setStatus(WasteReportStatus.ACCEPTED_ENTERPRISE);
        report.setAcceptedAt(LocalDateTime.now());
        
        wasteReportRepository.save(report);
    }

    @Override
    @Transactional
    public void rejectReport(Integer enterpriseId, Integer reportId, String reason) {
        WasteReport report = validateReport(reportId);
        report.setStatus(WasteReportStatus.REJECTED);
        report.setRejectionReason(reason);
        
        wasteReportRepository.save(report);
    }

    private Enterprise validateEnterprise(Integer enterpriseId) {
        if (enterpriseId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User hiện tại không phải Enterprise");
        }
        return enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enterprise không tồn tại"));
    }

    private WasteReport validateReport(Integer reportId) {
        return wasteReportRepository.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Báo cáo không tồn tại"));
    }

    private void validateProcessingEligibility(Enterprise enterprise, WasteReport report) {
        if (report.getStatus() != WasteReportStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Báo cáo này đã được xử lý hoặc không còn ở trạng thái chờ");
        }
        
        // Kiểm tra xem Enterprise có xử lý được loại rác và khu vực này không
        // Nếu không, họ không nên được phép accept (để tránh tranh giành report mà mình không làm được)
        if (!isSupportedWasteType(enterprise, report.getWasteType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enterprise không hỗ trợ loại rác này");
        }
        
        if (!AddressMatchUtil.isInServiceArea(report.getAddress(), enterprise.getServiceWards(), enterprise.getServiceCities())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Báo cáo nằm ngoài khu vực hoạt động của Enterprise");
        }
    }

    private boolean isSupportedWasteType(Enterprise enterprise, String wasteType) {
        // TODO: Implement check for supported waste types
        return true;
    }
}

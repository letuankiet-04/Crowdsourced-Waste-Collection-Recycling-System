package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.CreateCollectorReportRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.CollectorReportResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.WasteCategoryResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectorReport;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectorReportImage;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectorReportItem;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectionRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteCategory;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteReport;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.CollectionRequestStatus;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.CollectorReportStatus;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.WasteReportStatus;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorReportImageRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorReportItemRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorReportRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectionRequestRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.WasteCategoryRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.WasteReportRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectorReportCreationService {

    private final CollectorReportRepository collectorReportRepository;
    private final CollectionRequestRepository collectionRequestRepository;
    private final WasteCategoryRepository wasteCategoryRepository;
    private final CollectorReportItemRepository collectorReportItemRepository;
    private final CollectorReportImageRepository collectorReportImageRepository;
    private final WasteReportRepository wasteReportRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public CollectorReportResponse createCollectorReport(Integer requestId, Integer collectorId, CreateCollectorReportRequest request) {
        CollectionRequest collectionRequest = getAndValidateCollectionRequest(requestId, collectorId);
        WasteReport wasteReport = getAndValidateWasteReport(collectionRequest);
        validateInput(request);

        Calculation calculation = prepareItems(request.getCategoryIds(), request.getQuantities());
        LocalDateTime now = LocalDateTime.now();

        CollectorReport report = createAndSaveReport(collectionRequest, request, now);
        saveItems(report, calculation.items());
        List<String> imageUrls = uploadImages(report, request.getImages(), now);

        confirmCompleted(requestId, collectorId, calculation.totalWeightKg(), now);
        updateWasteReportStatus(wasteReport, now);

        return buildResponse(report, imageUrls, calculation.items());
    }

    private CollectionRequest getAndValidateCollectionRequest(Integer requestId, Integer collectorId) {
        if (requestId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu requestId");
        }
        if (collectorId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User hiện tại không phải Collector");
        }
        if (collectorReportRepository.existsByCollectionRequest_Id(requestId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Report đã được tạo cho collection request này");
        }

        CollectionRequest collectionRequest = collectionRequestRepository.findByIdAndCollector_Id(requestId, collectorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection Request không tồn tại"));

        if (collectionRequest.getStatus() != CollectionRequestStatus.COLLECTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ có thể tạo report khi task đang ở trạng thái COLLECTED");
        }
        return collectionRequest;
    }

    private WasteReport getAndValidateWasteReport(CollectionRequest collectionRequest) {
        WasteReport wasteReport = collectionRequest.getReport();
        if (wasteReport == null || wasteReport.getLatitude() == null || wasteReport.getLongitude() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Collection Request thiếu toạ độ ban đầu");
        }
        return wasteReport;
    }

    private void validateInput(CreateCollectorReportRequest request) {
        List<MultipartFile> images = request.getImages();
        if (images == null || images.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cần ít nhất 1 ảnh");
        }

        List<Integer> categoryIds = request.getCategoryIds();
        List<BigDecimal> quantities = request.getQuantities();
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phải chọn ít nhất 1 danh mục");
        }
        if (quantities == null || quantities.size() != categoryIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dữ liệu khối lượng không hợp lệ");
        }
    }

    private Calculation prepareItems(List<Integer> categoryIds, List<BigDecimal> quantities) {
        List<CollectorReportItem> items = new ArrayList<>();
        BigDecimal totalWeightKg = BigDecimal.ZERO;
        for (int i = 0; i < categoryIds.size(); i++) {
            Integer categoryId = categoryIds.get(i);
            BigDecimal quantity = quantities.get(i);


            WasteCategory category = wasteCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category ID " + categoryId + " không tồn tại"));

            CollectorReportItem item = new CollectorReportItem();
            item.setWasteCategory(category);
            item.setQuantity(quantity);
            item.setUnitSnapshot(category.getUnit());
            item.setPointPerUnitSnapshot(category.getPointPerUnit());
            item.setTotalPoint(0);

            items.add(item);

            totalWeightKg = totalWeightKg.add(quantity);
        }

        return new Calculation(items, totalWeightKg);
    }

    private CollectorReport createAndSaveReport(CollectionRequest collectionRequest, CreateCollectorReportRequest request, LocalDateTime now) {
        CollectorReport report = new CollectorReport();
        report.setCollectionRequest(collectionRequest);
        report.setCollector(collectionRequest.getCollector());
        report.setStatus(CollectorReportStatus.COMPLETED);
        report.setCollectorNote(request.getCollectorNote());
        report.setTotalPoint(0);
        report.setCollectedAt(now);
        report.setLatitude(BigDecimal.valueOf(request.getLatitude()));
        report.setLongitude(BigDecimal.valueOf(request.getLongitude()));
        report.setCreatedAt(now);

        CollectorReport saved = collectorReportRepository.save(report);
        if (saved.getReportCode() == null || saved.getReportCode().isBlank()) {
            saved.setReportCode(String.format("CRR%06d", saved.getId()));
            saved = collectorReportRepository.save(saved);
        }
        return saved;
    }

    private void saveItems(CollectorReport report, List<CollectorReportItem> items) {
        for (CollectorReportItem item : items) {
            item.setCollectorReport(report);
        }
        collectorReportItemRepository.saveAll(items);
    }

    private List<String> uploadImages(CollectorReport report, List<MultipartFile> images, LocalDateTime now) {
        List<CollectorReportImage> imageEntities = new ArrayList<>();
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : images) {
            var uploaded = cloudinaryService.uploadImage(file, "collectorReport");

            CollectorReportImage img = new CollectorReportImage();
            img.setCollectorReport(report);
            img.setImageUrl(uploaded.getUrl());
            img.setImagePublicId(uploaded.getPublicId());
            img.setCreatedAt(now);

            imageEntities.add(img);
            imageUrls.add(uploaded.getUrl());
        }

        collectorReportImageRepository.saveAll(imageEntities);
        return imageUrls;
    }

    private void confirmCompleted(Integer requestId, Integer collectorId, BigDecimal totalWeightKg, LocalDateTime now) {
        BigDecimal scaledWeightKg = totalWeightKg.setScale(2, RoundingMode.HALF_UP);
        int updated = collectionRequestRepository.confirmCompletedWithWeight(requestId, collectorId, scaledWeightKg, now);
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể xác nhận hoàn tất task");
        }
    }

    private void updateWasteReportStatus(WasteReport wasteReport, LocalDateTime now) {
        wasteReport.setStatus(WasteReportStatus.COLLECTED);
        wasteReport.setUpdatedAt(now);
        wasteReportRepository.save(wasteReport);
    }

    private CollectorReportResponse buildResponse(CollectorReport report, List<String> imageUrls, List<CollectorReportItem> items) {
        List<WasteCategoryResponse> categories = new ArrayList<>();
        for (CollectorReportItem item : items) {
            WasteCategory category = item.getWasteCategory();
            categories.add(WasteCategoryResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .unit(item.getUnitSnapshot().name())
                    .pointPerUnit(item.getPointPerUnitSnapshot())
                    .quantity(item.getQuantity())
                    .build());
        }

        return CollectorReportResponse.builder()
                .id(report.getId())
                .reportCode(report.getReportCode())
                .collectionRequestId(report.getCollectionRequest().getId())
                .collectorId(report.getCollector().getId())
                .status(report.getStatus())
                .collectorNote(report.getCollectorNote())
                .totalPoint(report.getTotalPoint())
                .collectedAt(report.getCollectedAt())
                .latitude(report.getLatitude())
                .longitude(report.getLongitude())
                .createdAt(report.getCreatedAt())
                .imageUrls(imageUrls)
                .categories(categories)
                .build();
    }

    private record Calculation(List<CollectorReportItem> items, BigDecimal totalWeightKg) {
    }
}

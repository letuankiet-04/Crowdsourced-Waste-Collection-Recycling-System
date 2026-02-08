package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.CreateCollectorReportRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.CollectorReportResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.*;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.CollectionRequestStatus;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.CollectorReportStatus;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.citizen.WasteReportRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectionRequestRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectionTrackingRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorReportImageRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorReportRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.CollectorReportService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.CloudinaryService;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.CloudinaryResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CollectorReportServiceImpl implements CollectorReportService {
    private final CollectorReportRepository collectorReportRepository;
    private final CollectorReportImageRepository collectorReportImageRepository;
    private final CollectionRequestRepository collectionRequestRepository;
    private final CollectionTrackingRepository collectionTrackingRepository;
    private final CollectorRepository collectorRepository;
    private final WasteReportRepository wasteReportRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
//tao collector report thì hoàn thành trạng thái thu gom
    public CollectorReportResponse createCollectorReport(CreateCollectorReportRequest request, Integer collectorId) {
        if (request == null || request.getCollectionRequestId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CollectionRequestId is required");
        }
        if (request.getImages() == null || request.getImages().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cần ít nhất 1 ảnh");
        }
        LocalDateTime now = LocalDateTime.now();

        CollectionRequest collectionRequest = collectionRequestRepository.findById(request.getCollectionRequestId())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection Request không tồn tại"));
        if (collectionRequest.getCollector() == null || !collectionRequest.getCollector().getId().equals(collectorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Collection Request không thuộc về bạn");

        }
        if (collectionRequest.getStatus() != CollectionRequestStatus.COLLECTED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Chỉ có thể xác nhận hoàn tất khi status là 'COLLECTED'. Hiện tại:'%s'",
                            collectionRequest.getStatus()));
        }
        if (collectorReportRepository.findByCollectionRequestId(request.getCollectionRequestId()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Report đã tồn tại cho collection request này");
        }

        BigDecimal actualWeightOrganic = normalizeWeight(request.getActualWeightOrganic());
        BigDecimal actualWeightRecyclable = normalizeWeight(request.getActualWeightRecyclable());
        BigDecimal actualWeightHazardous = normalizeWeight(request.getActualWeightHazardous());
        BigDecimal totalActualWeight = actualWeightOrganic.add(actualWeightRecyclable).add(actualWeightHazardous);
        if (totalActualWeight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cần nhập ít nhất 1 loại khối lượng > 0");
        }

        Collector collector = collectorRepository.getReferenceById(collectorId);
        CollectorReport report = new CollectorReport();
        report.setCollectionRequest(collectionRequest);
        report.setCollector(collector);
        report.setStatus(CollectorReportStatus.COMPLETED);
        report.setCollectorNote(request.getCollectorNote());
        report.setActualWeightOrganic(actualWeightOrganic);
        report.setActualWeightRecyclable(actualWeightRecyclable);
        report.setActualWeightHazardous(actualWeightHazardous);
        report.setCollectedAt(now);
        report.setCreatedAt(now);
        CollectorReport savedReport = collectorReportRepository.save(report);
        if (savedReport.getId() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể tạo report");
        }
        if (savedReport.getReportCode() == null || savedReport.getReportCode().isBlank()) {
            savedReport.setReportCode(formatCollectorReportCode(savedReport.getId()));
            savedReport = collectorReportRepository.save(savedReport);
        }
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : request.getImages()) {
            CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadImage(file, "collectorReport");
            CollectorReportImage image = new CollectorReportImage();
            image.setCollectorReport(savedReport);
            image.setImageUrl(cloudinaryResponse.getUrl());
            image.setImagePublicId(cloudinaryResponse.getPublicId());
            image.setCreatedAt(now);
            collectorReportImageRepository.save(image);
            imageUrls.add(cloudinaryResponse.getUrl());
        }

        CollectionTracking tracking = new CollectionTracking();
        tracking.setCollectionRequest(collectionRequest);
        tracking.setCollector(collector);
        tracking.setAction("completed");
        tracking.setNote("Collector confirmed completion");
        tracking.setCreatedAt(now);
        collectionTrackingRepository.save(tracking);

        collectionRequest.setActualWeightKg(totalActualWeight);
        collectionRequest.setUpdatedAt(now);
        collectionRequestRepository.save(collectionRequest);

        int updated = collectionRequestRepository.confirmCompleted(collectionRequest.getId(), collectorId, now);
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể xác nhận hoàn tất");
        }

        WasteReport wasteReport = collectionRequest.getReport();
        if (wasteReport != null) {
            if (request.getAddress() != null && !request.getAddress().isBlank()) {
                wasteReport.setAddress(request.getAddress());
            }
            wasteReport.setUpdatedAt(now);
            wasteReportRepository.save(wasteReport);
        }
        return mapToResponse(savedReport, imageUrls);
    }

    @Override
//lay report theo report id
    public CollectorReportResponse getReportById(Integer reportId, Integer collectorId) {
        CollectorReport report = collectorReportRepository.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report không tồn tại"));
        if (!report.getCollector().getId().equals(collectorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Report không thuộc về bạn");
        }
        
        List<String> imageUrls = new ArrayList<>();
        for (CollectorReportImage image : report.getImages()) {
            imageUrls.add(image.getImageUrl());
        }
        
        return mapToResponse(report, imageUrls);
    }

    @Override
  //lay danh sách report của collector hiện tại
    public Page<CollectorReportResponse> getReportsByCollector(Integer collectorId, Pageable pageable) {
        // page query
        Page<CollectorReport> reports = collectorReportRepository.findByCollectorIdOrderByCreatedAtDesc(collectorId, pageable);
        // map entity -> response
        return reports.map(report -> {
            List<String> imageUrls = new ArrayList<>();
            for (CollectorReportImage image : report.getImages()) {
                imageUrls.add(image.getImageUrl());
            }
            return mapToResponse(report, imageUrls);
        });
    }

    @Override
    //lấy report theo collectionRequest
    public CollectorReportResponse getReportByCollectionRequest(Integer collectionRequestId, Integer collectorId) {
        CollectorReport report = collectorReportRepository.findByCollectionRequestId(collectionRequestId)
                .orElse(null);
        if (report == null) {
            return null;
        }
        if (!report.getCollector().getId().equals(collectorId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Report không thuộc về bạn");
        }
        
        List<String> imageUrls = new ArrayList<>();
        for (CollectorReportImage image : report.getImages()) {
            imageUrls.add(image.getImageUrl());
        }
        
        return mapToResponse(report, imageUrls);
    }

    private CollectorReportResponse mapToResponse(CollectorReport report, List<String> imageUrls) {
        return CollectorReportResponse.builder()
                .reportId(report.getId())
                .reportCode(report.getReportCode())
                .collectionRequestId(report.getCollectionRequest().getId())
                .collectorId(report.getCollector().getId())
                .collectorName(report.getCollector().getUser().getFullName())
                .status(report.getStatus())
                .collectorNote(report.getCollectorNote())
                .actualWeightOrganic(report.getActualWeightOrganic())
                .actualWeightRecyclable(report.getActualWeightRecyclable())
                .actualWeightHazardous(report.getActualWeightHazardous())
                .latitude(report.getLatitude())
                .longitude(report.getLongitude())
                .collectedAt(report.getCollectedAt())
                .createdAt(report.getCreatedAt())
                .imageUrls(imageUrls)
                .build();
    }

    private String formatCollectorReportCode(Integer id) {
        return "CR" + String.format("%06d", id);
    }

    private BigDecimal normalizeWeight(BigDecimal weight) {
        if (weight == null) {
            return BigDecimal.ZERO;
        }
        if (weight.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khối lượng không được âm");
        }
        return weight;
    }
}

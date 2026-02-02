package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectionRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectionTracking;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Collector;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.CollectionRequestRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.CollectionTrackingRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.CollectorRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.CollectorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CollectorServiceImpl implements CollectorService {
    private final CollectionRequestRepository collectionRequestRepository;
    private final CollectionTrackingRepository collectionTrackingRepository;
    private final CollectorRepository collectorRepository;
    @Override
    @Transactional
    public void startTask(Integer requestId, Integer collectorId) {
        //update dyung collector, id va o trang thai assigned
        int updated = collectionRequestRepository.updateStatusIfMatch(
                requestId, collectorId,"accept","on_the_way", LocalDateTime.now()
        );
        CollectionRequest request = getValidRequest(requestId, collectorId, "assigned");

        request.setStatus("on_the_way");
        request.setStartedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        collectionRequestRepository.save(request);

        logTracking(request, collectorId, "started", "Collector started moving");
    }

    @Override
    @Transactional
    public void rejectTask(Integer requestId, Integer collectorId, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lý do từ chối là bắt buộc");
        }

        CollectionRequest request = getValidRequest(requestId, collectorId, "assigned");

        request.setStatus("accepted"); // Back to pool/enterprise
        request.setRejectionReason(reason);
        request.setCollector(null); // Unassign
        request.setUpdatedAt(LocalDateTime.now());
        collectionRequestRepository.save(request);

        logTracking(request, collectorId, "rejected", "Collector rejected task: " + reason);
    }

    @Override
    @Transactional
    public void completeTask(Integer requestId, Integer collectorId) {
        CollectionRequest request = getValidRequest(requestId, collectorId, "on_the_way");

        request.setStatus("collected");
        request.setCollectedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        collectionRequestRepository.save(request);

        logTracking(request, collectorId, "collected", "Collector completed task");
    }

    private CollectionRequest getValidRequest(Integer requestId, Integer collectorId, String expectedStatus) {
        CollectionRequest request = collectionRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection Request không tồn tại"));

        if (request.getCollector() == null || !request.getCollector().getId().equals(collectorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Collection Request không thuộc về bạn");
        }

        if (!expectedStatus.equalsIgnoreCase(request.getStatus())) {
            String message = String.format("Trạng thái không hợp lệ. Mong đợi '%s' nhưng thực tế là '%s'.", expectedStatus, request.getStatus());
            if ("on_the_way".equalsIgnoreCase(request.getStatus()) && "assigned".equalsIgnoreCase(expectedStatus)) {
                message += " Không thể từ chối khi đã bắt đầu di chuyển.";
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
        return request;
    }

    private void logTracking(CollectionRequest request, Integer collectorId, String action, String note) {
        CollectionTracking tracking = new CollectionTracking();
        Collector collector = collectorRepository.getReferenceById(collectorId);

        tracking.setCollectionRequest(request);
        tracking.setCollector(collector);
        tracking.setAction(action);
        tracking.setNote(note);
        tracking.setCreatedAt(LocalDateTime.now());
        collectionTrackingRepository.save(tracking);
    }
}


package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.CreateCollectorFeedbackRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.CollectorFeedbackResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectionRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Collector;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectorFeedback;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectionRequestRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.feedback.CollectorFeedbackRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.CollectorFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectorFeedbackServiceImpl implements CollectorFeedbackService {
    private final CollectorFeedbackRepository collectorFeedbackRepository;
    private final CollectorRepository collectorRepository;
    private final CollectionRequestRepository collectionRequestRepository;

    @Override
    @Transactional
    public CollectorFeedbackResponse createFeedback(Integer collectorId, CreateCollectorFeedbackRequest request) {
        Collector collector = collectorRepository.findById(collectorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collector không tồn tại"));

        CollectionRequest collectionRequest = null;
        if (request.getCollectionRequestId() != null) {
            collectionRequest = collectionRequestRepository.findById(request.getCollectionRequestId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CollectionRequest không tồn tại"));
            if (collectionRequest.getCollector() == null ||
                    collectionRequest.getCollector().getId() == null ||
                    !collectionRequest.getCollector().getId().equals(collectorId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền gắn feedback vào request này");
            }
        }

        LocalDateTime now = LocalDateTime.now();

        CollectorFeedback feedback = new CollectorFeedback();
        feedback.setFeedbackCode("TEMP-CF-" + java.util.UUID.randomUUID());
        feedback.setCollector(collector);
        feedback.setCollectionRequest(collectionRequest);
        feedback.setFeedbackType(request.getType());
        feedback.setSubject(request.getSubject());
        feedback.setContent(request.getContent());
        feedback.setStatus("PENDING");
        feedback.setRating(request.getRating());
        feedback.setCreatedAt(now);
        feedback.setUpdatedAt(now);

        CollectorFeedback saved = collectorFeedbackRepository.save(feedback);
        saved.setFeedbackCode(String.format("CF%03d", saved.getId()));
        CollectorFeedback saved2 = collectorFeedbackRepository.save(saved);

        return toResponse(saved2);
    }

    @Override
    public List<CollectorFeedbackResponse> getMyFeedbacks(Integer collectorId) {
        return collectorFeedbackRepository.findByCollector_IdOrderByCreatedAtDesc(collectorId).stream()
                .map(this::toResponse)
                .toList();
    }

    private CollectorFeedbackResponse toResponse(CollectorFeedback fb) {
        return CollectorFeedbackResponse.builder()
                .id(fb.getId())
                .feedbackCode(fb.getFeedbackCode())
                .collectorId(fb.getCollector() != null ? fb.getCollector().getId() : null)
                .collectionRequestId(fb.getCollectionRequest() != null ? fb.getCollectionRequest().getId() : null)
                .type(fb.getFeedbackType())
                .subject(fb.getSubject())
                .content(fb.getContent())
                .resolution(fb.getResolution())
                .status(fb.getStatus())
                .rating(fb.getRating())
                .createdAt(fb.getCreatedAt())
                .updatedAt(fb.getUpdatedAt())
                .build();
    }
}

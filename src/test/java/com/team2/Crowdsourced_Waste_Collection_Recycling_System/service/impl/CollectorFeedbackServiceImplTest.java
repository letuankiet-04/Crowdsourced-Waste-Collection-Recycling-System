package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.CreateCollectorFeedbackRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectionRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Collector;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectorFeedback;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectionRequestRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.feedback.CollectorFeedbackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollectorFeedbackServiceImplTest {

    @Mock
    private CollectorFeedbackRepository collectorFeedbackRepository;

    @Mock
    private CollectorRepository collectorRepository;

    @Mock
    private CollectionRequestRepository collectionRequestRepository;

    @InjectMocks
    private CollectorFeedbackServiceImpl service;

    @Test
    void createFeedback_generatesCodeAndPersists() {
        Integer collectorId = 7;
        Collector collector = new Collector();
        collector.setId(collectorId);

        CollectionRequest request = new CollectionRequest();
        request.setId(99);
        request.setCollector(collector);

        when(collectorRepository.findById(collectorId)).thenReturn(Optional.of(collector));
        when(collectionRequestRepository.findById(99)).thenReturn(Optional.of(request));

        when(collectorFeedbackRepository.save(any(CollectorFeedback.class))).thenAnswer(invocation -> {
            CollectorFeedback fb = invocation.getArgument(0);
            if (fb.getId() == null) {
                fb.setId(12);
            }
            return fb;
        });

        CreateCollectorFeedbackRequest req = new CreateCollectorFeedbackRequest();
        req.setCollectionRequestId(99);
        req.setType("SYSTEM");
        req.setSubject("App lỗi");
        req.setContent("Không cập nhật được trạng thái task");
        req.setRating(4);

        var res = service.createFeedback(collectorId, req);

        assertThat(res.getId()).isEqualTo(12);
        assertThat(res.getFeedbackCode()).isEqualTo("CF012");
        assertThat(res.getCollectorId()).isEqualTo(collectorId);
        assertThat(res.getCollectionRequestId()).isEqualTo(99);
        assertThat(res.getType()).isEqualTo("SYSTEM");
        assertThat(res.getStatus()).isEqualTo("PENDING");
        assertThat(res.getRating()).isEqualTo(4);

        verify(collectorFeedbackRepository, times(2)).save(any(CollectorFeedback.class));
    }

    @Test
    void createFeedback_whenRequestNotOwned_throwsForbidden() {
        Integer collectorId = 7;
        Collector collector = new Collector();
        collector.setId(collectorId);

        Collector otherCollector = new Collector();
        otherCollector.setId(8);

        CollectionRequest request = new CollectionRequest();
        request.setId(99);
        request.setCollector(otherCollector);

        when(collectorRepository.findById(collectorId)).thenReturn(Optional.of(collector));
        when(collectionRequestRepository.findById(99)).thenReturn(Optional.of(request));

        CreateCollectorFeedbackRequest req = new CreateCollectorFeedbackRequest();
        req.setCollectionRequestId(99);
        req.setType("TASK");
        req.setSubject("Sai phân công");
        req.setContent("Request không thuộc tôi");

        assertThatThrownBy(() -> service.createFeedback(collectorId, req))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("403");
    }

    @Test
    void getMyFeedbacks_mapsToDto() {
        Integer collectorId = 7;

        Collector collector = new Collector();
        collector.setId(collectorId);

        CollectorFeedback fb = new CollectorFeedback();
        fb.setId(1);
        fb.setFeedbackCode("CF001");
        fb.setCollector(collector);
        fb.setFeedbackType("SYSTEM");
        fb.setSubject("S1");
        fb.setContent("C1");
        fb.setStatus("PENDING");
        fb.setCreatedAt(LocalDateTime.now());

        when(collectorFeedbackRepository.findByCollector_IdOrderByCreatedAtDesc(collectorId)).thenReturn(List.of(fb));

        var res = service.getMyFeedbacks(collectorId);

        assertThat(res).hasSize(1);
        assertThat(res.getFirst().getFeedbackCode()).isEqualTo("CF001");
        assertThat(res.getFirst().getCollectorId()).isEqualTo(collectorId);

        verify(collectorFeedbackRepository).findByCollector_IdOrderByCreatedAtDesc(collectorId);
    }
}

package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectionRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectionTracking;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Collector;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Enterprise;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteReport;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.CollectionRequestStatus;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.CollectorStatus;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectionRequestRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectionTrackingRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.WasteReportRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.EnterpriseRequestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnterpriseAssignmentServiceImplTest {

    @Test
    void assignCollector_reassign_cannotAssignToLastRejectedCollector() {
        CollectionRequestRepository collectionRequestRepository = mock(CollectionRequestRepository.class);
        CollectorRepository collectorRepository = mock(CollectorRepository.class);
        CollectionTrackingRepository collectionTrackingRepository = mock(CollectionTrackingRepository.class);
        WasteReportRepository wasteReportRepository = mock(WasteReportRepository.class);
        EnterpriseRequestService enterpriseRequestService = mock(EnterpriseRequestService.class);

        EnterpriseAssignmentServiceImpl service = new EnterpriseAssignmentServiceImpl(
                collectionRequestRepository,
                collectorRepository,
                collectionTrackingRepository,
                wasteReportRepository,
                enterpriseRequestService
        );

        Integer enterpriseId = 1;
        Integer requestId = 10;
        Integer collectorId = 99;

        Enterprise enterprise = new Enterprise();
        enterprise.setId(enterpriseId);

        Collector collector = new Collector();
        collector.setId(collectorId);
        collector.setEnterprise(enterprise);
        collector.setStatus(CollectorStatus.ACTIVE);

        CollectionRequest requestBefore = new CollectionRequest();
        requestBefore.setId(requestId);
        requestBefore.setEnterprise(enterprise);
        requestBefore.setStatus(CollectionRequestStatus.REASSIGN);

        Collector rejectedCollector = new Collector();
        rejectedCollector.setId(collectorId);

        CollectionTracking rejectedTracking = new CollectionTracking();
        rejectedTracking.setCollector(rejectedCollector);

        when(collectorRepository.findById(collectorId)).thenReturn(Optional.of(collector));
        when(collectionRequestRepository.findById(requestId)).thenReturn(Optional.of(requestBefore));
        when(collectionTrackingRepository.findFirstByCollectionRequest_IdAndActionOrderByCreatedAtDesc(requestId, "rejected"))
                .thenReturn(Optional.of(rejectedTracking));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.assignCollector(enterpriseId, requestId, collectorId));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Không thể gán lại cho collector vừa từ chối nhiệm vụ", ex.getReason());
        verify(collectionRequestRepository, never()).assignCollector(any(), any(), any());
        verify(wasteReportRepository, never()).saveAndFlush(any());
        verify(collectionTrackingRepository, never()).save(any());
    }

    @Test
    void assignCollector_reassign_allowsAssignToDifferentCollector() {
        CollectionRequestRepository collectionRequestRepository = mock(CollectionRequestRepository.class);
        CollectorRepository collectorRepository = mock(CollectorRepository.class);
        CollectionTrackingRepository collectionTrackingRepository = mock(CollectionTrackingRepository.class);
        WasteReportRepository wasteReportRepository = mock(WasteReportRepository.class);
        EnterpriseRequestService enterpriseRequestService = mock(EnterpriseRequestService.class);

        EnterpriseAssignmentServiceImpl service = new EnterpriseAssignmentServiceImpl(
                collectionRequestRepository,
                collectorRepository,
                collectionTrackingRepository,
                wasteReportRepository,
                enterpriseRequestService
        );

        Integer enterpriseId = 1;
        Integer requestId = 10;
        Integer collectorId = 99;

        Enterprise enterprise = new Enterprise();
        enterprise.setId(enterpriseId);

        Collector collector = new Collector();
        collector.setId(collectorId);
        collector.setEnterprise(enterprise);
        collector.setStatus(CollectorStatus.ACTIVE);

        CollectionRequest requestBefore = new CollectionRequest();
        requestBefore.setId(requestId);
        requestBefore.setEnterprise(enterprise);
        requestBefore.setStatus(CollectionRequestStatus.REASSIGN);

        WasteReport report = new WasteReport();

        CollectionRequest requestAfter = new CollectionRequest();
        requestAfter.setId(requestId);
        requestAfter.setEnterprise(enterprise);
        requestAfter.setReport(report);

        Collector rejectedCollector = new Collector();
        rejectedCollector.setId(123);

        CollectionTracking rejectedTracking = new CollectionTracking();
        rejectedTracking.setCollector(rejectedCollector);

        when(collectorRepository.findById(collectorId)).thenReturn(Optional.of(collector));
        when(collectionRequestRepository.findById(requestId)).thenReturn(Optional.of(requestBefore), Optional.of(requestAfter));
        when(collectionTrackingRepository.findFirstByCollectionRequest_IdAndActionOrderByCreatedAtDesc(requestId, "rejected"))
                .thenReturn(Optional.of(rejectedTracking));
        when(collectionRequestRepository.assignCollector(requestId, collectorId, enterpriseId)).thenReturn(1);
        when(collectionRequestRepository.getReferenceById(requestId)).thenReturn(requestAfter);
        when(wasteReportRepository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(collectionTrackingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var res = service.assignCollector(enterpriseId, requestId, collectorId);
        assertEquals(requestId, res.getCollectionRequestId());
        assertEquals(collectorId, res.getCollectorId());
        assertEquals("assigned", res.getStatus());

        verify(collectionRequestRepository).assignCollector(requestId, collectorId, enterpriseId);
        verify(wasteReportRepository).saveAndFlush(any());
        verify(collectionTrackingRepository).save(any());
    }
}


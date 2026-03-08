package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectionRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Collector;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectionRequestRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectionTrackingRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.WasteReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollectorServiceImplRejectTaskTest {

    @Test
    void rejectTask_setsReassignAndLogsTracking_withoutAutoAssign() {
        CollectionRequestRepository collectionRequestRepository = mock(CollectionRequestRepository.class);
        CollectionTrackingRepository collectionTrackingRepository = mock(CollectionTrackingRepository.class);
        CollectorRepository collectorRepository = mock(CollectorRepository.class);
        WasteReportRepository wasteReportRepository = mock(WasteReportRepository.class);

        CollectorServiceImpl service = new CollectorServiceImpl(
                collectionRequestRepository,
                collectionTrackingRepository,
                collectorRepository,
                wasteReportRepository
        );

        Integer requestId = 10;
        Integer collectorId = 2;
        String reason = "bận";

        when(collectionRequestRepository.rejectTask(requestId, collectorId, reason)).thenReturn(1);
        when(collectionRequestRepository.findById(requestId)).thenReturn(Optional.of(new CollectionRequest()));
        when(collectionRequestRepository.getReferenceById(requestId)).thenReturn(new CollectionRequest());
        when(collectorRepository.getReferenceById(collectorId)).thenReturn(new Collector());
        when(collectionTrackingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.rejectTask(requestId, collectorId, reason);

        verify(collectionRequestRepository).rejectTask(requestId, collectorId, reason);
        verify(collectionTrackingRepository).save(any());
        verify(collectionRequestRepository).findById(requestId);
        verify(collectionRequestRepository).getReferenceById(requestId);
        verify(collectorRepository).getReferenceById(collectorId);
        verifyNoMoreInteractions(wasteReportRepository);

        verify(collectionRequestRepository, never()).assignCollector(eq(requestId), any(), any());
    }
}


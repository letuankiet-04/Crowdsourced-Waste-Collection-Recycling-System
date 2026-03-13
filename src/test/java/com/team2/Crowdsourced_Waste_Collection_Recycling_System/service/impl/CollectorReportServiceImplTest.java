package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectionRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Collector;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectorReport;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorReportRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectionRequestRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.ReportImageRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.WasteReportItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollectorReportServiceImplTest {

    @Mock
    private CollectorReportRepository collectorReportRepository;

    @Mock
    private CollectionRequestRepository collectionRequestRepository;

    @Mock
    private WasteReportItemRepository wasteReportItemRepository;

    @Mock
    private ReportImageRepository reportImageRepository;

    @InjectMocks
    private CollectorReportServiceImpl service;

    @Test
    void getReportsByCollector_usesFetchJoinRepositoryMethod() {
        Integer collectorId = 7;

        Collector collector = new Collector();
        collector.setId(collectorId);

        CollectionRequest request = new CollectionRequest();
        request.setId(99);

        CollectorReport report = new CollectorReport();
        report.setId(1);
        report.setReportCode("R-1");
        report.setCollector(collector);
        report.setCollectionRequest(request);

        when(collectorReportRepository.findByCollectorIdWithRequest(collectorId)).thenReturn(List.of(report));

        var result = service.getReportsByCollector(collectorId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCollectorId()).isEqualTo(collectorId);
        assertThat(result.getFirst().getCollectionRequestId()).isEqualTo(99);

        verify(collectorReportRepository).findByCollectorIdWithRequest(collectorId);
        verify(collectorReportRepository, never()).findByCollector_IdOrderByCreatedAtDesc(collectorId);
    }
}


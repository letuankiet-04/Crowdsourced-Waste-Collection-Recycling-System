package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Citizen;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectionRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.ReportImage;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.User;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteCategory;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteReport;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteReportItem;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.CollectionRequestStatus;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.WasteUnit;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectionRequestRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.enterprise.EnterpriseRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.ReportImageRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.WasteReportItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnterpriseCollectorRejectionServiceImplTest {

    @Mock
    private CollectionRequestRepository collectionRequestRepository;

    @Mock
    private EnterpriseRepository enterpriseRepository;

    @Mock
    private ReportImageRepository reportImageRepository;

    @Mock
    private WasteReportItemRepository wasteReportItemRepository;

    @InjectMocks
    private EnterpriseCollectorRejectionServiceImpl service;

    @Test
    void getCollectorRejections_batchLoadsImagesAndItems() {
        Integer enterpriseId = 10;

        User u1 = new User();
        u1.setId(1);
        u1.setFullName("Citizen A");
        u1.setEmail("a@example.com");

        Citizen c1 = new Citizen();
        c1.setId(1);
        c1.setUser(u1);

        WasteReport r1 = new WasteReport();
        r1.setId(100);
        r1.setReportCode("WR-100");
        r1.setCitizen(c1);
        r1.setCreatedAt(LocalDateTime.now());

        WasteReport r2 = new WasteReport();
        r2.setId(200);
        r2.setReportCode("WR-200");
        r2.setCitizen(c1);
        r2.setCreatedAt(LocalDateTime.now());

        CollectionRequest req1 = new CollectionRequest();
        req1.setId(1);
        req1.setRequestCode("CR-1");
        req1.setStatus(CollectionRequestStatus.REASSIGN);
        req1.setUpdatedAt(LocalDateTime.now());
        req1.setReport(r1);

        CollectionRequest req2 = new CollectionRequest();
        req2.setId(2);
        req2.setRequestCode("CR-2");
        req2.setStatus(CollectionRequestStatus.REASSIGN);
        req2.setUpdatedAt(LocalDateTime.now());
        req2.setReport(r2);

        WasteCategory cat = new WasteCategory();
        cat.setId(5);
        cat.setName("Plastic");
        cat.setUnit(WasteUnit.KG);
        cat.setPointPerUnit(BigDecimal.ONE);

        WasteReportItem item1 = new WasteReportItem();
        item1.setId(11);
        item1.setReport(r1);
        item1.setWasteCategory(cat);
        item1.setQuantity(new BigDecimal("1.5"));

        WasteReportItem item2 = new WasteReportItem();
        item2.setId(12);
        item2.setReport(r2);
        item2.setWasteCategory(cat);
        item2.setQuantity(new BigDecimal("2.0"));

        ReportImage img1 = new ReportImage();
        img1.setId(21);
        img1.setReport(r1);
        img1.setImageUrl("https://img/1");

        ReportImage img2 = new ReportImage();
        img2.setId(22);
        img2.setReport(r2);
        img2.setImageUrl("https://img/2");

        when(enterpriseRepository.existsById(enterpriseId)).thenReturn(true);
        when(collectionRequestRepository.findCollectorRejectedRequests(enterpriseId)).thenReturn(List.of(req1, req2));
        when(reportImageRepository.findByReport_IdIn(eq(List.of(100, 200)))).thenReturn(List.of(img1, img2));
        when(wasteReportItemRepository.findWithCategoryByReportIdIn(eq(List.of(100, 200)))).thenReturn(List.of(item1, item2));

        var result = service.getCollectorRejections(enterpriseId);

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().getWasteReport().getImageUrls()).containsExactly("https://img/1");
        assertThat(result.get(1).getWasteReport().getImageUrls()).containsExactly("https://img/2");

        verify(reportImageRepository, times(1)).findByReport_IdIn(eq(List.of(100, 200)));
        verify(reportImageRepository, never()).findByReport_Id(anyInt());
        verify(wasteReportItemRepository, times(1)).findWithCategoryByReportIdIn(eq(List.of(100, 200)));
        verify(wasteReportItemRepository, never()).findWithCategoryByReportId(any());
    }
}


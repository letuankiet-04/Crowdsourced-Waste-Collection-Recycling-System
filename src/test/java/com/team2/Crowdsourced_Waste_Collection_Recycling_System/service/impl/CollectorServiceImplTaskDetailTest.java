package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Citizen;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectionRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Collector;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Enterprise;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.ReportImage;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.User;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteCategory;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteReport;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteReportItem;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.WasteReportStatus;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.WasteUnit;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectionRequestRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectionTrackingRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorReportItemRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.ReportImageRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.WasteReportItemRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.WasteReportRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.EnterpriseAssignmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollectorServiceImplTaskDetailTest {

    @Mock
    private CollectionRequestRepository collectionRequestRepository;

    @Mock
    private CollectionTrackingRepository collectionTrackingRepository;

    @Mock
    private CollectorRepository collectorRepository;

    @Mock
    private WasteReportRepository wasteReportRepository;

    @Mock
    private EnterpriseAssignmentService enterpriseAssignmentService;

    @Mock
    private CollectorReportItemRepository collectorReportItemRepository;

    @Mock
    private ReportImageRepository reportImageRepository;

    @Mock
    private WasteReportItemRepository wasteReportItemRepository;

    @InjectMocks
    private CollectorServiceImpl service;

    @Test
    void getTaskDetail_whenOwned_returnsWasteReportDetail() {
        Integer collectorId = 7;
        Integer requestId = 99;
        Integer reportId = 123;

        User citizenUser = new User();
        citizenUser.setFullName("Citizen A");
        citizenUser.setEmail("citizen@example.com");

        Citizen citizen = new Citizen();
        citizen.setUser(citizenUser);

        WasteReport report = new WasteReport();
        report.setId(reportId);
        report.setReportCode("WR123");
        report.setCitizen(citizen);
        report.setWasteType("RECYCLABLE");
        report.setDescription("D1");
        report.setAddress("ADDR");
        report.setLatitude(new BigDecimal("10.12345678"));
        report.setLongitude(new BigDecimal("106.12345678"));
        report.setImages("legacy");
        report.setStatus(WasteReportStatus.ASSIGNED);
        report.setCreatedAt(LocalDateTime.now());

        Collector collector = new Collector();
        collector.setId(collectorId);
        collector.setUser(new User());
        collector.setEnterprise(new Enterprise());

        CollectionRequest request = new CollectionRequest();
        request.setId(requestId);
        request.setRequestCode("CR099");
        request.setReport(report);
        request.setCollector(collector);
        request.setEnterprise(new Enterprise());

        WasteCategory cat = new WasteCategory();
        cat.setId(5);
        cat.setName("Plastic");
        cat.setUnit(WasteUnit.KG);
        cat.setPointPerUnit(new BigDecimal("2.5"));

        WasteReportItem item1 = new WasteReportItem();
        item1.setReport(report);
        item1.setWasteCategory(cat);
        item1.setUnitSnapshot(WasteUnit.KG);
        item1.setQuantity(new BigDecimal("1.25"));

        WasteReportItem item2 = new WasteReportItem();
        item2.setReport(report);
        item2.setWasteCategory(cat);
        item2.setUnitSnapshot(WasteUnit.KG);
        item2.setQuantity(new BigDecimal("2.00"));

        when(collectionRequestRepository.findByIdAndCollector_Id(requestId, collectorId)).thenReturn(Optional.of(request));
        when(reportImageRepository.findByReport_Id(reportId)).thenReturn(List.of(img("u1"), img("u2")));
        when(wasteReportItemRepository.findWithCategoryByReportId(reportId)).thenReturn(List.of(item1, item2));

        var res = service.getTaskDetail(collectorId, requestId);

        assertThat(res.getId()).isEqualTo(reportId);
        assertThat(res.getReportCode()).isEqualTo("WR123");
        assertThat(res.getCollectionRequestId()).isEqualTo(requestId);
        assertThat(res.getSubmitBy()).isEqualTo("Citizen A");
        assertThat(res.getImageUrls()).containsExactly("u1", "u2");
        assertThat(res.getCategories()).hasSize(1);
        assertThat(res.getCategories().getFirst().getId()).isEqualTo(5);
        assertThat(res.getCategories().getFirst().getQuantity()).isEqualByComparingTo(new BigDecimal("3.25"));
    }

    @Test
    void getTaskDetail_whenNotOwned_throwsForbidden() {
        Integer collectorId = 7;
        Integer requestId = 99;

        when(collectionRequestRepository.findByIdAndCollector_Id(requestId, collectorId)).thenReturn(Optional.empty());
        when(collectionRequestRepository.existsById(requestId)).thenReturn(true);

        assertThatThrownBy(() -> service.getTaskDetail(collectorId, requestId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("403");
    }

    @Test
    void getTaskDetail_whenNotFound_throwsNotFound() {
        Integer collectorId = 7;
        Integer requestId = 99;

        when(collectionRequestRepository.findByIdAndCollector_Id(requestId, collectorId)).thenReturn(Optional.empty());
        when(collectionRequestRepository.existsById(requestId)).thenReturn(false);

        assertThatThrownBy(() -> service.getTaskDetail(collectorId, requestId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
    }

    private static ReportImage img(String url) {
        ReportImage img = new ReportImage();
        img.setImageUrl(url);
        return img;
    }
}


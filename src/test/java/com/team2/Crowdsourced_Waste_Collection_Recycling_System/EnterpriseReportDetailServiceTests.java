package com.team2.Crowdsourced_Waste_Collection_Recycling_System;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.EnterpriseRequestReportDetailResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Citizen;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectionRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Enterprise;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Role;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.User;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteCategory;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteReport;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteReportItem;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.CollectionRequestStatus;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.WasteReportStatus;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.WasteUnit;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.RoleRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.UserRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectionRequestRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.enterprise.EnterpriseRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.profile.CitizenRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.WasteCategoryRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.WasteReportItemRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.WasteReportRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.EnterpriseReportDetailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class EnterpriseReportDetailServiceTests {

    @Autowired
    private EnterpriseReportDetailService enterpriseReportDetailService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CitizenRepository citizenRepository;

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private WasteReportRepository wasteReportRepository;

    @Autowired
    private CollectionRequestRepository collectionRequestRepository;

    @Autowired
    private WasteCategoryRepository wasteCategoryRepository;

    @Autowired
    private WasteReportItemRepository wasteReportItemRepository;

    @Test
    void getRequestReportDetail_allowsLazyLoadingWithOpenInViewDisabled() {
        Role citizenRole = roleRepository.saveAndFlush(new Role(null, "CITIZEN", "Citizen", null));

        User user = new User(
                null,
                "citizen@test.com",
                "hash",
                "Citizen A",
                null,
                citizenRole,
                null,
                "active",
                null,
                null
        );
        user = userRepository.saveAndFlush(user);

        Citizen citizen = new Citizen(
                null,
                user,
                "citizen@test.com",
                "Citizen A",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        citizen = citizenRepository.saveAndFlush(citizen);

        Enterprise enterprise = new Enterprise(
                null,
                "Enterprise 1",
                "Addr",
                null,
                "enterprise@test.com",
                null,
                null,
                "active",
                null,
                null
        );
        enterprise = enterpriseRepository.saveAndFlush(enterprise);

        WasteReport wasteReport = new WasteReport(
                null,
                "WR-1",
                citizen,
                "desc",
                "PLASTIC",
                new BigDecimal("10.12345678"),
                new BigDecimal("106.12345678"),
                "address",
                null,
                null,
                WasteReportStatus.PENDING,
                null,
                null,
                null,
                null
        );
        wasteReport = wasteReportRepository.saveAndFlush(wasteReport);

        CollectionRequest request = new CollectionRequest();
        request.setRequestCode("CR-1");
        request.setReport(wasteReport);
        request.setEnterprise(enterprise);
        request.setStatus(CollectionRequestStatus.ACCEPTED_ENTERPRISE);
        request = collectionRequestRepository.saveAndFlush(request);


        WasteCategory category = new WasteCategory(
                null,
                "Plastic",
                null,
                WasteUnit.KG,
                new BigDecimal("10"),
                null,
                null
        );
        category = wasteCategoryRepository.saveAndFlush(category);

        wasteReportItemRepository.saveAndFlush(new WasteReportItem(
                null,
                wasteReport,
                category,
                WasteUnit.KG,
                new BigDecimal("1.5"),
                null
        ));

        EnterpriseRequestReportDetailResponse response = enterpriseReportDetailService.getRequestReportDetail(
                enterprise.getId(),
                request.getId()
        );

        assertThat(response.getRequestId()).isEqualTo(request.getId());
        assertThat(response.getWasteReport()).isNotNull();
        assertThat(response.getWasteReport().getSubmitBy()).isEqualTo("Citizen A");
        assertThat(response.getWasteReport().getCategories()).hasSize(1);
        assertThat(response.getWasteReport().getCategories().get(0).getQuantity()).isEqualByComparingTo("1.5");
    }
}

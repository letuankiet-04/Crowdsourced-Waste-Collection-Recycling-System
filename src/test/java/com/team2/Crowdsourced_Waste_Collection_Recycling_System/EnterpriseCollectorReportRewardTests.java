package com.team2.Crowdsourced_Waste_Collection_Recycling_System;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EnterpriseCollectorReportRewardRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.EnterpriseCollectorReportRewardResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Citizen;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectionRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Collector;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectorReport;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectorReportItem;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Enterprise;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.PointTransaction;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Role;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.User;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteCategory;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteReport;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.CollectionRequestStatus;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.CollectorReportStatus;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.WasteUnit;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.RoleRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.UserRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorReportItemRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorReportRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectionRequestRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.enterprise.EnterpriseRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.profile.CitizenRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.reward.PointTransactionRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.WasteCategoryRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.WasteReportRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.EnterpriseCollectorReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class EnterpriseCollectorReportRewardTests {

    @Autowired
    EnterpriseCollectorReportService enterpriseCollectorReportService;

    @Autowired
    EnterpriseRepository enterpriseRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CitizenRepository citizenRepository;

    @Autowired
    WasteReportRepository wasteReportRepository;

    @Autowired
    CollectionRequestRepository collectionRequestRepository;

    @Autowired
    CollectorRepository collectorRepository;

    @Autowired
    CollectorReportRepository collectorReportRepository;

    @Autowired
    CollectorReportItemRepository collectorReportItemRepository;

    @Autowired
    WasteCategoryRepository wasteCategoryRepository;

    @Autowired
    PointTransactionRepository pointTransactionRepository;

    @Test
    @Transactional
    void reward_shouldCreateEarnTransaction_updateCitizenAndCollectorReportPoints() {
        Enterprise enterprise = new Enterprise();
        enterprise.setName("E1");
        enterprise = enterpriseRepository.save(enterprise);

        Role enterpriseRole = new Role();
        enterpriseRole.setRoleCode("ENTERPRISE");
        enterpriseRole.setRoleName("Enterprise");
        enterpriseRole = roleRepository.save(enterpriseRole);

        Role citizenRole = new Role();
        citizenRole.setRoleCode("CITIZEN");
        citizenRole.setRoleName("Citizen");
        citizenRole = roleRepository.save(citizenRole);

        Role collectorRole = new Role();
        collectorRole.setRoleCode("COLLECTOR");
        collectorRole.setRoleName("Collector");
        collectorRole = roleRepository.save(collectorRole);

        User enterpriseUser = new User();
        enterpriseUser.setEmail("enterprise@test.com");
        enterpriseUser.setPasswordHash("x");
        enterpriseUser.setFullName("Enterprise User");
        enterpriseUser.setRole(enterpriseRole);
        enterpriseUser.setEnterprise(enterprise);
        enterpriseUser = userRepository.save(enterpriseUser);

        User citizenUser = new User();
        citizenUser.setEmail("citizen@test.com");
        citizenUser.setPasswordHash("x");
        citizenUser.setFullName("Citizen User");
        citizenUser.setRole(citizenRole);
        citizenUser = userRepository.save(citizenUser);

        Citizen citizen = new Citizen();
        citizen.setUser(citizenUser);
        citizen.setEmail(citizenUser.getEmail());
        citizen.setFullName(citizenUser.getFullName());
        citizen.setTotalPoints(100);
        citizen = citizenRepository.save(citizen);

        WasteReport report = new WasteReport();
        report.setReportCode("WR0001");
        report.setCitizen(citizen);
        report.setWasteType("MIXED");
        report.setLatitude(new BigDecimal("10.12345678"));
        report.setLongitude(new BigDecimal("106.12345678"));
        report.setCreatedAt(LocalDateTime.now());
        report = wasteReportRepository.save(report);

        User collectorUser = new User();
        collectorUser.setEmail("collector@test.com");
        collectorUser.setPasswordHash("x");
        collectorUser.setFullName("Collector User");
        collectorUser.setRole(collectorRole);
        collectorUser = userRepository.save(collectorUser);

        Collector collector = new Collector();
        collector.setUser(collectorUser);
        collector.setEnterprise(enterprise);
        collector.setEmail(collectorUser.getEmail());
        collector.setFullName(collectorUser.getFullName());
        collector = collectorRepository.save(collector);

        CollectionRequest collectionRequest = new CollectionRequest();
        collectionRequest.setRequestCode("CR0001");
        collectionRequest.setReport(report);
        collectionRequest.setEnterprise(enterprise);
        collectionRequest.setCollector(collector);
        collectionRequest.setStatus(CollectionRequestStatus.COMPLETED);
        collectionRequest.setCreatedAt(LocalDateTime.now());
        collectionRequest = collectionRequestRepository.save(collectionRequest);

        CollectorReport collectorReport = new CollectorReport();
        collectorReport.setReportCode("CRR000001");
        collectorReport.setCollectionRequest(collectionRequest);
        collectorReport.setCollector(collector);
        collectorReport.setStatus(CollectorReportStatus.COMPLETED);
        collectorReport.setTotalPoint(0);
        collectorReport.setCreatedAt(LocalDateTime.now());
        collectorReport = collectorReportRepository.save(collectorReport);

        WasteCategory c1 = new WasteCategory();
        c1.setName("Plastic");
        c1.setUnit(WasteUnit.KG);
        c1.setPointPerUnit(new BigDecimal("10.0"));
        c1 = wasteCategoryRepository.save(c1);

        CollectorReportItem i1 = new CollectorReportItem();
        i1.setCollectorReport(collectorReport);
        i1.setWasteCategory(c1);
        i1.setQuantity(new BigDecimal("2.5"));
        i1.setUnitSnapshot(WasteUnit.KG);
        i1.setPointPerUnitSnapshot(new BigDecimal("10.0"));
        i1.setTotalPoint(0);

        CollectorReportItem i2 = new CollectorReportItem();
        i2.setCollectorReport(collectorReport);
        i2.setWasteCategory(c1);
        i2.setQuantity(new BigDecimal("1.0"));
        i2.setUnitSnapshot(WasteUnit.KG);
        i2.setPointPerUnitSnapshot(new BigDecimal("10.0"));
        i2.setTotalPoint(0);

        collectorReportItemRepository.saveAll(List.of(i1, i2));

        EnterpriseCollectorReportRewardRequest req = EnterpriseCollectorReportRewardRequest.builder()
                .verificationRate(50.0)
                .build();

        EnterpriseCollectorReportRewardResponse res = enterpriseCollectorReportService.reward(
                enterprise.getId(),
                enterpriseUser.getEmail(),
                collectorReport.getId(),
                req
        );

        assertNotNull(res);
        assertEquals(collectorReport.getId(), res.getCollectorReportId());
        assertEquals(collectionRequest.getId(), res.getCollectionRequestId());
        assertEquals(report.getId(), res.getReportId());
        assertEquals(citizen.getId(), res.getCitizenId());
        assertEquals(17, res.getPoints());
        assertEquals(117, res.getBalanceAfter());
        assertNotNull(res.getTransactionId());

        Citizen updatedCitizen = citizenRepository.findById(citizen.getId()).orElseThrow();
        assertEquals(117, updatedCitizen.getTotalPoints());

        CollectorReport updatedReport = collectorReportRepository.findById(collectorReport.getId()).orElseThrow();
        assertEquals(17, updatedReport.getTotalPoint());

        List<CollectorReportItem> updatedItems = collectorReportItemRepository.findByCollectorReport_Id(collectorReport.getId());
        assertEquals(2, updatedItems.size());
        List<Integer> itemPoints = updatedItems.stream().map(CollectorReportItem::getTotalPoint).sorted().toList();
        assertEquals(List.of(5, 12), itemPoints);

        List<PointTransaction> txs = pointTransactionRepository.findByCollectionRequestId(collectionRequest.getId());
        assertEquals(1, txs.size());
        assertEquals("EARN", txs.get(0).getTransactionType());
        assertEquals(17, txs.get(0).getPoints());
        assertEquals(117, txs.get(0).getBalanceAfter());
        assertEquals(enterpriseUser.getEmail(), txs.get(0).getCreatedBy().getEmail());

        Integer enterpriseId = enterprise.getId();
        String actorEmail = enterpriseUser.getEmail();
        Integer collectorReportId = collectorReport.getId();
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                enterpriseCollectorReportService.reward(enterpriseId, actorEmail, collectorReportId, req)
        );
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }
}

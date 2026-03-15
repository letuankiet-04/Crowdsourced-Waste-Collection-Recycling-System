package com.team2.Crowdsourced_Waste_Collection_Recycling_System.config;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.RoleRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.UserRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.enterprise.EnterpriseRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.profile.CitizenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = { "app.seed.enabled=true" })
@ActiveProfiles("test")
class DataInitSeedTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CitizenRepository citizenRepository;

    @Autowired
    private CollectorRepository collectorRepository;

    @Test
    void seedsEnterpriseCitizenAdminCollector() {
        assertTrue(roleRepository.findByRoleCodeIgnoreCase("ADMIN").isPresent());
        assertTrue(roleRepository.findByRoleCodeIgnoreCase("CITIZEN").isPresent());
        assertTrue(roleRepository.findByRoleCodeIgnoreCase("ENTERPRISE").isPresent());
        assertTrue(roleRepository.findByRoleCodeIgnoreCase("COLLECTOR").isPresent());

        assertTrue(enterpriseRepository.findByEmailIgnoreCase("enterprise@gmail.com").isPresent());

        assertTrue(userRepository.findByEmail("admin@gmail.com").isPresent());
        assertTrue(userRepository.findByEmail("citizen@gmail.com").isPresent());
        assertTrue(userRepository.findByEmail("enterprise@gmail.com").isPresent());
        assertTrue(userRepository.findByEmail("collector1@gmail.com").isPresent());
        assertTrue(userRepository.findByEmail("collector2@gmail.com").isPresent());

        var enterpriseUser = userRepository.findOneWithAuthByEmail("enterprise@gmail.com").orElseThrow();
        assertTrue(enterpriseUser.getEnterprise() != null && enterpriseUser.getEnterprise().getId() != null);
        assertTrue(enterpriseRepository.findById(enterpriseUser.getEnterprise().getId()).isPresent());

        assertTrue(citizenRepository.findByUser_Email("citizen@gmail.com").isPresent());

        Integer collectorUser1Id = userRepository.findByEmail("collector1@gmail.com").orElseThrow().getId();
        Integer collectorUser2Id = userRepository.findByEmail("collector2@gmail.com").orElseThrow().getId();
        assertTrue(collectorRepository.findByUserId(collectorUser1Id).isPresent());
        assertTrue(collectorRepository.findByUserId(collectorUser2Id).isPresent());
    }
}

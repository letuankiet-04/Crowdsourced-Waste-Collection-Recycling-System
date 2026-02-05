package com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.citizen;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "app.seed.enabled=false"
})
class WasteTypeRepositoryContextTest {

    @Autowired
    private WasteTypeRepository wasteTypeRepository;

    @Test
    void repositoryLoadsAndQueriesParse() {
        assertNotNull(wasteTypeRepository);
    }
}


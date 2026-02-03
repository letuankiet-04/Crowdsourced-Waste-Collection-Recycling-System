package com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.config.CustomJwtDecoder;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.config.SecurityConfig;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller.enterprise.EnterpriseController;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.AssignCollectorResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectionRequestRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.EnterpriseAssignmentService;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.EnterpriseRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EnterpriseController.class)
@Import(SecurityConfig.class)
class EnterpriseControllerWebMvcTest {
    @Autowired
    MockMvc mockMvc;

    @org.springframework.boot.test.mock.mockito.MockBean
    CollectionRequestRepository collectionRequestRepository;

    @org.springframework.boot.test.mock.mockito.MockBean
    EnterpriseAssignmentService enterpriseAssignmentService;

    @org.springframework.boot.test.mock.mockito.MockBean
    EnterpriseRequestService enterpriseRequestService;

    @org.springframework.boot.test.mock.mockito.MockBean
    CustomJwtDecoder customJwtDecoder;

    @Test
    void assignCollector_reads_requestId_from_path_and_collectorId_from_body() throws Exception {
        AssignCollectorResponse response = AssignCollectorResponse.builder()
                .collectionRequestId(7)
                .collectorId(200)
                .status("assigned")
                .assignedAt(LocalDateTime.now())
                .build();

        when(enterpriseAssignmentService.assignCollector(10, "CR_TEST_0001", 200)).thenReturn(response);

        mockMvc.perform(post("/api/enterprise/requests/{requestCode}/assign", "CR_TEST_0001")
                        .contentType("application/json")
                        .content("{\"collectorId\":200}")
                        .with(jwt().authorities(createAuthorityList("ROLE_ENTERPRISE"))
                                .jwt(j -> j.claim("enterpriseId", 10))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.collectionRequestId").value(7))
                .andExpect(jsonPath("$.result.collectorId").value(200))
                .andExpect(jsonPath("$.result.status").value("assigned"));

        verify(enterpriseAssignmentService).assignCollector(10, "CR_TEST_0001", 200);
        verifyNoMoreInteractions(enterpriseAssignmentService);
    }

    @Test
    void acceptRequest_reads_requestId_from_path() throws Exception {
        when(enterpriseRequestService.acceptRequest(eq(10), eq("CR_TEST_0002"))).thenReturn(8);

        mockMvc.perform(post("/api/enterprise/requests/{requestCode}/accept", "CR_TEST_0002")
                        .with(jwt().authorities(createAuthorityList("ROLE_ENTERPRISE"))
                                .jwt(j -> j.claim("enterpriseId", 10))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.collectionRequestId").value(8))
                .andExpect(jsonPath("$.result.status").value("accepted_enterprise"));

        verify(enterpriseRequestService).acceptRequest(eq(10), eq("CR_TEST_0002"));
        verifyNoMoreInteractions(enterpriseRequestService);
    }
}

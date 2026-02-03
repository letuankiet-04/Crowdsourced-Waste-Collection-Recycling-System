package com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.config.CustomJwtDecoder;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.config.SecurityConfig;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller.collector.CollectionController;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectionRequestRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.CollectorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CollectionController.class)
@Import(SecurityConfig.class)
class CollectionControllerWebMvcTest {
    @Autowired
    MockMvc mockMvc;

    @org.springframework.boot.test.mock.mockito.MockBean
    CollectionRequestRepository collectionRequestRepository;

    @org.springframework.boot.test.mock.mockito.MockBean
    CollectorService collectorService;

    @org.springframework.boot.test.mock.mockito.MockBean
    CustomJwtDecoder customJwtDecoder;

    @Test
    void getTasks_default_calls_active_tasks_query() throws Exception {
        when(collectionRequestRepository.findActiveTasksForCollector(200))
                .thenReturn(List.of(new TaskView(10, "REQ001", "assigned")));

        mockMvc.perform(get("/api/collector/collections/tasks")
                        .with(jwt().authorities(createAuthorityList("ROLE_COLLECTOR"))
                                .jwt(j -> j.claim("collectorId", 200))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].id").value(10))
                .andExpect(jsonPath("$.result[0].requestCode").value("REQ001"))
                .andExpect(jsonPath("$.result[0].status").value("assigned"));

        verify(collectionRequestRepository).findActiveTasksForCollector(200);
        verifyNoMoreInteractions(collectionRequestRepository);
    }

    @Test
    void getTasks_all_true_calls_all_tasks_query() throws Exception {
        when(collectionRequestRepository.findTasksForCollector(200))
                .thenReturn(List.of(new TaskView(11, "REQ002", "collected")));

        mockMvc.perform(get("/api/collector/collections/tasks")
                        .queryParam("all", "true")
                        .with(jwt().authorities(createAuthorityList("ROLE_COLLECTOR"))
                                .jwt(j -> j.claim("collectorId", 200))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].id").value(11));

        verify(collectionRequestRepository).findTasksForCollector(200);
        verifyNoMoreInteractions(collectionRequestRepository);
    }

    @Test
    void getTasks_status_param_calls_status_query() throws Exception {
        when(collectionRequestRepository.findTasksForCollectorByStatus(200, "on_the_way"))
                .thenReturn(List.of(new TaskView(12, "REQ003", "on_the_way")));

        mockMvc.perform(get("/api/collector/collections/tasks")
                        .queryParam("status", "on_the_way")
                        .with(jwt().authorities(createAuthorityList("ROLE_COLLECTOR"))
                                .jwt(j -> j.claim("collectorId", 200))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].status").value("on_the_way"));

        verify(collectionRequestRepository).findTasksForCollectorByStatus(200, "on_the_way");
        verifyNoMoreInteractions(collectionRequestRepository);
    }

    @Test
    void acceptTask_calls_service_and_returns_expected_status() throws Exception {
        mockMvc.perform(post("/api/collector/collections/{id}/accept", 100)
                        .with(jwt().authorities(createAuthorityList("ROLE_COLLECTOR"))
                                .jwt(j -> j.claim("collectorId", 200))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.collectionRequestId").value(100))
                .andExpect(jsonPath("$.result.status").value("accepted_collector"));

        verify(collectorService).acceptTask(100, 200);
        verifyNoMoreInteractions(collectorService);
    }

    @Test
    void startTask_calls_service_and_returns_expected_status() throws Exception {
        mockMvc.perform(post("/api/collector/collections/{id}/start", 101)
                        .with(jwt().authorities(createAuthorityList("ROLE_COLLECTOR"))
                                .jwt(j -> j.claim("collectorId", 200))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.collectionRequestId").value(101))
                .andExpect(jsonPath("$.result.status").value("on_the_way"));

        verify(collectorService).startTask(101, 200);
        verifyNoMoreInteractions(collectorService);
    }

    @Test
    void rejectTask_missing_collectorId_claim_returns_403() throws Exception {
        mockMvc.perform(post("/api/collector/collections/{id}/reject", 102)
                        .contentType("application/json")
                        .content("{\"reason\":\"bận\"}")
                        .with(jwt().authorities(createAuthorityList("ROLE_COLLECTOR"))))
                .andExpect(status().isForbidden());

        verifyNoInteractions(collectorService);
    }

    @Test
    void completeTask_calls_service_and_returns_expected_status() throws Exception {
        mockMvc.perform(post("/api/collector/collections/{id}/complete", 103)
                        .with(jwt().authorities(createAuthorityList("ROLE_COLLECTOR"))
                                .jwt(j -> j.claim("collectorId", 200))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.collectionRequestId").value(103))
                .andExpect(jsonPath("$.result.status").value("collected"));

        verify(collectorService).completeTask(103, 200);
        verifyNoMoreInteractions(collectorService);
    }

    static class TaskView implements CollectionRequestRepository.CollectorTaskView {
        private final Integer id;
        private final String requestCode;
        private final String status;

        TaskView(Integer id, String requestCode, String status) {
            this.id = id;
            this.requestCode = requestCode;
            this.status = status;
        }

        @Override
        public Integer getId() {
            return id;
        }

        @Override
        public String getRequestCode() {
            return requestCode;
        }

        @Override
        public String getStatus() {
            return status;
        }

        @Override
        public String getPriority() {
            return "normal";
        }

        @Override
        public LocalDateTime getAssignedAt() {
            return LocalDateTime.now();
        }

        @Override
        public LocalDateTime getEstimatedArrival() {
            return LocalDateTime.now().plusMinutes(30);
        }

        @Override
        public BigDecimal getDistanceKm() {
            return new BigDecimal("1.20");
        }

        @Override
        public LocalDateTime getCreatedAt() {
            return LocalDateTime.now();
        }

        @Override
        public LocalDateTime getUpdatedAt() {
            return LocalDateTime.now();
        }
    }
}

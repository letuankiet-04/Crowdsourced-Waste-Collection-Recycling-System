package com.team2.Crowdsourced_Waste_Collection_Recycling_System;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UtcLocalDateTimeSerializationTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializesLocalDateTimeWithUtcOffset() throws Exception {
        LocalDateTime value = LocalDateTime.of(2026, 3, 24, 10, 31, 13);
        String json = objectMapper.writeValueAsString(Map.of("t", value));
        assertThat(json).contains("2026-03-24T10:31:13Z");
    }
}


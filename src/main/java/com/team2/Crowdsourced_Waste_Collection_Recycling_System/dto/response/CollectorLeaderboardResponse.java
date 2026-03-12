package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectorLeaderboardResponse {
    private Integer rank;
    private Integer collectorId;
    private String fullName;
    private Long totalTasks;
    private BigDecimal totalWeight;
}

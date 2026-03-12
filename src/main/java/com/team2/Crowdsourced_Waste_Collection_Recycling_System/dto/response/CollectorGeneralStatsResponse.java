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
public class CollectorGeneralStatsResponse {
    private BigDecimal totalWeight;
    private Long totalTasks;
    private Integer day;
    private Integer month;
    private Integer year;
}

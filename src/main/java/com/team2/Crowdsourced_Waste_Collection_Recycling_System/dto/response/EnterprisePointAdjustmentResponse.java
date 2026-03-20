package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterprisePointAdjustmentResponse {
    private Integer id;
    private Integer citizenId;
    private String citizenName;
    private Integer collectionRequestId;
    private Integer reportId;
    private Integer points;
    private String description;
    private Integer balanceAfter;
    private String createdByEmail;
    private LocalDateTime createdAt;
}


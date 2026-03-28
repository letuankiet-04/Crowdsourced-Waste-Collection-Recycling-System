package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnterpriseCollectorReportRewardResponse {
    Integer transactionId;
    Integer collectorReportId;
    Integer collectionRequestId;
    Integer reportId;
    Integer citizenId;
    Integer points;
    Double verificationRate;
    Integer balanceAfter;
    LocalDateTime createdAt;
}


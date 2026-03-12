package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminCollectedWasteByUnitResponse {
    BigDecimal totalWeightKg;
    BigDecimal totalCans;
    BigDecimal totalBottles;
    Map<String, BigDecimal> otherUnits;
}

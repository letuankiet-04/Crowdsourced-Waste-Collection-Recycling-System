package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnterprisePointAdjustmentCreateRequest {
    @NotNull(message = "collectionRequestId không được để trống")
    Integer collectionRequestId;

    Integer citizenId;

    @NotNull(message = "points không được để trống")
    Integer points;

    String description;
}


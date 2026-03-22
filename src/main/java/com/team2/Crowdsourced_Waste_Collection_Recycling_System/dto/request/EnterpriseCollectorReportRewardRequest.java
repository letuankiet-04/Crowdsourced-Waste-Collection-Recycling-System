package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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
public class EnterpriseCollectorReportRewardRequest {
    @NotNull(message = "verificationRate là bắt buộc")
    @DecimalMin(value = "0.0", message = "Tỷ lệ xác thực phải từ 0 đến 100")
    @DecimalMax(value = "100.0", message = "Tỷ lệ xác thực phải từ 0 đến 100")
    Double verificationRate;
}


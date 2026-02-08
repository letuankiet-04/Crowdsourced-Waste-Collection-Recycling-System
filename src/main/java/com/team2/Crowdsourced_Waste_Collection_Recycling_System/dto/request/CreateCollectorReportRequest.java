package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCollectorReportRequest {
    Integer collectionRequestId;
    @Size(max = 1000, message = "Do not exceed 1000 characters.")
    String collectorNote;

    @PositiveOrZero(message = "Actual weight organic must be >= 0")
    BigDecimal actualWeightOrganic;

    @PositiveOrZero(message = "Actual weight recyclable must be >= 0")
    BigDecimal actualWeightRecyclable;

    @PositiveOrZero(message = "Actual weight hazardous must be >= 0")
    BigDecimal actualWeightHazardous;

    @Size(max = 500, message = "Do not exceed 500 characters.")
    String address;

    @NotNull(message = "Image is not  null")
    @Size(min = 1, message = "There must be at least one photo.")
    List<MultipartFile> images;
}

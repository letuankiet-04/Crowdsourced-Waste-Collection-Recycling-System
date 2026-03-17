package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCollectorFeedbackRequest {
    private Integer collectionRequestId;

    @NotBlank
    private String type;

    @NotBlank
    private String content;

    @Min(1)
    @Max(5)
    private Integer rating;
}

package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseFeedbackResolveRequest {
    @NotBlank(message = "Resolution is required")
    private String resolution;
    
    @NotBlank(message = "Status is required")
    private String status;
}

package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectorFeedbackResponse {
    private Integer id;
    private String feedbackCode;
    private Integer collectorId;
    private Integer collectionRequestId;
    private String type;
    private String subject;
    private String content;
    private String resolution;
    private String status;
    private Integer rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

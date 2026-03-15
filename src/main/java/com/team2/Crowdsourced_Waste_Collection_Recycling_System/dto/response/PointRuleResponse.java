package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PointRuleResponse {
    String content;
    LocalDateTime updatedAt;
}

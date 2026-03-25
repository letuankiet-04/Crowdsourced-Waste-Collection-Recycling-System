package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EkcyFullFlowResponse {
    String ekycSessionId;
    String status;
    String hashFront;
    String hashBack;
    EkycExtractedProfileResponse profile;
    JsonNode classify;
    JsonNode liveness;
    JsonNode ocrFront;
    JsonNode ocrBack;
    JsonNode ocrFull;
}

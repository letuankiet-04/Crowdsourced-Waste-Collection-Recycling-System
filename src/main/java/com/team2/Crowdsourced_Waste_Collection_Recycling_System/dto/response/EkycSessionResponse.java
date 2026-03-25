package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EkycSessionResponse {
    String id;
    Integer userId;
    String status;
    String errorMessage;

    String clientSession;
    String token;
    Integer type;
    Boolean validatePostcode;
    String cropParam;
    Boolean enhance;

    String hashFront;
    String hashBack;
    String frontImageUrl;
    String frontImagePublicId;
    String backImageUrl;
    String backImagePublicId;

    EkycExtractedProfileResponse profile;

    Boolean classifyOk;
    String classifyCode;
    JsonNode classify;

    Boolean livenessOk;
    String livenessCode;
    JsonNode liveness;

    Boolean ocrFrontOk;
    String ocrFrontCode;
    JsonNode ocrFront;

    Boolean ocrBackOk;
    String ocrBackCode;
    JsonNode ocrBack;

    Boolean ocrFullOk;
    String ocrFullCode;
    JsonNode ocrFull;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}

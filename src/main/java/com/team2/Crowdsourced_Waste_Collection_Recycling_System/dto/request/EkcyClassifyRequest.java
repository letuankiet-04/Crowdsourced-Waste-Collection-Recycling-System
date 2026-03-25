package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EkcyClassifyRequest {
    @JsonProperty("img_card")
    String imgCard;

    @JsonProperty("client_session")
    String clientSession;

    String token;
}


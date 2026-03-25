package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EkcyOcrFullRequest {
    @JsonProperty("img_front")
    String imgFront;

    @JsonProperty("img_back")
    String imgBack;

    @JsonProperty("client_session")
    String clientSession;

    int type = -1;

    @JsonProperty("crop_param")
    String cropParam;

    @JsonProperty("validate_postcode")
    boolean validatePostcode = true;

    String token;
}


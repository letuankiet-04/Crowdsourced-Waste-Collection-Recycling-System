package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EkcyUploadResponse {
    String hash;
}


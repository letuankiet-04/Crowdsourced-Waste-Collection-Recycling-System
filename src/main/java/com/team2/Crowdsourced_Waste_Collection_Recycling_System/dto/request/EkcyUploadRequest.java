package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EkcyUploadRequest {
    String title;
    String description;
}


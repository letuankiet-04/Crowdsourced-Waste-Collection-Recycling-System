package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EkycExtractedProfileResponse {
    String idNumber;
    String citizenId;
    String fullName;
    String birthDay;
    String gender;
    String nationality;
    String originLocation;
    String recentLocation;
    String issueDate;
    String issuePlace;
    Integer typeId;
    Integer backTypeId;
    String cardType;
}


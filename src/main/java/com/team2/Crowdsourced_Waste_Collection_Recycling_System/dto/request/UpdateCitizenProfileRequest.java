package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request;

import lombok.Data;

@Data
public class UpdateCitizenProfileRequest {
    private String fullName;
    private String email;
    private String address;
    private String phone;
    private String ward;
    private String city;
}


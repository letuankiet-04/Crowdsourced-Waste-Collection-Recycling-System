package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request;

import lombok.Data;

@Data
public class UpdateEnterpriseProfileRequest {
    private String name;
    private String address;
    private String phone;
    private String email;
    private String serviceWards;
    private String serviceCities;
}


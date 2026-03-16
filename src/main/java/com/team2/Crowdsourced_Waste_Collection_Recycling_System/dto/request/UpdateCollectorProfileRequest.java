package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request;

import lombok.Data;

@Data
public class UpdateCollectorProfileRequest {
    private String fullName;
    private String email;
    private String vehicleType;
    private String vehiclePlate;
}


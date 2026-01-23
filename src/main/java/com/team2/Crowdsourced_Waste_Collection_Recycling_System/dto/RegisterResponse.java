package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private String message;
    private UserDto user;
}

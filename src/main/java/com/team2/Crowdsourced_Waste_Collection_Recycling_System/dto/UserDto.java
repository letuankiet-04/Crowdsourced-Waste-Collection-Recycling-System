package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto;

import lombok.Data;

@Data
public class UserDto {
    private Integer id;
    private String email;
    private String roleCode;
    private String passwordHash;
}

package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;

    // Role is optional, defaults to CITIZEN
    private String role; 
    
    // Address fields
    private String address;
    private String ward;
    private String district;
    private String city;

    // Enterprise specific fields
    private String enterpriseName;
    private String taxCode;
    private String licenseNumber;

    // Collector specific fields
    private String vehicleType;
    private String vehiclePlate;
    
    // Enterprise Admin specific fields
    private Integer enterpriseId;
}

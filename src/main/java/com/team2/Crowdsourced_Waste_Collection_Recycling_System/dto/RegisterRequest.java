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

    @NotBlank(message = "Full Name is required")
    private String fullName;

    private String phone;
    
    private String address;
    private String ward;
    private String district;
    private String city;

    // Optional fields for Enterprise registration
    private String role; // "citizen" or "enterprise"
    private String enterpriseName;
    private String taxCode;
    private String licenseNumber;
}

package com.team2.Crowdsourced_Waste_Collection_Recycling_System.mapper;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.AuthResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public AuthResponse toAuthResponse(User user) {
        if (user == null) {
            return null;
        }
        AuthResponse response = new AuthResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setStatus(user.getStatus());
        if (user.getRole() != null) {
            response.setRole(user.getRole().getRoleCode());
        }
        return response;
    }
}

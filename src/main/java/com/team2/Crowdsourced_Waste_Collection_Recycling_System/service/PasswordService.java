package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.ChangePasswordRequest;

public interface PasswordService {
    void changePassword(String email, ChangePasswordRequest request);
}


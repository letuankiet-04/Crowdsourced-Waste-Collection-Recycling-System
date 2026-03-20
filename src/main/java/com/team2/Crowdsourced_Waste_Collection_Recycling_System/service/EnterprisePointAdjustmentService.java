package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EnterprisePointAdjustmentCreateRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EnterprisePointAdjustmentUpdateRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.EnterprisePointAdjustmentResponse;

public interface EnterprisePointAdjustmentService {
    EnterprisePointAdjustmentResponse create(Integer enterpriseId, String actorEmail, EnterprisePointAdjustmentCreateRequest request);

    EnterprisePointAdjustmentResponse update(Integer enterpriseId, String actorEmail, Integer transactionId, EnterprisePointAdjustmentUpdateRequest request);

    void delete(Integer enterpriseId, String actorEmail, Integer transactionId);
}


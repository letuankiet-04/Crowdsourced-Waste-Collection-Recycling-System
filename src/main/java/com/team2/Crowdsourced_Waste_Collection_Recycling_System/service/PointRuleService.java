package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.PointRuleRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.PointRuleResponse;

public interface PointRuleService {
    PointRuleResponse getPointRule();
    PointRuleResponse updatePointRule(PointRuleRequest request);
}

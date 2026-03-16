package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.CreateCollectorFeedbackRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.CollectorFeedbackResponse;

import java.util.List;

public interface CollectorFeedbackService {
    CollectorFeedbackResponse createFeedback(Integer collectorId, CreateCollectorFeedbackRequest request);
    List<CollectorFeedbackResponse> getMyFeedbacks(Integer collectorId);
}

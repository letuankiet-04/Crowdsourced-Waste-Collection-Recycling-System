package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EkcyClassifyRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EkcyLivenessRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EkcyOcrBackRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EkcyOcrFrontRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EkcyOcrFullRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EkcyUploadRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.EkcyFullFlowResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.EkycSessionResponse;
import org.springframework.web.multipart.MultipartFile;

public interface EkcyService {
    String upload(EkcyUploadRequest request, MultipartFile file, boolean enhance);

    JsonNode classify(EkcyClassifyRequest request);

    JsonNode liveness(EkcyLivenessRequest request);

    JsonNode ocrFront(EkcyOcrFrontRequest request);

    JsonNode ocrBack(EkcyOcrBackRequest request);

    JsonNode ocrFull(EkcyOcrFullRequest request);

    EkcyFullFlowResponse fullFlow(MultipartFile frontFile, MultipartFile backFile, String clientSession, String token, Integer type, Boolean validatePostcode, String cropParam, boolean enhance);

    EkycSessionResponse getSession(String id);
}

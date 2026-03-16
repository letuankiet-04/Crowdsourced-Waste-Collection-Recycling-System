package com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller.collector;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.CreateCollectorFeedbackRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.ApiResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.CollectorFeedbackResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.CollectorFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/collector/feedbacks")
@RequiredArgsConstructor
@Validated
@Tag(name = "Collector Feedbacks", description = "Feedback của Collector")
public class CollectorFeedbackController {
    private final CollectorFeedbackService collectorFeedbackService;

    @PostMapping
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Gửi feedback", description = "Collector gửi feedback (có thể gắn với collectionRequestId)")
    public ApiResponse<CollectorFeedbackResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateCollectorFeedbackRequest request) {
        Integer collectorId = CollectorJwtSupport.extractCollectorId(jwt);
        CollectorFeedbackResponse response = collectorFeedbackService.createFeedback(collectorId, request);
        return ApiResponse.<CollectorFeedbackResponse>builder().result(response).build();
    }

    @GetMapping
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Danh sách feedback của tôi", description = "Liệt kê feedback đã gửi của collector hiện tại")
    public ApiResponse<List<CollectorFeedbackResponse>> getMyFeedbacks(
            @AuthenticationPrincipal Jwt jwt) {
        Integer collectorId = CollectorJwtSupport.extractCollectorId(jwt);
        List<CollectorFeedbackResponse> result = collectorFeedbackService.getMyFeedbacks(collectorId);
        return ApiResponse.<List<CollectorFeedbackResponse>>builder().result(result).build();
    }
}

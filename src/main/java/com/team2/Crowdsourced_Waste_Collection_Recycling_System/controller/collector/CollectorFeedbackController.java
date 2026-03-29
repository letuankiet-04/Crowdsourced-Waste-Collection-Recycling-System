package com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller.collector;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller.common.ApiResponses;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.CreateCollectorFeedbackRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.ApiResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.CollectorFeedbackResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.CollectorFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Gửi feedback", description = "Collector gửi feedback (có thể gắn với collectionRequestId)")
    public ResponseEntity<ApiResponse<CollectorFeedbackResponse>> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @ModelAttribute CreateCollectorFeedbackRequest request) {
        Integer collectorId = CollectorJwtSupport.extractCollectorId(jwt);
        CollectorFeedbackResponse response = collectorFeedbackService.createFeedback(collectorId, request);
        return ok(response, "Gửi feedback thành công");
    }

    @GetMapping
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Danh sách feedback của tôi", description = "Liệt kê feedback đã gửi của collector hiện tại")
    public ResponseEntity<ApiResponse<List<CollectorFeedbackResponse>>> getMyFeedbacks(
            @AuthenticationPrincipal Jwt jwt) {
        Integer collectorId = CollectorJwtSupport.extractCollectorId(jwt);
        List<CollectorFeedbackResponse> result = collectorFeedbackService.getMyFeedbacks(collectorId);
        return ok(result, "Lấy danh sách feedback thành công");
    }

    private <T> ResponseEntity<ApiResponse<T>> ok(T result, String message) {
        return ApiResponses.okEntity(result, message);
    }
}

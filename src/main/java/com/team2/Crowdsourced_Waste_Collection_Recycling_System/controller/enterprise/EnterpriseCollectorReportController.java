package com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller.enterprise;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EnterpriseCollectorReportRewardRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.ApiResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.CollectorReportResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.EnterpriseCollectorReportRewardResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.EnterpriseCollectorReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/enterprise/collector-reports")
@RequiredArgsConstructor
public class EnterpriseCollectorReportController extends EnterpriseControllerSupport {

    private final EnterpriseCollectorReportService enterpriseCollectorReportService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ENTERPRISE', 'ENTERPRISE_ADMIN')")
    public ResponseEntity<ApiResponse<List<CollectorReportResponse>>> getCollectorReports(
            @AuthenticationPrincipal Jwt jwt) {

        Integer enterpriseId = extractEnterpriseId(jwt);
        List<CollectorReportResponse> result = enterpriseCollectorReportService.getCollectorReports(enterpriseId);

        return okEntity(result, "Lấy danh sách collector report thành công");
    }

    @PostMapping("/{id}/reward")
    @PreAuthorize("hasAnyRole('ENTERPRISE', 'ENTERPRISE_ADMIN')")
    public ResponseEntity<ApiResponse<EnterpriseCollectorReportRewardResponse>> reward(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Integer collectorReportId,
            @Valid @RequestBody EnterpriseCollectorReportRewardRequest request) {

        Integer enterpriseId = extractEnterpriseId(jwt);
        EnterpriseCollectorReportRewardResponse result = enterpriseCollectorReportService.reward(enterpriseId, jwt.getSubject(), collectorReportId, request);
        return okEntity(result, "Cộng điểm theo verificationRate thành công");
    }
}

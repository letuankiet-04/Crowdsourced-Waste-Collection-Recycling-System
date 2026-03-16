package com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller.enterprise;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.UpdateEnterpriseProfileRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.ApiResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Enterprise;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enterprise/profile")
@RequiredArgsConstructor
@Tag(name = "Enterprise Profile", description = "Cập nhật hồ sơ doanh nghiệp tái chế")
public class EnterpriseProfileController extends EnterpriseControllerSupport {

    private final ProfileService profileService;

    @PutMapping
    @PreAuthorize("hasRole('ENTERPRISE')")
    @Operation(summary = "Cập nhật hồ sơ doanh nghiệp", description = "Cập nhật tên, địa chỉ, phone, email, khu vực phục vụ")
    public ApiResponse<Enterprise> updateMyProfile(@AuthenticationPrincipal Jwt jwt,
                                                   @RequestBody UpdateEnterpriseProfileRequest request) {
        Integer enterpriseId = extractEnterpriseId(jwt);
        Enterprise updated = profileService.updateEnterpriseProfile(enterpriseId, request);
        return ok(updated, "Cập nhật hồ sơ thành công");
    }
}


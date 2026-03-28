package com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller.enterprise;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.ChangePasswordRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.ApiResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.PasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enterprise")
@RequiredArgsConstructor
@Tag(name = "Enterprise Account", description = "Tác vụ tài khoản doanh nghiệp")
public class EnterpriseAccountController extends EnterpriseControllerSupport {

    private final PasswordService passwordService;

    @PutMapping("/password")
    @PreAuthorize("hasAnyRole('ENTERPRISE', 'ENTERPRISE_ADMIN')")
    @Operation(summary = "Đổi mật khẩu", description = "Đổi mật khẩu tài khoản enterprise hiện tại")
    public ApiResponse<Void> changePassword(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ChangePasswordRequest request) {
        extractEnterpriseId(jwt);
        passwordService.changePassword(jwt != null ? jwt.getSubject() : null, request);
        return ok("Đổi mật khẩu thành công");
    }
}

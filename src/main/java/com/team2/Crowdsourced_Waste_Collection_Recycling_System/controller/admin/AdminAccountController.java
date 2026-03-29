package com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller.admin;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller.common.ApiResponses;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.AdminCreateCitizenAccountRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.AdminCreateCollectorAccountRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.AdminCreateEnterpriseAccountRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.ChangePasswordRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.UpdateAdminProfileRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.UpdateCitizenProfileRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.UpdateCollectorProfileRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.UpdateEnterpriseProfileRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.AdminUserResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.ApiResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.DeleteUserPreviewResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.AdminAccountService;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.PasswordService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Admin – Quản lý tài khoản trong hệ thống.
 * Tất cả các endpoint yêu cầu role ADMIN.
 *
 * Base URL: /api/admin/accounts
 */
@RestController
@RequestMapping("/api/admin/accounts")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin – Account Management", description = "Quản lý toàn bộ tài khoản người dùng trong hệ thống")
public class AdminAccountController {

    private final AdminAccountService adminAccountService;
    private final PasswordService passwordService;

    @PatchMapping("/me/profile")
    @Operation(summary = "Admin cập nhật hồ sơ", description = "Cập nhật email và họ tên của admin đang đăng nhập")
    public ApiResponse<AdminUserResponse> updateMyProfile(
            @RequestBody UpdateAdminProfileRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String adminEmail = extractAdminEmail(jwt);
        return ApiResponses.ok(adminAccountService.updateMyProfile(adminEmail, request));
    }

    @PutMapping("/me/password")
    @Operation(summary = "Admin đổi mật khẩu")
    public ApiResponse<Void> changeMyPassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String adminEmail = extractAdminEmail(jwt);
        passwordService.changePassword(adminEmail, request);
        return ApiResponses.message("Đổi mật khẩu thành công");
    }

        @PostMapping("/citizens")
        @Operation(summary = "Tạo tài khoản CITIZEN")
        public ApiResponse<AdminUserResponse> createCitizenAccount(
            @RequestBody AdminCreateCitizenAccountRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String adminEmail = extractAdminEmail(jwt);
        return ApiResponses.ok(adminAccountService.createCitizenAccount(request, adminEmail));
        }

        @PostMapping("/collectors")
        @Operation(summary = "Tạo tài khoản COLLECTOR")
        public ApiResponse<AdminUserResponse> createCollectorAccount(
            @RequestBody AdminCreateCollectorAccountRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String adminEmail = extractAdminEmail(jwt);
        return ApiResponses.ok(adminAccountService.createCollectorAccount(request, adminEmail));
        }

        @PostMapping("/enterprises")
        @Operation(summary = "Tạo tài khoản ENTERPRISE")
        public ApiResponse<AdminUserResponse> createEnterpriseAccount(
            @RequestBody AdminCreateEnterpriseAccountRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String adminEmail = extractAdminEmail(jwt);
        return ApiResponses.ok(adminAccountService.createEnterpriseAccount(request, adminEmail));
    }

    /**
     * Lấy danh sách toàn bộ tài khoản.
     * Có thể lọc theo status (active/suspended) và/hoặc role (CITIZEN,
     * COLLECTOR...).
     */
    @GetMapping
    @Operation(summary = "Lấy danh sách tài khoản", description = "Lọc tuỳ chọn theo status và roleCode")
    public ApiResponse<List<AdminUserResponse>> getAllUsers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            @AuthenticationPrincipal Jwt jwt) {
        String adminEmail = extractAdminEmail(jwt);
        return ApiResponses.ok(adminAccountService.getAllUsers(status, role, adminEmail));
    }

    /**
     * Xem chi tiết một tài khoản.
     */
    @GetMapping("/{userId}")
    @Operation(summary = "Xem chi tiết tài khoản")
    public ApiResponse<AdminUserResponse> getUserDetail(@PathVariable Integer userId) {
        return ApiResponses.ok(adminAccountService.getUserDetail(userId));
    }

        @PutMapping("/{userId}/citizen-profile")
        @Operation(summary = "Admin cập nhật hồ sơ CITIZEN")
        public ApiResponse<AdminUserResponse> updateCitizenProfile(
            @PathVariable Integer userId,
                @RequestBody UpdateCitizenProfileRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String adminEmail = extractAdminEmail(jwt);
            return ApiResponses.ok(adminAccountService.updateCitizenProfile(userId, request, adminEmail));
        }

        @PutMapping("/{userId}/collector-profile")
        @Operation(summary = "Admin cập nhật hồ sơ COLLECTOR")
        public ApiResponse<AdminUserResponse> updateCollectorProfile(
            @PathVariable Integer userId,
                @RequestBody UpdateCollectorProfileRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String adminEmail = extractAdminEmail(jwt);
            return ApiResponses.ok(adminAccountService.updateCollectorProfile(userId, request, adminEmail));
        }

        @PutMapping("/{userId}/enterprise-profile")
        @Operation(summary = "Admin cập nhật hồ sơ ENTERPRISE")
        public ApiResponse<AdminUserResponse> updateEnterpriseProfile(
            @PathVariable Integer userId,
                @RequestBody UpdateEnterpriseProfileRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String adminEmail = extractAdminEmail(jwt);
            return ApiResponses.ok(adminAccountService.updateEnterpriseProfile(userId, request, adminEmail));
        }

    /**
     * Khóa tài khoản (status → suspended).
     * Admin không thể tự khóa chính mình.
     */
    @PatchMapping("/{userId}/suspend")
    @Operation(summary = "Khóa tài khoản")
    public ApiResponse<AdminUserResponse> suspendUser(
            @PathVariable Integer userId,
            @AuthenticationPrincipal Jwt jwt) {
        String adminEmail = extractAdminEmail(jwt);
        return ApiResponses.ok(adminAccountService.suspendUser(userId, adminEmail));
    }

    /**
     * Mở khóa tài khoản (status → active).
     */
    @PatchMapping("/{userId}/activate")
    @Operation(summary = "Mở khóa tài khoản")
    public ApiResponse<AdminUserResponse> activateUser(
            @PathVariable Integer userId,
            @AuthenticationPrincipal Jwt jwt) {
        String adminEmail = extractAdminEmail(jwt);
        return ApiResponses.ok(adminAccountService.activateUser(userId, adminEmail));
    }

    /**
     * Preview data sẽ bị xóa khi hard-delete tài khoản.
     * Gọi endpoint này trước khi xác nhận xóa.
     */
    @GetMapping("/{userId}/delete-preview")
    @Operation(summary = "Preview dữ liệu trước khi xóa",
               description = "Trả về thông tin user và số lượng dữ liệu liên quan sẽ bị xóa vĩnh viễn. Admin không thể xóa chính mình hoặc tài khoản ADMIN.")
    public ApiResponse<DeleteUserPreviewResponse> previewDeleteUser(
            @PathVariable Integer userId,
            @AuthenticationPrincipal Jwt jwt) {
        String adminEmail = extractAdminEmail(jwt);
        return ApiResponses.ok(adminAccountService.previewDeleteUser(userId, adminEmail));
    }

    /**
     * Hard-delete tài khoản: xóa vĩnh viễn user và toàn bộ dữ liệu liên quan.
     * Admin không thể xóa chính mình hoặc tài khoản ADMIN.
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "Xóa tài khoản vĩnh viễn (hard-delete)",
               description = "Xóa vĩnh viễn user và toàn bộ dữ liệu liên quan. Hành động này không thể hoàn tác. Nên gọi preview trước.")
    public ApiResponse<Void> hardDeleteUser(
            @PathVariable Integer userId,
            @AuthenticationPrincipal Jwt jwt) {
        String adminEmail = extractAdminEmail(jwt);
        adminAccountService.hardDeleteUser(userId, adminEmail);
        return ApiResponses.message("Tài khoản và toàn bộ dữ liệu liên quan đã được xóa vĩnh viễn");
    }

    // ─────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────

    /**
     * Lấy email của admin từ JWT subject (claim "sub").
     */
    private String extractAdminEmail(Jwt jwt) {
        if (jwt == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Thiếu token");
        }
        String subject = jwt.getSubject(); // email của admin hiện tại
        if (subject == null || subject.isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không tìm thấy thông tin admin trong token");
        }
        return subject;
    }
}

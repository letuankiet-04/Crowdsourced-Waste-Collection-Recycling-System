package com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller.collector;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller.common.ApiResponses;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.ApiResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.ChangePasswordRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.UpdateCollectorProfileRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.UpdateCollectorStatusRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.CollectorGeneralStatsResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.CollectorLeaderboardResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.CollectorPerformanceStatsResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.CollectorTaskStatusCountResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.CollectorWasteVolumeStatsResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.CollectorWorkHistoryItemResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Collector;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.CollectorService;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.PasswordService;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.ProfileService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/collector")
@Tag(name = "Collector", description = "Endpoint dành cho người thu gom")
public class CollectorController {
    private final CollectorService collectorService;
    private final CollectorRepository collectorRepository;
    private final ProfileService profileService;
    private final PasswordService passwordService;

    public CollectorController(CollectorService collectorService,
                               CollectorRepository collectorRepository,
                               ProfileService profileService,
                               PasswordService passwordService) {
        this.collectorService = collectorService;
        this.collectorRepository = collectorRepository;
        this.profileService = profileService;
        this.passwordService = passwordService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Trang tổng quan Collector", description = "Thông tin tổng quan nhanh cho Collector")
    public ApiResponse<Map<String, Object>> getDashboard(@AuthenticationPrincipal Jwt jwt) {
        Integer collectorId = getCollectorId(jwt);
        String status = collectorRepository.findById(collectorId)
                .map(Collector::getStatus)
                .map(Enum::name)
                .orElse(null);
        boolean online = "ONLINE".equalsIgnoreCase(status);
        return ApiResponses.ok(Map.of(
                "collectorId", collectorId,
                "status", status,
                "online", online
        ));
    }

    @GetMapping("/stats/performance")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Hiệu suất thu gom", description = "Thống kê số lượng thu gom theo tháng")
    public ApiResponse<CollectorPerformanceStatsResponse> getPerformanceStats(@AuthenticationPrincipal Jwt jwt, @RequestParam(required = false) Integer year) {
        Integer collectorId = getCollectorId(jwt);
        return ApiResponses.ok(collectorService.getStats(collectorId, year));
    }

    @GetMapping("/stats/waste-volume")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Khối lượng rác thu gom", description = "Thống kê khối lượng rác theo tháng/quý")
    public ApiResponse<CollectorWasteVolumeStatsResponse> getWasteVolumeStats(@AuthenticationPrincipal Jwt jwt, @RequestParam(required = false) Integer year) {
        Integer collectorId = getCollectorId(jwt);
        return ApiResponses.ok(collectorService.getWasteVolumeStats(collectorId, year));
    }

    @GetMapping("/stats/waste-type")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Thống kê theo loại rác", description = "Tổng khối lượng rác theo từng loại")
    public ApiResponse<Map<String, BigDecimal>> getWasteTypeStats(@AuthenticationPrincipal Jwt jwt) {
        Integer collectorId = getCollectorId(jwt);
        return ApiResponses.ok(collectorService.getWasteTypeStats(collectorId));
    }

    @GetMapping("/stats/tasks")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Thống kê trạng thái task", description = "Số lượng task theo từng trạng thái")
    public ApiResponse<List<CollectorTaskStatusCountResponse>> getTaskStatusCounts(@AuthenticationPrincipal Jwt jwt) {
        Integer collectorId = getCollectorId(jwt);
        return ApiResponses.ok(collectorService.getTaskStatusCounts(collectorId));
    }

    @GetMapping("/stats/general")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Thống kê tổng quát", description = "Thống kê khối lượng rác và số task theo ngày, tháng, năm")
    public ApiResponse<CollectorGeneralStatsResponse> getGeneralStats(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        Integer collectorId = getCollectorId(jwt);
        return ApiResponses.ok(collectorService.getGeneralStats(collectorId, day, month, year));
    }

    @GetMapping("/leaderboard")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Bảng xếp hạng Collector", description = "Xếp hạng theo số lượng task hoàn thành (KPI)")
    public ApiResponse<List<CollectorLeaderboardResponse>> getLeaderboard(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return ApiResponses.ok(collectorService.getLeaderboard(month, year));
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Lịch sử thu gom", description = "Danh sách các task đã xử lý")
    public ApiResponse<List<CollectorWorkHistoryItemResponse>> getWorkHistory(@AuthenticationPrincipal Jwt jwt, @RequestParam(required = false) String status) {
        Integer collectorId = getCollectorId(jwt);
        return ApiResponses.ok(collectorService.getWorkHistory(collectorId, status));
    }

    @PatchMapping("/status")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Cập nhật trạng thái", description = "Chuyển ACTIVE/INACTIVE")
    public ApiResponse<Void> updateMyStatus(@AuthenticationPrincipal Jwt jwt, @RequestBody UpdateCollectorStatusRequest request) {
        Integer collectorId = getCollectorId(jwt);
        collectorService.updateAvailabilityStatus(collectorId, request == null ? null : request.getStatus());
        return ApiResponses.message("Updated");
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Cập nhật hồ sơ Collector", description = "Cập nhật tên, email, phương tiện, biển số")
    public ApiResponse<Collector> updateMyProfile(@AuthenticationPrincipal Jwt jwt,
                                                  @RequestBody UpdateCollectorProfileRequest request) {
        Integer collectorId = getCollectorId(jwt);
        Collector updated = profileService.updateCollectorProfile(collectorId, request);
        return ApiResponses.ok(updated, "Cập nhật hồ sơ thành công");
    }

    @PutMapping("/password")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Đổi mật khẩu", description = "Đổi mật khẩu tài khoản collector hiện tại")
    public ApiResponse<Void> changePassword(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ChangePasswordRequest request) {
        passwordService.changePassword(jwt == null ? null : jwt.getSubject(), request);
        return ApiResponses.message("Đổi mật khẩu thành công");
    }

    private Integer getCollectorId(Jwt jwt) {
        return CollectorJwtSupport.extractCollectorId(jwt);
    }
}

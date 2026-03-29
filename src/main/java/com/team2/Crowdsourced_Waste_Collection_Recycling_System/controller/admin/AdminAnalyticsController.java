package com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller.admin;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller.common.ApiResponses;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.AdminCollectedWeightChartResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.AdminCollectedWeightDailyChartResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.ApiResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.CitizenLeaderboardResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.CollectorLeaderboardResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.AdminAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.AdminSystemAnalyticsResponse;

import java.util.List;

@RestController
@RequestMapping("/api/admin/analytics")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin – Analytics", description = "Phân tích dữ liệu toàn hệ thống")
public class AdminAnalyticsController {

    private final AdminAnalyticsService adminAnalyticsService;

    @GetMapping("/system")
    @Operation(summary = "Thống kê tổng quan toàn hệ thống")
    public ResponseEntity<ApiResponse<AdminSystemAnalyticsResponse>> getSystemAnalytics() {
        AdminSystemAnalyticsResponse result = adminAnalyticsService.getSystemAnalytics();
        return ApiResponses.okEntity(result, "Lấy thống kê hệ thống thành công");
    }

    @GetMapping("/collected-weight")
    @Operation(summary = "Biểu đồ khối lượng rác đã thu gom toàn hệ thống")
    public ResponseEntity<ApiResponse<AdminCollectedWeightChartResponse>> getCollectedWeightChart(
            @RequestParam(name = "year", required = false) Integer year) {
        AdminCollectedWeightChartResponse result = adminAnalyticsService.getCollectedWeightChart(year);
        return ApiResponses.okEntity(result, "Lấy dữ liệu biểu đồ khối lượng rác thành công");
    }

    @GetMapping("/collected-weight/daily")
    @Operation(summary = "Biểu đồ khối lượng rác theo ngày")
    public ResponseEntity<ApiResponse<AdminCollectedWeightDailyChartResponse>> getCollectedWeightDailyChart(
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "month", required = false) Integer month) {
        AdminCollectedWeightDailyChartResponse result = adminAnalyticsService.getCollectedWeightDailyChart(year, month);
        return ApiResponses.okEntity(result, "Lấy dữ liệu biểu đồ khối lượng rác theo ngày thành công");
    }

    @GetMapping("/leaderboard/collectors")
    @Operation(summary = "Bảng xếp hạng Collector theo khối lượng thu gom", 
            description = "Trả về danh sách các Collector có tổng khối lượng rác thu gom cao nhất, sắp xếp giảm dần. Có thể lọc theo ngày, tháng, năm.")
    public ResponseEntity<ApiResponse<List<CollectorLeaderboardResponse>>> getCollectorLeaderboard(
            @RequestParam(name = "day", required = false) Integer day,
            @RequestParam(name = "month", required = false) Integer month,
            @RequestParam(name = "year", required = false) Integer year) {
        List<CollectorLeaderboardResponse> result = adminAnalyticsService.getCollectorLeaderboard(day, month, year);
        return ApiResponses.okEntity(result, "Lấy bảng xếp hạng Collector thành công");
    }

    @GetMapping("/leaderboard/citizens")
    @Operation(summary = "Bảng xếp hạng Citizen theo điểm tích lũy", 
            description = "Trả về danh sách các Citizen có tổng điểm tích lũy cao nhất (chỉ tính điểm kiếm được), sắp xếp giảm dần. Có thể lọc theo ngày, tháng, năm.")
    public ResponseEntity<ApiResponse<List<CitizenLeaderboardResponse>>> getCitizenLeaderboard(
            @RequestParam(name = "day", required = false) Integer day,
            @RequestParam(name = "month", required = false) Integer month,
            @RequestParam(name = "year", required = false) Integer year) {
        List<CitizenLeaderboardResponse> result = adminAnalyticsService.getCitizenLeaderboard(day, month, year);
        return ApiResponses.okEntity(result, "Lấy bảng xếp hạng Citizen thành công");
    }

    @GetMapping("/collected-waste-by-unit")
    @Operation(summary = "Thống kê tổng lượng rác thu gom theo đơn vị (KG, CAN, BOTTLE)")
    public ResponseEntity<ApiResponse<com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.AdminCollectedWasteByUnitResponse>> getCollectedWasteByUnit() {
        com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.AdminCollectedWasteByUnitResponse result = adminAnalyticsService.getCollectedWasteByUnit();
        return ApiResponses.okEntity(result, "Lấy thống kê rác theo đơn vị thành công");
    }
}

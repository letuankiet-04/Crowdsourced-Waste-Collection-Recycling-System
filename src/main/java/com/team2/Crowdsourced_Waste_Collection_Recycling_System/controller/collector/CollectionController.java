package com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller.collector;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.CreateCollectorReportRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.RejectTaskRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.UpdateTaskStatusRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.*;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller.common.ApiResponses;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl.CollectorReportCreationService;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.CollectorReportService;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.CollectorService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

/**
 * Controller dành cho Người thu gom (Collector).
 * Sử dụng @PreAuthorize để đảm bảo chỉ Collector mới có quyền truy cập.
 */
@RestController
@RequestMapping("/api/collector/collections")
@RequiredArgsConstructor
@Validated
@Tag(name = "Collector Collections", description = "Nhiệm vụ và báo cáo của Collector")
public class CollectionController {
    private final CollectorService collectorService;
    private final CollectorReportService collectorReportService;
    private final CollectorReportCreationService collectorReportCreationService;

    /**
     * Lấy danh sách task của collector.
     * - Mặc định: chỉ hiển thị task active (assigned/accepted_collector/on_the_way)
     * - status: lọc theo trạng thái cụ thể
     * - all=true: lấy toàn bộ task
     */
    @GetMapping("/tasks")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Danh sách task", description = "Hiển thị task active theo mặc định; hỗ trợ lọc status hoặc all=true")
    public ApiResponse<java.util.List<CollectorTaskResponse>> getTasks(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "all", required = false, defaultValue = "false") boolean all) {
        Integer collectorId = getCollectorId(jwt);
        return ApiResponses.ok(collectorService.getTasks(collectorId, status, all));
    }

    @GetMapping("/tasks/{requestId}")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Chi tiết task", description = "Xem chi tiết task được Enterprise assign cho Collector hiện tại")
    public ApiResponse<EnterpriseWasteReportResponse> getTaskDetail(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer requestId) {
        Integer collectorId = getCollectorId(jwt);
        return ApiResponses.ok(collectorService.getTaskDetail(collectorId, requestId));
    }

    @GetMapping("/tasks/status_counts")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Đếm task theo trạng thái", description = "Trả về số lượng task của collector theo từng status")
    public ApiResponse<java.util.List<CollectorTaskStatusCountResponse>> getTaskStatusCounts(
            @AuthenticationPrincipal Jwt jwt) {
        Integer collectorId = getCollectorId(jwt);
        return ApiResponses.ok(collectorService.getTaskStatusCounts(collectorId));
    }

    @GetMapping("/work_history")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Lịch sử công việc", description = "Liệt kê lịch sử làm việc, hỗ trợ lọc trạng thái")
    public ApiResponse<java.util.List<CollectorWorkHistoryItemResponse>> getWorkHistory(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(value = "status", required = false) String status) {
        Integer collectorId = getCollectorId(jwt);
        return ApiResponses.ok(collectorService.getWorkHistory(collectorId, status));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Thống kê hiệu suất", description = "Tổng hợp số liệu theo năm của Collector")
    public ApiResponse<CollectorPerformanceStatsResponse> getStats(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(value = "year", required = false) Integer year) {
        Integer collectorId = getCollectorId(jwt);
        return ApiResponses.ok(collectorService.getStats(collectorId, year));
    }

    @GetMapping("/waste-volume")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Thống kê khối lượng rác", description = "Thống kê khối lượng rác đã hoàn tất theo tháng/quý trong năm")
    public ApiResponse<CollectorWasteVolumeStatsResponse> getWasteVolumeStats(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(value = "year", required = false) Integer year) {
        Integer collectorId = getCollectorId(jwt);
        return ApiResponses.ok(collectorService.getWasteVolumeStats(collectorId, year));
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Lịch sử task và report", description = "Danh sách tất cả task và các báo cáo đã tạo")
    public ApiResponse<CollectorHistoryResponse> getHistory(
            @AuthenticationPrincipal Jwt jwt) {
        Integer collectorId = getCollectorId(jwt);
        CollectorHistoryResponse history = CollectorHistoryResponse.builder()
                .tasks(collectorService.getTasks(collectorId, null, true))
                .reports(collectorReportService.getReportsByCollector(collectorId))
                .build();
        return ApiResponses.ok(history);
    }

    /**
     * Collector bắt đầu di chuyển: assigned -> on_the_way.
     */
    @PostMapping("/{requestId}/accept")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Chấp nhận nhiệm vụ", description = "Chuyển ASSIGNED → ACCEPTED_COLLECTOR")
    public ApiResponse<CollectionRequestActionResponse> acceptTask(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer requestId) {
        Integer collectorId = getCollectorId(jwt);
        collectorService.acceptTask(requestId, collectorId);
        return ApiResponses.ok(CollectionRequestActionResponse.builder()
                .collectionRequestId(requestId)
                .status("accepted_collector")
                .actionAt(LocalDateTime.now())
                .build());
    }

    /**
     * Collector bắt đầu di chuyển: accepted_collector -> on_the_way.
     */
    @PostMapping("/{requestId}/start")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Bắt đầu di chuyển", description = "Chuyển ACCEPTED_COLLECTOR → ON_THE_WAY")
    public ApiResponse<CollectionRequestActionResponse> startTask(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer requestId) {
        Integer collectorId = getCollectorId(jwt);
        collectorService.startTask(requestId, collectorId);
        return ApiResponses.ok(CollectionRequestActionResponse.builder()
                .collectionRequestId(requestId)
                .status("on_the_way")
                .actionAt(LocalDateTime.now())
                .build());
    }

    /**
     * Collector từ chối task (chỉ khi đang assigned):
     * - status -> reassign
     * - unassign collector để enterprise phân công lại
     */
    @PostMapping("/{requestId}/reject")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Từ chối nhiệm vụ", description = "Chỉ khi đang ASSIGNED; chuyển về REASSIGN để gán collector khác")
    public ApiResponse<CollectionRequestActionResponse> rejectTask(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer requestId,
            @RequestBody(required = false) RejectTaskRequest request) {
        Integer collectorId = getCollectorId(jwt);
        String reason = request != null ? request.getReason() : null;
        collectorService.rejectTask(requestId, collectorId, reason);
        return ApiResponses.ok(CollectionRequestActionResponse.builder()
                .collectionRequestId(requestId)
                .status("reassign")
                .actionAt(LocalDateTime.now())
                .build());
    }

    /**
     * Collector xác nhận đã thu gom tại điểm:
     * - Cập nhật collection_request.status: on_the_way -> collected
     */
    @PostMapping("/{requestId}/collected")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Đánh dấu đã thu gom", description = "Chuyển ON_THE_WAY → COLLECTED")
    public ApiResponse<CollectionRequestActionResponse> markCollected(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer requestId) {
        Integer collectorId = getCollectorId(jwt);
        collectorService.completeTask(requestId, collectorId);
        return ApiResponses.ok(CollectionRequestActionResponse.builder()
                .collectionRequestId(requestId)
                .status("collected")
                .actionAt(LocalDateTime.now())
                .build());
    }

    /**
     * Cập nhật trạng thái nhiệm vụ (chỉ tiến về phía trước).
     * Hiện tại hỗ trợ: ON_THE_WAY.
     */
    @PatchMapping("/{requestId}/status")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Cập nhật trạng thái", description = "Cập nhật trạng thái nhiệm vụ (chỉ tiến về phía trước)")
    public ApiResponse<CollectionRequestActionResponse> updateStatus(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer requestId,
            @RequestBody UpdateTaskStatusRequest request) {
        Integer collectorId = getCollectorId(jwt);
        collectorService.updateStatus(requestId, collectorId, request.getStatus());
        return ApiResponses.ok(CollectionRequestActionResponse.builder()
                .collectionRequestId(requestId)
                .status(request.getStatus().toLowerCase())
                .actionAt(LocalDateTime.now())
                .build());
    }

    @GetMapping("/{requestId}/create_report")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Dữ liệu tạo report", description = "Lấy WasteReport + danh mục để Collector tạo báo cáo")
    public ApiResponse<ReportCollectorResponse> getCreateReport(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer requestId) {
        Integer collectorId = getCollectorId(jwt);
        return ApiResponses.ok(collectorReportService.getCreateReport(requestId, collectorId));
    }

    @PostMapping(value = "/{requestId}/complete", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Tạo báo cáo thu gom", description = "Nhập khối lượng, ghi chú, GPS và upload ảnh")
    public ApiResponse<CollectorReportResponse> createCollectorReport(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer requestId,
            @Valid @ModelAttribute CreateCollectorReportRequest request) {
        Integer collectorId = getCollectorId(jwt);
        return ApiResponses.ok(collectorReportCreationService.createCollectorReport(requestId, collectorId, request));
    }
    @GetMapping("/{requestId}/report")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Xem report theo yêu cầu", description = "Lấy collector_report gắn với collection request")
    public ApiResponse<CollectorReportResponse> getReportByCollectionRequest(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer requestId) {
        Integer collectorId = getCollectorId(jwt);
        CollectorReportResponse report = collectorReportService.getReportByCollectionRequest(requestId, collectorId);

        if (report == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Report chưa được tạo cho collection request này");
        }

        return ApiResponses.ok(report);
    }

    /**
     * Lấy danh sách report của collector hiện tại.
     */
    @GetMapping("/list_reports")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Danh sách report của tôi", description = "Danh sách báo cáo đã gửi")
    public ApiResponse<java.util.List<CollectorReportResponse>> getMyReports(
            @AuthenticationPrincipal Jwt jwt) {
        Integer collectorId = getCollectorId(jwt);
        return ApiResponses.ok(collectorReportService.getReportsByCollector(collectorId));
    }

    /**
     * Lấy chi tiết report theo reportId (chỉ cho report thuộc collector hiện tại).
     */
    @GetMapping("/reports/{reportId}")
    @PreAuthorize("hasRole('COLLECTOR')")
    @Operation(summary = "Chi tiết report", description = "Lấy chi tiết báo cáo theo reportId (thuộc collector hiện tại)")
    public ApiResponse<CollectorReportResponse> getReportById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer reportId) {
        Integer collectorId = getCollectorId(jwt);
        return ApiResponses.ok(collectorReportService.getReportById(reportId, collectorId));
    }

    private Integer getCollectorId(Jwt jwt) {
        return CollectorJwtSupport.extractCollectorId(jwt);
    }

}

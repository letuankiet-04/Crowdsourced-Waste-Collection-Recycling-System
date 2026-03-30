package com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response preview cho Admin trước khi xóa tài khoản.
 * Hiển thị thông tin user và số lượng dữ liệu liên quan sẽ bị xóa vĩnh viễn.
 */
@Data
@Builder
public class DeleteUserPreviewResponse {
    private Integer userId;
    private String email;
    private String fullName;
    private String roleCode;
    private String status;
    private LocalDateTime createdAt;

    // Số lượng dữ liệu liên quan sẽ bị xóa
    private long wasteReportCount;
    private long feedbackCount;
    private long pointTransactionCount;
    private long collectionRequestCount;
    private long collectorReportCount;
    private long voucherRedemptionCount;
    private long leaderboardEntryCount;
    private long collectorFeedbackCount;

    /**
     * Tổng số bản ghi liên quan sẽ bị xóa.
     */
    public long getTotalRelatedRecords() {
        return wasteReportCount + feedbackCount + pointTransactionCount
                + collectionRequestCount + collectorReportCount
                + voucherRedemptionCount + leaderboardEntryCount
                + collectorFeedbackCount;
    }
}

# Phân Tích Chi Tiết Dự Án Crowdsourced Waste Collection

Tài liệu này bổ sung chi tiết về các module và hiện trạng hệ thống, dựa trên phân tích mã nguồn ngày 12/03/2026.

## 1. Hệ Thống Voucher & Đổi Thưởng (Reward)

Module này cho phép Citizen đổi điểm tích lũy (từ việc thu gom rác) lấy các ưu đãi.

### Cấu trúc
- **Entity**:
  - `Voucher`: Quản lý thông tin ưu đãi (code, points required, stock, valid until).
  - `VoucherRedemption`: Lưu lịch sử đổi mã (citizen_id, voucher_id, redemption_code).
  - `PointTransaction`: Ghi nhận biến động số dư (EARN từ thu gom, SPEND_VOUCHER từ đổi quà).

### Luồng nghiệp vụ (Redeem Flow)
1. **Citizen** gọi `POST /api/citizen/vouchers/redeem/{voucherId}`.
2. **System** (`VoucherServiceImpl`):
   - Validate Voucher: còn hạn (`validUntil`), còn kho (`remainingStock > 0`), trạng thái `ACTIVE`.
   - Validate Citizen: đủ điểm (`totalPoints >= voucher.pointsRequired`).
   - **Locking**: Sử dụng `citizenRepository.findByIdForUpdate` để khóa row Citizen, tránh race condition khi trừ điểm.
   - **Transaction**:
     - Trừ điểm Citizen (`totalPoints`).
     - Trừ kho Voucher (`remainingStock`).
     - Tạo `PointTransaction` (type `SPEND_VOUCHER`).
     - Tạo `VoucherRedemption` (sinh code unique).

## 2. Hệ Thống Tự Động Hóa (Automation)

Sử dụng Spring Scheduler để duy trì vận hành và SLA.

- **Class**: `TaskAutomationServiceImpl`
- **Các tác vụ định kỳ**:
  1. **Timeout Nhận Việc** (`handleAssignedTimeout`):
     - Quét các request `ASSIGNED` quá hạn (config `workrule.accept-timeout-hours`).
     - Xử lý: Hủy assign hiện tại, chuyển về trạng thái chờ Enterprise phân công lại.
  2. **Kiểm Tra SLA** (`checkSlaViolations`):
     - Quét các request đang thực hiện nhưng quá hạn hoàn thành.
     - Xử lý: Ghi nhận vi phạm, có thể tạm khóa (SUSPEND) Collector nếu vượt ngưỡng vi phạm.

## 3. Hiện Trạng Hạ Tầng & Còn Thiếu

### Notification (Thông báo)
- **Trạng thái**: **Chưa triển khai**.
- **Chi tiết**:
  - Không có code gửi Email (SMTP), SMS, hay Push Notification.
  - Các sự kiện (như có request mới, hoàn thành đơn) chỉ được cập nhật trạng thái trong DB, không có cơ chế báo ra ngoài cho người dùng.
  - Từ khóa "Email" trong code chỉ dùng cho định danh/login.

### Containerization (Docker)
- **Trạng thái**: **Chưa sẵn sàng**.
- **Chi tiết**: File `dockerfile` tồn tại nhưng **rỗng (0 bytes)**. Cần viết Dockerfile để đóng gói ứng dụng.

### Testing
- **Trạng thái**: **Rất hạn chế**.
- **Chi tiết**:
  - Folder `src/test` có cấu trúc nhưng các file test (như `...ApplicationTests.java`) rỗng hoặc chỉ có context load cơ bản.
  - Chưa có Unit Test cho Service hay Integration Test cho Controller.
  - Có các file SQL data test (`test_data_collector.sql`) nhưng chưa được gắn vào quy trình test tự động CI/CD.

## 4. Các Điểm Cần Lưu Ý Khi Phát Triển Tiếp

1.  **Triển khai Notification**: Cần thiết để Collector biết khi được assign task, và Enterprise biết khi task hoàn thành. Nên dùng Observer Pattern hoặc Event Listener.
2.  **Viết Dockerfile**: Để chuẩn hóa môi trường deploy.
3.  **Bổ sung Unit Test**: Đặc biệt cho logic tính điểm (`CollectorReportCreationService`) và đổi quà (`VoucherServiceImpl`) vì liên quan đến tài sản (điểm) của người dùng.
4.  **Cấu hình biến môi trường**: Các key của Cloudinary, Database password, JWT Secret đang nằm trong `application.yml`. Cần chuyển sang biến môi trường (`${VAR_NAME}`) để bảo mật khi deploy.

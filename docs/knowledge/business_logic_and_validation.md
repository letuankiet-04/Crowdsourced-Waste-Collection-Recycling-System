# Tài Liệu Kiến Thức Nghiệp Vụ & Logic Code

Tài liệu này đi sâu vào cách **code triển khai** các nghiệp vụ chính, các quy tắc **validation** (kiểm tra hợp lệ) được áp dụng, và logic xử lý giao dịch.

Dành cho: Backend Developer, QC, Mobile Developer (để hiểu flow API).

---

## 1. Nghiệp Vụ: Tạo Báo Cáo Rác (Citizen Create Report)

Người dân chụp ảnh và gửi báo cáo rác thải. Đây là điểm bắt đầu của luồng dữ liệu.

### Logic Code
- **Class chính**: `WasteReportServiceImpl`
- **Method**: `createReport(CreateWasteReportRequest request, MultipartFile[] images, String citizenEmail)`

### Quy Trình Xử Lý & Validation
1.  **Input Validation** (Tầng Controller & Service):
    - **Annotation**: `@Valid` trong Controller kiểm tra các field cơ bản (Not Null).
    - **Logic Code**:
      - `images`: Bắt buộc tối thiểu 1 ảnh.
      - `categoryIds`: Bắt buộc tối thiểu 1 loại rác.
      - `quantities`: Số lượng phải khớp với số category (mảng song song).
      - `latitude/longitude`: Bắt buộc có, nằm trong range hợp lệ (-90 đến 90, -180 đến 180).
2.  **Business Logic Validation** (Tầng Service):
    - **Anti-Spam**:
      - Hệ thống kiểm tra trong **10 phút** gần nhất, tại vị trí đó (bán kính ~30m), user này có tạo báo cáo nào chưa.
      - Nếu có -> Trả lỗi `TOO_MANY_REQUESTS` hoặc thông báo trùng lặp.
    - **Data Integrity**:
      - Kiểm tra `citizenEmail` có tồn tại profile Citizen không.
      - Kiểm tra danh sách `categoryIds` có tồn tại trong DB không.
3.  **Lưu Trữ (Persistence)**:
    - Upload ảnh lên **Cloudinary** (folder `reports`).
    - Lưu `WasteReport` với status `PENDING`.
    - Lưu `WasteReportItem` (snapshot thông tin loại rác tại thời điểm báo cáo).

### Code Reference
- Service: [WasteReportServiceImpl.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/impl/WasteReportServiceImpl.java)
- DTO: [CreateWasteReportRequest.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/dto/request/CreateWasteReportRequest.java)

---

## 2. Nghiệp Vụ: Hoàn Thành Thu Gom & Trả Thưởng (Collector Complete & Reward)

Collector sau khi thu gom xong sẽ chụp ảnh minh chứng và nộp báo cáo. Hệ thống tính điểm và cộng cho Citizen.

### Logic Code
- **Class chính**: `CollectorReportCreationService`
- **Method**: `createCollectorReport(Long requestId, CreateCollectorReportRequest request, ...)`

### Quy Trình Xử Lý & Validation
1.  **Pre-check (Điều kiện tiên quyết)**:
    - Request phải ở trạng thái `COLLECTED` (đã đến nơi thu gom).
    - Request phải thuộc về Collector đang đăng nhập.
    - Request chưa từng có báo cáo (tránh double-submit).
2.  **Location Validation (Chống gian lận)**:
    - Tính khoảng cách giữa **Vị trí báo cáo gốc** và **Vị trí hiện tại của Collector**.
    - Nếu khoảng cách > `workrule.reportRadiusKm` (ví dụ 0.1km) -> Từ chối (Lỗi: "Bạn đang ở quá xa điểm thu gom").
3.  **Weight/Quantity Validation**:
    - Tổng khối lượng thu gom (`totalWeight`) phải > 0.
    - Phải có ảnh minh chứng.
4.  **Reward Transaction (Giao dịch trả thưởng)**:
    - **Locking**: Dùng `citizenRepository.findByIdForUpdate(citizenId)` để khóa row Citizen (Pessimistic Write).
    - **Idempotency**: Kiểm tra bảng `PointTransaction` xem đã có giao dịch `EARN` cho request này chưa. Nếu có rồi -> Bỏ qua cộng điểm (tránh cộng 2 lần do mạng lag).
    - **Calculate**:
      - Điểm = `Tổng (Số lượng item * Điểm/đơn vị của category)`.
    - **Update**:
      - Cộng `citizen.totalPoints`.
      - Insert `PointTransaction`.
      - Update `CollectionRequest` -> `COMPLETED`.
      - Update `WasteReport` -> `COLLECTED`.

### Code Reference
- Service: [CollectorReportCreationService.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/impl/CollectorReportCreationService.java)
- Repository Lock: [CitizenRepository.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/repository/profile/CitizenRepository.java)

---

## 3. Nghiệp Vụ: Đổi Điểm Lấy Voucher (Redeem Voucher)

Citizen dùng điểm tích lũy đổi lấy mã giảm giá/quà tặng.

### Logic Code
- **Class chính**: `VoucherServiceImpl`
- **Method**: `redeem(Long voucherId, String citizenEmail)`

### Quy Trình Xử Lý & Validation
1.  **Voucher Validation**:
    - `voucher.isActive()` == true.
    - `voucher.validUntil` >= ngày hiện tại.
    - `voucher.remainingStock` > 0.
2.  **Citizen Validation**:
    - `citizen.totalPoints` >= `voucher.pointsRequired`.
3.  **Concurrency Handling (Xử lý đồng thời)**:
    - Tương tự Reward, dùng **Locking** trên Citizen và Voucher để đảm bảo không bị âm kho hoặc âm điểm khi nhiều người đổi cùng lúc.
    - **Transaction**:
      - Trừ điểm Citizen.
      - Trừ Stock Voucher.
      - Tạo `VoucherRedemption` với code unique.
      - Tạo `PointTransaction` loại `SPEND_VOUCHER`.

### Code Reference
- Service: [VoucherServiceImpl.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/impl/VoucherServiceImpl.java)
- Entity: [Voucher.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/entity/Voucher.java)

---

## 4. Nghiệp Vụ: Tự Động Hóa & SLA (Automation)

Hệ thống chạy ngầm để đảm bảo đơn hàng không bị treo.

### Logic Code
- **Class chính**: `TaskAutomationServiceImpl`
- **Trigger**: `@Scheduled` (cron job).

### Quy Tắc Xử Lý
1.  **Timeout Nhận Việc**:
    - Quét đơn `ASSIGNED` quá `X` giờ (config).
    - Action: Hủy assign, log tracking, notify (nếu có).
2.  **SLA Vi Phạm**:
    - Quét đơn đang làm quá `Y` giờ.
    - Action: Tăng đếm `slaViolations` của Collector.
    - Rule: Nếu vi phạm > `Z` lần -> Set status Collector = `SUSPEND` (Tạm khóa).

### Code Reference
- Service: [TaskAutomationServiceImpl.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/impl/TaskAutomationServiceImpl.java)
- Config: [WorkRuleProperties.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/config/WorkRuleProperties.java)

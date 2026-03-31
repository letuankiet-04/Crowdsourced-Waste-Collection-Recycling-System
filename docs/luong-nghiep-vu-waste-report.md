# Luồng nghiệp vụ: WasteReport, CollectorReport, Update Status

Tài liệu này mô tả “luồng nghiệp vụ theo code hiện tại” của 3 phần:
- WasteReport (báo cáo rác) do Citizen tạo
- CollectionRequest (yêu cầu thu gom) do Enterprise tạo và giao cho Collector
- CollectorReport (báo cáo thu gom) do Collector tạo sau khi thu gom

## 1) Entity & trạng thái chính

### WasteReport
- Entity: [WasteReport.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/entity/WasteReport.java)
- Enum status: [WasteReportStatus.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/enums/WasteReportStatus.java)

Chuỗi trạng thái thường gặp theo luồng:
- `PENDING → ACCEPTED_ENTERPRISE → ASSIGNED → ACCEPTED_COLLECTOR → ON_THE_WAY → COLLECTED`

Nhánh ngoại lệ:
- Enterprise từ chối: `PENDING → REJECTED`
- Collector từ chối nhiệm vụ: `ASSIGNED → REASSIGN` (để Enterprise gán người khác)

### CollectionRequest
- Entity: [CollectionRequest.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/entity/CollectionRequest.java)
- Enum status: [CollectionRequestStatus.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/enums/CollectionRequestStatus.java)

Chuỗi trạng thái thường gặp theo luồng:
- `ACCEPTED_ENTERPRISE → ASSIGNED → ACCEPTED_COLLECTOR → ON_THE_WAY → COLLECTED → COMPLETED`

### CollectorReport
- Entity: [CollectorReport.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/entity/CollectorReport.java)
- Enum status: [CollectorReportStatus.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/enums/CollectorReportStatus.java)

Theo code hiện tại, khi Collector tạo report thì `CollectorReport.status = COMPLETED`.
- Tạo CollectorReport: [CollectorReportCreationService.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/impl/CollectorReportCreationService.java)

## 2) Luồng WasteReport (Citizen)

### 2.1 Tạo báo cáo rác
- API: `POST /api/citizen/reports` ở [CitizenController.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/citizen/CitizenController.java)
- Logic service: [WasteReportServiceImpl.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/impl/WasteReportServiceImpl.java)

Các bước chính:
- Validate dữ liệu đầu vào + lấy list categoryIds
- Làm tròn toạ độ 8 chữ số thập phân
- Chặn spam: nếu có report gần-trùng vị trí trong 10 phút thì báo lỗi
- Tạo WasteReport mới, set `status = PENDING`
- Lưu report, rồi chuẩn hoá `reportCode` theo ID (ví dụ `WR001`)
- Lưu WasteReportItem theo từng loại rác và quantity
- Upload ảnh, lưu ReportImage

### 2.2 Sửa báo cáo rác
- API: `PUT /api/citizen/reports/{id}`
- Chỉ được sửa khi `WasteReport.status == PENDING`
- Có thể sửa mô tả/toạ độ/địa chỉ, và thay danh sách loại rác/ảnh (nếu gửi lên)

Điểm chặn quan trọng:
- Nếu status khác `PENDING` thì không cho sửa: xem [WasteReportServiceImpl.updateReport](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/impl/WasteReportServiceImpl.java)

### 2.3 Huỷ báo cáo rác
- API: `DELETE /api/citizen/reports/{id}`
- Chỉ được huỷ khi `WasteReport.status == PENDING`
- Và chưa có CollectionRequest liên quan (đã được Enterprise xử lý) thì không huỷ được

## 3) Luồng Enterprise xử lý WasteReport → tạo CollectionRequest

### 3.1 Accept WasteReport (tạo CollectionRequest)
- API: `POST /api/enterprise/requests/accept/{reportCode}` ở [EnterpriseController.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/enterprise/EnterpriseController.java)
- Service: [EnterpriseRequestServiceImpl.acceptWasteReport](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/impl/EnterpriseRequestServiceImpl.java)

Điều kiện & hành vi:
- Nếu report đang `PENDING`:
  - Check report có nằm trong khu vực phục vụ (service ward/city) của enterprise
  - Chống trùng: nếu đã có CollectionRequest theo report_id thì trả về requestId cũ
  - Cập nhật `WasteReport.status = ACCEPTED_ENTERPRISE`, set `acceptedAt`
  - Tạo `CollectionRequest.status = ACCEPTED_ENTERPRISE`
  - Chuẩn hoá `requestCode` theo format `CR%03d` sau khi có ID
- Nếu report đang `ACCEPTED_ENTERPRISE`:
  - Trả về requestId hiện có (hoặc tự tạo mới nếu dữ liệu cũ bị lệch)
- Nếu report ở trạng thái khác:
  - Báo lỗi “không ở trạng thái hợp lệ để accept”

### 3.2 Reject WasteReport
- API: `POST /api/enterprise/requests/reject/{reportCode}` ở [EnterpriseController.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/enterprise/EnterpriseController.java)
- Service: [EnterpriseRequestServiceImpl.rejectWasteReport](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/impl/EnterpriseRequestServiceImpl.java)

Hành vi:
- Set `WasteReport.status = REJECTED` + lưu `rejectionReason`

## 4) Luồng assign/reassign Collector (Enterprise)

- API assign theo request:
  - `POST /api/enterprise/requests/{requestId}/assign-collector` ở [EnterpriseController.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/enterprise/EnterpriseController.java)
- API assign theo reportCode (1 bước: accept nếu chưa có request rồi assign):
  - `POST /api/enterprise/requests/reports/{reportCode}/assign-collector` ở [EnterpriseController.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/enterprise/EnterpriseController.java)
- Service: [EnterpriseAssignmentServiceImpl](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/impl/EnterpriseAssignmentServiceImpl.java)

Điều kiện & hành vi:
- Chỉ cho gán khi `CollectionRequest.status ∈ {ACCEPTED_ENTERPRISE, REASSIGN}`.
- Nếu request đang `REASSIGN` thì không được gán lại đúng collector vừa từ chối (dò tracking action `rejected`).
- Assign thực hiện bằng update DB “atomic” (đổi `CollectionRequest.status = ASSIGNED`, set collector_id, assignedAt): [CollectionRequestRepository.assignCollector](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/repository/collector/CollectionRequestRepository.java)
- Sau khi assign thành công thì đồng bộ `WasteReport.status = ASSIGNED` và ghi tracking action `assigned`.

## 5) Luồng update status nhiệm vụ (Collector)

API nằm trong [CollectionController.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/collector/CollectionController.java), service nằm ở [CollectorServiceImpl.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/impl/CollectorServiceImpl.java).

Các bước trạng thái:

### 5.1 Accept task
- API: `POST /api/collector/collections/{requestId}/accept`
- DB: `ASSIGNED → ACCEPTED_COLLECTOR`
- Đồng bộ report: `WasteReport.status = ACCEPTED_COLLECTOR`

### 5.2 Start task
- API: `POST /api/collector/collections/{requestId}/start`
- DB: `ACCEPTED_COLLECTOR → ON_THE_WAY`
- Đồng bộ report: `WasteReport.status = ON_THE_WAY`

### 5.3 Reject task (trả về REASSIGN)
- API: `POST /api/collector/collections/{requestId}/reject`
- Điều kiện: chỉ khi request đang `ASSIGNED`
- DB: `ASSIGNED → REASSIGN`, đồng thời bỏ gán collector + lưu lý do từ chối
- Đồng bộ report: `WasteReport.status = REASSIGN`

### 5.4 Mark collected
- API: `POST /api/collector/collections/{requestId}/collected`
- DB: `ON_THE_WAY → COLLECTED`
- Đồng bộ report: `WasteReport.status = COLLECTED`

### 5.5 Endpoint updateStatus (hiện chỉ hỗ trợ ON_THE_WAY)
- API: `PATCH /api/collector/collections/{requestId}/status`
- Service `updateStatus(...)` chỉ nhận `"ON_THE_WAY"` và gọi `startTask(...)`: [CollectorServiceImpl.updateStatus](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/impl/CollectorServiceImpl.java)

## 6) Luồng tạo CollectorReport (Collector)

### 6.1 Lấy dữ liệu tạo report
- API: `GET /api/collector/collections/{requestId}/create_report`
- Service: `collectorReportService.getCreateReport(...)` (trả về ReportCollectorResponse)

### 6.2 Tạo báo cáo thu gom (complete)
- API: `POST /api/collector/collections/{requestId}/complete` (multipart/form-data)
- Service chính: [CollectorReportCreationService.createCollectorReport](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/impl/CollectorReportCreationService.java)

Điều kiện bắt buộc trước khi tạo:
- CollectionRequest phải thuộc collector hiện tại, và `CollectionRequest.status == COLLECTED`
- Chưa tồn tại CollectorReport cho requestId (chống tạo trùng)
- Bắt buộc có ít nhất 1 ảnh
- Bắt buộc có categoryIds và quantities (kích thước khớp nhau)
- Bắt buộc có GPS (lat/lng) trong request
- GPS lúc báo cáo thu gom phải cách vị trí WasteReport ban đầu ≤ 30m (check Haversine)

Các bước chính khi tạo:
- Tạo `CollectorReport.status = COMPLETED`, set collectedAt, lat/lng, note; chuẩn hoá `reportCode` theo `CR%03d`
- Tạo CollectorReportItem:
  - snapshot unit + pointPerUnit từ WasteCategory
  - lưu quantity, totalPoint ban đầu = 0 (chưa thưởng điểm)
- Upload ảnh lên Cloudinary, lưu CollectorReportImage
- Xác nhận hoàn tất CollectionRequest:
  - `CollectionRequest.status: COLLECTED → COMPLETED`
  - lưu `actualWeightKg` = tổng quantity (scale 2)
  - xem [CollectionRequestRepository.confirmCompletedWithWeight](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/repository/collector/CollectionRequestRepository.java)
- Đồng bộ WasteReport:
  - service set `WasteReport.status = COLLECTED` (WasteReport không có trạng thái COMPLETED)

## 7) Luồng Enterprise reward điểm theo CollectorReport (xác thực)

- API: `POST /api/enterprise/collector-reports/{id}/reward` ở [EnterpriseCollectorReportController.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/enterprise/EnterpriseCollectorReportController.java)
- Service: [EnterpriseCollectorReportServiceImpl.reward](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/impl/EnterpriseCollectorReportServiceImpl.java)

Điều kiện & hành vi:
- Chỉ enterprise chủ của collectionRequest (qua collectorReport) mới được reward
- Chống cộng điểm 2 lần: nếu đã tồn tại PointTransaction type `EARN` theo collectionRequestId thì báo lỗi conflict
- Lấy items của CollectorReport:
  - `basePoints = quantity * pointPerUnitSnapshot`
  - `adjusted = basePoints * (verificationRate / 100)`
  - lưu lại `CollectorReportItem.totalPoint = adjusted`
- Tổng điểm earnedPoints:
  - set `CollectorReport.totalPoint = earnedPoints`
  - cộng dồn vào `Citizen.totalPoints` (lock bằng `findByIdForUpdate`)
  - tạo `PointTransaction` (type `EARN`) gắn citizen + collectionRequest + wasteReport

## 8) Luồng xem kết quả & xem CollectorReport (Citizen)

### 8.1 Xem “kết quả xử lý” báo cáo rác
- API: `GET /api/citizen/reports/{id}/result` ở [CitizenController.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/citizen/CitizenController.java)
- Service: [WasteReportServiceImpl.getMyReportResult](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/impl/WasteReportServiceImpl.java)

Logic tính điểm hiển thị:
- Nếu chưa có CollectionRequest theo report_id → trả về totalPoint = 0
- Nếu có request nhưng chưa có CollectorReport → totalPoint = 0 (có thể trả collectedAt theo request)
- Nếu có CollectorReport:
  - lấy `CollectorReport.totalPoint`
  - nếu `totalPoint == 0` thì thử lấy từ `PointTransaction` theo collectionRequestId (lấy bản ghi cuối)
  - `classificationResult = CORRECT` nếu totalPoint > 0, ngược lại `INCORRECT`

### 8.2 Xem “collector report” theo WasteReport
- API: `GET /api/citizen/reports/{id}/collector-report` ở [CitizenController.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/citizen/CitizenController.java)
- Service: [WasteReportServiceImpl.getCollectorReportByWasteReportId](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/impl/WasteReportServiceImpl.java)

Logic:
- Bắt buộc report thuộc về citizen hiện tại (ownership)
- Tìm CollectionRequest theo report_id, rồi tìm CollectorReport theo collectionRequestId (report mới nhất)
- Trả về:
  - ảnh: `CollectorReportImage` theo collectorReportId
  - danh mục/khối lượng: gom nhóm `CollectorReportItem` theo wasteCategoryId

## 9) Mapping status hiển thị cho Citizen

Trong màn Citizen, code map `WasteReportStatus` về chuỗi hiển thị riêng:
- Hàm map: [WasteReportServiceImpl.mapCitizenStatus](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/impl/WasteReportServiceImpl.java)

Quy ước theo code:
- `PENDING` → `PENDING`
- `ACCEPTED_ENTERPRISE` → `ACCEPTED`
- `ASSIGNED` hoặc `ACCEPTED_COLLECTOR` → `ASSIGNED`
- `ON_THE_WAY` → `ON THE WAY`
- `COLLECTED` → `COLLECTED`
- `REASSIGN` → `REASSIGN`
- `REJECTED` → `REJECTED`
- `TIMED_OUT` → `TIMED_OUT`

## 10) Phân biệt “collected” và “complete” (2 bước của Collector)

Trong API Collector có 2 thao tác khác nhau:
- `POST /api/collector/collections/{requestId}/collected`
  - Ý nghĩa: đánh dấu “đã thu gom xong” ở mức nhiệm vụ
  - Chuyển `CollectionRequest: ON_THE_WAY → COLLECTED`
- `POST /api/collector/collections/{requestId}/complete` (tạo CollectorReport)
  - Ý nghĩa: gửi “báo cáo thu gom” (khối lượng, ảnh, GPS, ghi chú) và chốt task
  - Điều kiện: chỉ cho tạo report khi `CollectionRequest.status == COLLECTED`
  - Chuyển `CollectionRequest: COLLECTED → COMPLETED` (có `actualWeightKg`)

Lưu ý đồng bộ:
- WasteReport không có trạng thái `COMPLETED`, nên sau bước create report, WasteReport vẫn là `COLLECTED`.

## 11) Các màn/endpoint xem dữ liệu cho Enterprise/Collector/Admin

### 11.1 Enterprise xem WasteReport theo khu vực hoạt động
- API:
  - `GET /api/enterprise/waste-reports` (lọc status tuỳ chọn) [EnterpriseWasteReportController.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/enterprise/EnterpriseWasteReportController.java)
  - `GET /api/enterprise/waste-reports/pending` [EnterpriseWasteReportController.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/enterprise/EnterpriseWasteReportController.java)
  - `GET /api/enterprise/waste-reports/{id}` [EnterpriseWasteReportController.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/enterprise/EnterpriseWasteReportController.java)
- Service: [EnterpriseWasteReportServiceImpl](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/impl/EnterpriseWasteReportServiceImpl.java)

Logic lọc:
- Dù query theo status hay không, service vẫn lọc lại theo `AddressMatchUtil.isInServiceArea(...)` của enterprise.

Lưu ý:
- Trong code có `acceptReport/rejectReport` ở EnterpriseWasteReportService, nhưng luồng nghiệp vụ chuẩn (có tạo CollectionRequest) đang đi qua [EnterpriseRequestServiceImpl.acceptWasteReport](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/impl/EnterpriseRequestServiceImpl.java).

### 11.2 Collector xem task/report
- Task endpoints: [CollectionController.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/collector/CollectionController.java)
  - danh sách task: `GET /api/collector/collections/tasks`
  - chi tiết task: `GET /api/collector/collections/tasks/{requestId}`
- Report endpoints: [CollectionController.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/collector/CollectionController.java)
  - dữ liệu tạo report: `GET /api/collector/collections/{requestId}/create_report`
  - tạo report: `POST /api/collector/collections/{requestId}/complete`
  - xem report theo request: `GET /api/collector/collections/{requestId}/report`

### 11.3 Admin xem tổng hợp báo cáo
- API: [AdminReportController.java](file:///d:/Documents/SWP/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/admin/AdminReportController.java)
  - `GET /api/admin/reports/waste-reports` (lọc enterpriseId/status)
  - `GET /api/admin/reports/collector` (lọc enterpriseId)

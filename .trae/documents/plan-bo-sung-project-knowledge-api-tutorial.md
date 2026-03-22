## Tóm tắt

Bổ sung tài liệu trong `project-knowledge.html` theo hướng “tutorial cho dev mới”: mỗi API được mô tả rõ **làm gì**, **đụng bảng/Entity nào**, **validate gì** (Bean Validation + rule ở service), và **đang dùng kiến thức/pattern gì** (JWT, phân quyền, JPA projection, EntityGraph, Cloudinary, transaction, scheduler…).

---

## Phân tích trạng thái hiện tại (grounded)

- File tài liệu hiện có: `project-knowledge.html` ở root repo.
- Nội dung đã có:
  - Tổng quan stack/module/validate/security.
  - Section `5.x. Bảng tổng hợp Endpoint toàn hệ thống` đã liệt kê hầu hết endpoint theo module, nhưng:
    - Nhiều endpoint chỉ có 1 dòng (thiếu “bảng nào”, “validate gì”, “pattern gì”).
    - Một số validate được mô tả ở chỗ khác (mục 3, 6…) nhưng chưa “gắn” vào từng API để đọc theo kiểu tutorial.
- Backend là Spring Boot, mapping endpoint nằm ở các controller dưới `src/main/java/.../controller/**`.
- DB là code-first JPA: mapping bảng nằm ở các entity `src/main/java/.../entity/**` (annotation `@Table(name="...")`).
- Bean Validation nằm chủ yếu ở các request DTO `src/main/java/.../dto/request/**` (annotation như `@NotNull`, `@NotBlank`, `@Size`, `@DecimalMin/@DecimalMax`, `@Min/@Max`, `@Positive`).

---

## Thay đổi đề xuất (cụ thể theo file)

### 1) Cập nhật `project-knowledge.html`: chuẩn hoá format “API tutorial”

**Mục tiêu:** Biến phần `5.x` thành nơi dev có thể tra cứu theo API và hiểu ngay:
1) API làm gì trong nghiệp vụ, 2) chạm DB ở đâu, 3) validate/rule nào quan trọng, 4) kiến thức/pattern nào cần biết để đọc code.

**Cách làm (format thống nhất cho từng endpoint):**
- Giữ nguyên cách chia module hiện có (Auth/Citizen/Collector/Enterprise/Admin/Files).
- Với mỗi endpoint trong `5.x.*`, đổi từ bullet 1-dòng sang block chuẩn hoá theo template sau (ưu tiên dùng `<details>` để file không quá dài khi mở):
  - **Summary (dòng tóm tắt)**: `METHOD /path — mục đích`
  - **Controller/Service**: tên method/controller/service đã có trong doc (cập nhật nếu lệch code).
  - **Bảng/Entity liên quan**: liệt kê entity → tên bảng (ví dụ `WasteReport → waste_reports`), và mối quan hệ join chính nếu có (ví dụ `CollectionRequest → WasteReport (report_id)`).
  - **Validate**:
    - Bean Validation (nếu controller dùng `@Valid` + DTO nào).
    - Validate nghiệp vụ ở service (ownership, state machine, range year, chống spam, unique…).
  - **Kiến thức/pattern sử dụng** (liệt kê tag + 1 câu “vì sao”):
    - JWT/roles/`@PreAuthorize`
    - Denylist token (`invalidated_tokens`)
    - JPA Projection / Interface-based projection
    - `@EntityGraph` tránh N+1
    - JPQL update chống race condition
    - Upload ảnh Cloudinary + nén ảnh
    - Aggregate SQL (SUM/COUNT/GROUP BY)
    - Scheduler/SLA job (nếu endpoint liên quan trạng thái bị job tác động)

**Nguồn dữ liệu để điền (grounded theo repo):**
- Endpoint & controller: `src/main/java/.../controller/**`
- Service flow: `src/main/java/.../service/impl/**`
- Bảng/Entity: `src/main/java/.../entity/**` (đọc `@Table(name=...)`)
- Bean Validation: `src/main/java/.../dto/request/**`

**Phạm vi điền chi tiết:**
- Điền chi tiết cho **toàn bộ endpoint đang liệt kê ở `5.x.*`**.
- Nếu phát hiện endpoint trong code nhưng chưa có trong `5.x`, sẽ bổ sung thêm mục tương ứng (để tài liệu “cover” đúng code).
- Nếu phát hiện endpoint trong `5.x` nhưng không tồn tại trong code (đổi path/method), sẽ chỉnh lại theo code hiện tại.

### 2) Thêm “Tutorial: cách trace 1 API trong dự án” vào `project-knowledge.html`

**Mục tiêu:** Dev mới đọc xong là tự lần được code.

Nội dung tutorial đề xuất (ngắn gọn, thực hành được):
- Bước 1: Tìm endpoint ở controller (`@RequestMapping/@GetMapping/...`).
- Bước 2: Xác định quyền truy cập (role/`@PreAuthorize`/SecurityConfig).
- Bước 3: Xem request DTO + Bean Validation (nếu có `@Valid`).
- Bước 4: Theo service để hiểu rule nghiệp vụ (ownership, state machine, spam check, SLA…).
- Bước 5: Xác định bảng/Entity (đọc `@Table`, quan hệ `@ManyToOne/@OneToMany`).
- Bước 6: Kiểm tra tối ưu hiệu năng (projection/entity graph/aggregate query).
- Bước 7: Kiểm tra xử lý lỗi (AppException/ErrorCode) và response mapping (Mapper/DTO).

---

## Quyết định & giả định

- Tài liệu hướng tới **developer onboarding** (tutorial + tra cứu), nên ưu tiên “đọc nhanh hiểu ngay” hơn là liệt kê full code.
- “Nối với bảng gì” sẽ được diễn giải theo **Entity → tên bảng** (từ `@Table(name="...")`) và các quan hệ FK tiêu biểu (theo `@JoinColumn`).
- “Validate gì” sẽ bao gồm cả:
  - Bean Validation (annotation trong DTO khi endpoint dùng `@Valid`),
  - Validate nghiệp vụ trong service (kể cả validate thủ công).
- “Sử dụng kiến thức gì” sẽ được chuẩn hoá thành một nhóm tag/pattern lặp lại để tránh viết lan man.

---

## Các bước kiểm chứng (sau khi triển khai)

- Mở `project-knowledge.html` trên browser để kiểm tra format (đặc biệt `<details>`/`<table>` nếu dùng).
- Đối chiếu endpoint:
  - So sánh danh sách `5.x` với mapping thực tế trong `controller/**` để chắc chắn không thiếu/không sai path.
- Spot-check ngẫu nhiên 5–10 endpoint:
  - Endpoint có đúng entity/bảng liên quan.
  - Validate có đúng DTO annotation và rule trong service.
  - Tags “kiến thức/pattern” phản ánh đúng cách implement.

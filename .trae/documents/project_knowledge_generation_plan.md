# Kế hoạch Tạo File Tổng Hợp Kiến Thức Dự Án (Project Knowledge Summary)

Mục tiêu: Tạo một file HTML (`project_knowledge.html`) tổng hợp toàn bộ kiến thức, công nghệ, logic validation và các chức năng đã được triển khai trong dự án Crowdsourced Waste Collection Recycling System.

## Các bước thực hiện

### 1. Phân tích và Thu thập Thông tin
-   **Phân tích Tech Stack**:
    -   Đọc file `pom.xml` để liệt kê các thư viện và framework sử dụng (Spring Boot, Database driver, Security, Utils...).
-   **Phân tích Logic Validation trong Service**:
    -   Quét thư mục `src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/impl`.
    -   Trích xuất các đoạn code validation (các câu lệnh `if`, `throw new AppException`, kiểm tra null, kiểm tra trạng thái logic).
    -   Ghi chú lại tên method và mục đích của validation.
-   **Phân tích Chức năng và Luồng Code**:
    -   Quét thư mục `controller` để liệt kê các API endpoints.
    -   Map từng Controller với Service tương ứng để mô tả luồng xử lý (Controller -> Service -> Repository).
    -   Phân nhóm theo module chức năng:
        -   Authentication & Authorization (SecurityConfig, JWT).
        -   Quản lý Người dùng (User, Citizen, Collector, Enterprise).
        -   Quản lý Rác thải & Thu gom (WasteReport, CollectionRequest).
        -   Hệ thống Điểm thưởng & Voucher (Point, Voucher).
-   **Phân tích Configuration & Utilities**:
    -   Tổng hợp các cấu hình quan trọng (`SecurityConfig`, `CloudinaryConfig`, `SwaggerConfig`).
    -   Các utility class (`JWTHelper`, `FileUpLoadUtil`).

### 2. Cấu trúc File HTML
File HTML sẽ được chia thành các phần chính sau:
-   **Tổng quan Dự án (Project Overview)**: Giới thiệu ngắn gọn và Tech Stack.
-   **Kiến trúc & Cấu hình (Architecture & Configuration)**:
    -   Cấu trúc thư mục.
    -   Cấu hình Security (JWT, Role-based access).
    -   Exception Handling (Global Exception Handler).
-   **Chi tiết Chức năng & Logic Code (Functional Modules)**:
    -   Phân chia theo từng Actor (Admin, Citizen, Collector, Enterprise) hoặc Feature.
    -   Mô tả chức năng -> Code Service tương ứng -> Code Controller tương ứng.
-   **Tổng hợp Validation (Service Validations)**:
    -   Bảng liệt kê các Class Service.
    -   Danh sách các điều kiện validate chi tiết trong từng hàm.
-   **Database & Entity**:
    -   Danh sách các Entity và mối quan hệ.

### 3. Tạo nội dung và Viết File
-   Sử dụng thông tin thu thập được để soạn thảo nội dung HTML.
-   Sử dụng Bootstrap hoặc CSS đơn giản để trình bày đẹp, dễ đọc.
-   Tạo file `project_knowledge.html` tại thư mục gốc của dự án.

### 4. Kiểm tra và Hoàn thiện
-   Review lại file HTML sinh ra để đảm bảo đầy đủ thông tin yêu cầu:
    -   Tech stack.
    -   Validate trong service.
    -   Mapping chức năng và code.

# Crowdsourced Waste Collection & Recycling System

Hệ thống thu gom và tái chế rác thải dựa trên cộng đồng (Crowdsourced Waste Collection & Recycling System) là một giải pháp full-stack toàn diện dành cho việc quản lý, thu gom và tái chế rác thải đô thị tại Việt Nam. 

Dự án được xây dựng với **Spring Boot** cho backend, tích hợp bản đồ OpenStreetMap để báo cáo vị trí rác thải dựa trên GPS, hỗ trợ điều phối thu gom rác, và giám sát minh bạch nhằm đáp ứng các quy định bắt buộc về phân loại rác thải.

## 🌟 Tính Năng Chính

- **Quản lý đa vai trò (Role-based Access Control):** 
  - **Admin:** Quản lý toàn bộ hệ thống, danh mục rác thải, tài khoản, khiếu nại và luật cộng điểm.
  - **Enterprise (Doanh nghiệp):** Quản lý thu gom, báo cáo từ Collector, đánh giá, thống kê khối lượng rác, và tạo Voucher.
  - **Collector (Người thu gom):** Nhận nhiệm vụ thu gom, cập nhật trạng thái thu gom, chụp ảnh báo cáo, xem bảng xếp hạng và lịch sử công việc.
  - **Citizen (Người dân):** Báo cáo rác thải, theo dõi trạng thái thu gom, tích lũy điểm thưởng, đổi Voucher và xem bảng xếp hạng.
- **Xác thực điện tử (VNPT eKYC):** Tích hợp eKYC của VNPT để xác minh danh tính người dùng một cách chính xác và bảo mật (nhận diện CCCD/CMND, khuôn mặt liveness).
- **Lưu trữ đám mây (Cloudinary):** Quản lý và lưu trữ hình ảnh tải lên (hình ảnh rác thải, ảnh báo cáo hoàn thành nhiệm vụ, avatar...).
- **Hệ thống Điểm & Phần thưởng (Points & Vouchers):** Người dân được cộng điểm khi phân loại và báo cáo rác đúng quy định, có thể dùng điểm để đổi các Voucher từ doanh nghiệp.
- **Bảo mật & Xác thực (JWT):** Sử dụng Spring Security và OAuth2 Resource Server để bảo mật API với chuẩn JSON Web Token (JWT).
- **Định tuyến & Vị trí:** Tích hợp GPS/OpenStreetMap để theo dõi, giao việc và tối ưu hóa tuyến đường thu gom theo bán kính (`reassign-radius-km`).
- **Tài liệu API tự động:** Cung cấp tài liệu API trực quan với Swagger/OpenAPI.

## 🚀 Công Nghệ Sử Dụng

- **Ngôn ngữ:** Java 21
- **Framework:** Spring Boot 3.4.1
- **Cơ sở dữ liệu:** MySQL (Spring Data JPA, Hibernate)
- **Bảo mật:** Spring Security, OAuth2 (JWT)
- **Mapper/DTO:** MapStruct, ModelMapper
- **Tiện ích:** Lombok
- **Tích hợp bên thứ 3:** 
  - [Cloudinary](https://cloudinary.com/) (Lưu trữ ảnh)
  - VNPT eKYC API (Định danh điện tử)
- **Tài liệu API:** Springdoc OpenAPI (Swagger UI)

## 🛠 Yêu Cầu Hệ Thống

- [JDK 21](https://jdk.java.net/21/) trở lên.
- [Maven](https://maven.apache.org/) 3.8+ (hoặc có thể dùng Maven Wrapper `mvnw` đi kèm dự án).
- Cơ sở dữ liệu [MySQL](https://www.mysql.com/) 8.x.

## 🔍 Tích Hợp VNPT eKYC

Dự án có tích hợp hệ thống định danh điện tử VNPT eKYC để xác thực danh tính người dùng (CCCD/CMND) và chống giả mạo khuôn mặt (Liveness) trong quá trình đăng ký. 

- **Endpoint kiểm tra nhanh:** Base path `/api/auth/ekyc/flow`.
- Khi người dùng hoàn thành eKYC, backend sẽ bóc tách các thông tin từ CCCD để tự động điền vào form đăng ký (tên, số CCCD, ngày sinh...).

## 📚 Tài Liệu API (Swagger)

Sau khi ứng dụng chạy thành công (mặc định ở port `8080`), bạn có thể truy cập tài liệu API tự động tại:
- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## 📁 Cấu Trúc Dự Án (Project Structure)

```
src/main/
├── java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/
│   ├── config/         # Cấu hình Security, Cloudinary, Swagger, eKYC...
│   ├── controller/     # Các API Endpoints phân chia theo vai trò (admin, citizen, collector, enterprise...)
│   ├── dto/            # Data Transfer Objects (Request/Response)
│   ├── entity/         # Các Entity mapping với database (JPA)
│   ├── enums/          # Các tập hợp hằng số Enum (Status, Role...)
│   ├── exception/      # Xử lý ngoại lệ toàn cục (Global Exception Handler)
│   ├── integration/    # Tích hợp API bên thứ 3 (VNPT eKYC Client)
│   ├── mapper/         # MapStruct/ModelMapper interfaces
│   ├── repository/     # Spring Data JPA Repositories truy xuất DB
│   ├── service/        # Chứa Business Logic xử lý của hệ thống
│   └── util/           # Các lớp tiện ích dùng chung (JWT, Upload, Matcher...)
└── resources/
    └── application.yml # File cấu hình chính của ứng dụng (database, jwt, server...)
```

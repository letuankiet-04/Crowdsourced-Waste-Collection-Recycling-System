# Hướng dẫn cấu hình (Spring Boot)

Tài liệu này giải thích các cấu hình runtime của backend, tập trung vào `src/main/resources/application.yml` và cách dùng profile/env để chạy theo môi trường.

## 1) File cấu hình chính

- File: `src/main/resources/application.yml`
- Cấu trúc:
  - Block mặc định (không profile): cấu hình chung cho mọi môi trường.
  - Block theo profile:
    - `spring.config.activate.on-profile: dev`
    - `spring.config.activate.on-profile: prod`

Kích hoạt profile bằng biến môi trường:

- Windows PowerShell:
  - `$env:SPRING_PROFILES_ACTIVE="dev"`
  - `$env:SPRING_PROFILES_ACTIVE="prod"`
- Hoặc qua JVM/System property:
  - `-Dspring.profiles.active=dev`

## 2) Cấu hình chung (không profile)

### 2.1. App metadata

- `spring.application.name`: tên ứng dụng hiển thị trong log/actuator.

### 2.2. Time zone JSON/JPA

- `spring.jackson.time-zone: UTC`: serialize/deserialize thời gian theo TZ.
- `spring.jpa.properties.hibernate.jdbc.time_zone: UTC`: đồng bộ TZ khi Hibernate ghi/đọc timestamp.

### 2.3. Datasource (MySQL)

- `spring.datasource.url`: JDBC URL MySQL.
  - Đang hỗ trợ nhiều tên env fallback: `MYSQLHOST` hoặc `MYSQL_HOST`, tương tự cho port/db/user/pass.
- `spring.datasource.username`, `spring.datasource.password`: credential DB.
- `spring.datasource.driver-class-name`: `com.mysql.cj.jdbc.Driver`.

### 2.4. JPA/Hibernate

- `spring.jpa.hibernate.ddl-auto`:
  - Mặc định hiện tại: `update` (phù hợp dev/preview; không khuyến nghị cho prod).
- `spring.jpa.open-in-view: false`: tắt Open Session in View để giảm rủi ro N+1 và lỗi lazy khi ra khỏi transaction.
- `spring.jpa.show-sql`: bật/tắt log SQL (dev/prod override theo profile).
- `spring.jpa.properties.hibernate.format_sql`: format SQL khi log (dev/prod override theo profile).

### 2.5. Upload multipart

- `spring.servlet.multipart.max-file-size`: giới hạn size 1 file upload.
- `spring.servlet.multipart.max-request-size`: giới hạn tổng request.

### 2.6. Port

- `server.port: ${PORT:8080}`: ưu tiên env `PORT`, fallback 8080.

### 2.7. Work rule (SLA / tự động hoá)

Nhóm `workrule.*` dùng cho các rule vận hành và job tự động (timeout/SLA).

- `workrule.accept-timeout-hours`: thời gian tối đa collector phải nhận task sau khi được assign.
- `workrule.sla-hours`: SLA tổng thời gian xử lý.
- `workrule.suspend-threshold`: số lần vi phạm để bị suspend.
- `workrule.working-start-hour`, `workrule.working-end-hour`: khung giờ làm việc hợp lệ (phục vụ rule/automation).
- `workrule.reassign-radius-km`: bán kính gợi ý/reassign (km).

### 2.8. JWT

- `jwt.signerKey`: khoá ký JWT (đọc từ env `JWT_SIGNER_KEY`, có default).
- `jwt.valid-duration`: thời gian sống token (giây) (đọc từ env `JWT_VALID_DURATION`, có default).

Lưu ý:
- Nếu đổi `JWT_SIGNER_KEY` trong lúc hệ thống đang hoạt động, các token phát hành trước đó sẽ không còn decode được → 401.

### 2.9. OpenAPI/Swagger

- `springdoc.api-docs.path`: đường dẫn JSON OpenAPI (mặc định: `/v3/api-docs`).
- `springdoc.swagger-ui.path`: UI swagger (mặc định: `/swagger-ui.html`).
- `open.api.*`: metadata hiển thị trên swagger.

### 2.10. Seed data

- `app.seed.enabled`: bật/tắt seed (đọc từ env `APP_SEED_ENABLED`, default false).
- `app.seed.modular`: chế độ seed theo module.

### 2.11. CORS

- `app.cors.allowed-origins`: danh sách origin được phép (string, ngăn cách bằng dấu phẩy).
  - Lấy từ env `APP_CORS_ALLOWED_ORIGINS` (có default).

### 2.12. Cloudinary

Nhóm `cloudinary.*` là credential để upload/xoá ảnh:

- `cloudinary.cloud-name` ← `CLOUDINARY_CLOUD_NAME`
- `cloudinary.api-key` ← `CLOUDINARY_API_KEY`
- `cloudinary.api-secret` ← `CLOUDINARY_API_SECRET`

## 3) Cấu hình theo profile

### 3.1. Profile `dev`

- Bật log SQL:
  - `spring.jpa.show-sql: true`
  - `hibernate.format_sql: true`

### 3.2. Profile `prod`

- `spring.jpa.hibernate.ddl-auto: validate` (không tự tạo/sửa schema; chỉ validate schema khớp entity).
- Tắt log SQL (giữ sạch log prod):
  - `spring.jpa.show-sql: false`
  - `hibernate.format_sql: false`
- Giữ `hibernate.jdbc.time_zone` (đảm bảo thống nhất TZ).

## 4) Danh sách biến môi trường (gợi ý)

Tối thiểu để chạy “đúng môi trường”:

- `SPRING_PROFILES_ACTIVE`: `dev` hoặc `prod`
- DB:
  - `MYSQL_HOST` hoặc `MYSQLHOST`
  - `MYSQL_PORT` hoặc `MYSQLPORT`
  - `MYSQL_DATABASE` hoặc `MYSQLDATABASE`
  - `MYSQL_USER` hoặc `MYSQLUSER`
  - `MYSQL_PASSWORD` hoặc `MYSQLPASSWORD`
- JWT:
  - `JWT_SIGNER_KEY`
  - `JWT_VALID_DURATION`
- CORS:
  - `APP_CORS_ALLOWED_ORIGINS`
- Cloudinary:
  - `CLOUDINARY_CLOUD_NAME`
  - `CLOUDINARY_API_KEY`
  - `CLOUDINARY_API_SECRET`
- Server:
  - `PORT`

## 5) Gợi ý cấu hình local nhanh

- Dùng `dev` khi develop.
- Dùng MySQL local hoặc DB cloud tuỳ team.
- Luôn ưu tiên set secret qua env thay vì hardcode trong file cấu hình.


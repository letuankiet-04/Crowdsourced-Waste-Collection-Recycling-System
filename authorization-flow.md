# Luồng phân quyền RBAC và hướng dẫn test

## Mục tiêu
- Áp dụng phân quyền theo role: Citizen, Enterprise, Collector, Admin cho toàn bộ API.
- Xác thực bằng JWT, kiểm soát truy cập theo URL và theo method.
- Hướng dẫn test trên Postman với các tài khoản seed sẵn.

## Kiến trúc tổng quan
- Phân quyền URL: cấu hình tại [SecurityConfig.java:L34-L54](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/config/security/SecurityConfig.java#L34-L54)
  - Cho phép /api/auth/** công khai.
  - /api/citizen/** yêu cầu role CITIZEN.
  - /api/enterprise/** yêu cầu role ENTERPRISE hoặc ENTERPRISE_ADMIN.
  - /api/collector/** yêu cầu role COLLECTOR.
  - /api/admin/** yêu cầu role ADMIN.
- Bảo mật method: dùng @EnableMethodSecurity và @PreAuthorize trên controller:
  - Ví dụ Citizen: [CitizenController.java:L9-L16](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/CitizenController.java#L9-L16)
  - Ví dụ Enterprise: [EnterpriseController.java:L9-L16](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/EnterpriseController.java#L9-L16)
  - Ví dụ Collector: [CollectorController.java:L9-L16](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/CollectorController.java#L9-L16)
  - Ví dụ Admin: [AdminController.java:L9-L16](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/AdminController.java#L9-L16)
- JWT filter: chèn JwtAuthenticationFilter trước UsernamePasswordAuthenticationFilter để thiết lập SecurityContext từ token.
  - Xem [SecurityConfig.java:L51-L53](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/config/security/SecurityConfig.java#L51-L53)
- UserPrincipal: ánh xạ User sang UserDetails và cấp quyền ROLE_<roleCode>.
  - Xem [UserPrincipal.java:L21-L31](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/config/security/UserPrincipal.java#L21-L31)
- Mapper phản hồi: UserMapper chuyển User sang AuthResponse để trả về client.
  - Xem [UserMapper.java:L14-L23](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/mapper/UserMapper.java#L14-L23)

## Seed dữ liệu cho test
- Khởi tạo role và user mẫu tại [DataSeeder.java:L15-L29](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/config/DataSeeder.java#L15-L29).
- Tài khoản mẫu:
  - CITIZEN: email citizen@test.com, password citizen123
  - ENTERPRISE: email enterprise@test.com, password enterprise123
  - COLLECTOR: email collector@test.com, password collector123
  - ADMIN: email admin@test.com, password admin123
- Tạo role nếu thiếu: [DataSeeder.java:L31-L39](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/config/DataSeeder.java#L31-L39)
- Tạo user nếu chưa tồn tại: [DataSeeder.java:L41-L52](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/config/DataSeeder.java#L41-L52)

## Luồng xác thực và phân quyền
- Đăng ký:
  - Endpoint: POST /api/auth/register
  - Mặc định tạo user role CITIZEN và trả về token JWT.
  - Xem xử lý tại [AuthService.java:L54-L104](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/AuthService.java#L54-L104)
- Đăng nhập:
  - Endpoint: POST /api/auth/login
  - Trả về AuthResponse kèm token JWT.
  - Xem [AuthService.java:L106-L128](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/AuthService.java#L106-L128)
- Truy cập API:
  - Gắn header Authorization: Bearer <JWT>.
  - Token hợp lệ thiết lập người dùng và quyền tương ứng trong SecurityContext → kiểm tra quyền theo URL hoặc @PreAuthorize.

## Hướng dẫn test trên Postman
- Bước 1: Đăng nhập để lấy token
  - Request: POST http://localhost:8080/api/auth/login
  - Body (JSON):
    ```
    { "email": "citizen@test.com", "password": "citizen123" }
    ```
  - Nhận về trường token trong phản hồi.
- Bước 2: Gọi API theo role
  - CITIZEN: GET http://localhost:8080/api/citizen/dashboard
  - ENTERPRISE: GET http://localhost:8080/api/enterprise/dashboard
  - COLLECTOR: GET http://localhost:8080/api/collector/dashboard
  - ADMIN: GET http://localhost:8080/api/admin/dashboard
  - Với mỗi request, thêm header:
    ```
    Authorization: Bearer <token>
    Content-Type: application/json
    ```
  - Kỳ vọng:
    - Đúng role → 200 OK, trả về chuỗi “Hello <Role>! ...”
    - Sai role → 403 Forbidden
    - Thiếu token/invalid → 401 Unauthorized
- Bước 3: Kiểm tra @PreAuthorize
  - Truy cập các endpoint dashboard ở trên để xác nhận method-level security hoạt động đúng.

## Ghi chú kỹ thuật
- Stateless session: cấu hình SessionCreationPolicy.STATELESS để mỗi request tự mang token.
  - Xem [SecurityConfig.java:L47-L49](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/config/security/SecurityConfig.java#L47-L49)
- CORS: cho phép nguồn localhost cho frontend.
  - Xem [SecurityConfig.java:L56-L66](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/config/security/SecurityConfig.java#L56-L66)
- Lưu ý schema: nếu gặp lỗi khóa ngoại liên quan enterprise_id, cần đồng bộ lại quan hệ entity-db trước khi test nâng cao.

## Mở rộng và bảo trì
- Thêm role mới:
  - Bổ sung rule .requestMatchers(...).hasRole("<NEW_ROLE>") trong SecurityConfig.
  - Dùng @PreAuthorize("hasRole('<NEW_ROLE>')") trên method cần bảo vệ.
  - Seed role mới trong DataSeeder hoặc tạo qua migration.
- Tích hợp UI: frontend chỉ cần lưu token sau login và gắn vào header mỗi request.

## Chi tiết từng file
- SecurityConfig.java:
  - Khai báo cấu hình bảo mật Spring và bật @EnableMethodSecurity.
  - Định nghĩa SecurityFilterChain: CORS, disable CSRF, RBAC theo URL, stateless session.
  - Chèn JwtAuthenticationFilter vào chuỗi filter trước UsernamePasswordAuthenticationFilter.
  - CORS cho localhost 3000/5173; bean PasswordEncoder (BCrypt) và AuthenticationManager.
  - Xem [SecurityConfig.java:L34-L76](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/config/security/SecurityConfig.java#L34-L76)
- JwtAuthenticationFilter.java:
  - Lọc mỗi request (OncePerRequestFilter), bỏ qua /api/auth/** để tránh xác thực token cho endpoint công khai.
  - Đọc header Authorization; nếu có Bearer token thì:
    - extractUsername từ token bằng JwtService.
    - loadUserByUsername lấy UserDetails.
    - isTokenValid kiểm tra token khớp user và chưa hết hạn.
    - Đặt Authentication vào SecurityContextHolder để các rule RBAC hoạt động.
  - Xem [JwtAuthenticationFilter.java:L29-L58](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/config/security/JwtAuthenticationFilter.java#L29-L58)
- JwtService.java:
  - Sinh và xác thực JWT dùng HS256, key từ cấu hình application (security.jwt.secret, security.jwt.expiration-ms).
  - generateToken(subject) tạo token với subject là email và thời hạn hết hạn.
  - extractUsername(token) đọc subject từ token.
  - isTokenValid(token, userDetails) so sánh subject và kiểm tra thời gian hết hạn.
  - Hỗ trợ secret ở dạng Base64 hoặc UTF-8; bắt buộc tối thiểu 32 bytes cho HS256.
  - Xem [JwtService.java:L21-L66](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/config/security/JwtService.java#L21-L66)
- UserPrincipal.java:
  - Adapter từ entity User sang UserDetails cho Spring Security.
  - authorities chứa quyền dạng ROLE_<roleCode>, tương thích với hasRole/hasAnyRole.
  - isEnabled dựa trên status "active".
  - Xem [UserPrincipal.java:L21-L79](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/config/security/UserPrincipal.java#L21-L79)
- AuthService.java:
  - registerUser: kiểm tra email, gán role CITIZEN mặc định, hash password, lưu user, tạo profile Citizen, trả AuthResponse + token.
  - login: xác thực credentials qua AuthenticationManager, sinh token và trả AuthResponse.
  - logout: placeholder (stateless, thường xử lý ở client).
  - Xem [AuthService.java:L54-L132](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/service/AuthService.java#L54-L132)
- AuthController.java:
  - REST endpoints /api/auth/register, /login, /logout.
  - Nhận DTO, gọi AuthService, trả về ResponseEntity.
  - Xem [AuthController.java:L18-L45](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/AuthController.java#L18-L45)
- Controllers theo role:
  - CitizenController: @PreAuthorize("hasRole('CITIZEN')"), endpoint demo /dashboard.
    - Xem [CitizenController.java:L9-L16](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/CitizenController.java#L9-L16)
  - EnterpriseController: @PreAuthorize("hasAnyRole('ENTERPRISE','ENTERPRISE_ADMIN')"), endpoint demo /dashboard.
    - Xem [EnterpriseController.java:L9-L16](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/EnterpriseController.java#L9-L16)
  - CollectorController: @PreAuthorize("hasRole('COLLECTOR')"), endpoint demo /dashboard.
    - Xem [CollectorController.java:L9-L16](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/CollectorController.java#L9-L16)
  - AdminController: @PreAuthorize("hasRole('ADMIN')"), endpoint demo /dashboard.
    - Xem [AdminController.java:L9-L16](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/controller/AdminController.java#L9-L16)
- DataSeeder.java:
  - Tạo các role: CITIZEN, ENTERPRISE, COLLECTOR, ENTERPRISE_ADMIN, ADMIN nếu chưa có.
  - Tạo các user demo tương ứng, hash password qua PasswordEncoder.
  - Dùng existsByEmail để tránh tạo trùng.
  - Xem [DataSeeder.java:L15-L52](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/config/DataSeeder.java#L15-L52)
- UserMapper.java:
  - Chuyển entity User thành AuthResponse: id, email, fullName, phone, status, roleCode.
  - Xem [UserMapper.java:L10-L23](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/mapper/UserMapper.java#L10-L23)
- RoleRepository.java:
  - Truy vấn role theo roleCode để gán cho User lúc đăng ký/seed.
  - Xem [RoleRepository.java:L8-L11](file:///d:/Documents/GIT_HUB/Crowdsourced-Waste-Collection-Recycling-System/src/main/java/com/team2/Crowdsourced_Waste_Collection_Recycling_System/repository/RoleRepository.java#L8-L11)

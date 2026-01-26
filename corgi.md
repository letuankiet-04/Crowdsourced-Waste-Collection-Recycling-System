
---

### Step 3: Thiết kế API

#### Citizen
- Tạo báo cáo rác (ảnh + GPS + mô tả)
- Nhận gợi ý loại rác
- Xác nhận loại rác trước khi submit
- Theo dõi trạng thái thu gom
- Xem lịch sử điểm thưởng & leaderboard
- Gửi phản hồi / khiếu nại

#### Recycling Enterprise
- Xem danh sách báo cáo trong khu vực
- Accept / Reject yêu cầu thu gom
- Gán Collector
- Theo dõi tiến độ thu gom theo thời gian thực
- Xem báo cáo thống kê
- Cấu hình quy tắc tính điểm

#### Collector
- Xem danh sách công việc được giao
- Cập nhật trạng thái thu gom
- Upload ảnh xác nhận hoàn tất
- Xem lịch sử công việc

#### Administrator
- Quản lý tài khoản và phân quyền
- Giám sát hoạt động toàn hệ thống
- Tiếp nhận và giải quyết tranh chấp/khiếu nại

---

### Step 4: Business Logic
Mô tả logic backend cho:
- Gợi ý loại rác (rule-based hoặc AI-ready)
- Xác nhận loại rác trước khi lưu vào database
- Điều kiện cộng điểm thưởng
- Kiểm soát quyền truy cập theo role
- Xử lý khiếu nại và tranh chấp

---

### Step 5: Workflow
- Luồng Citizen báo cáo rác
- Luồng Enterprise tiếp nhận và điều phối
- Luồng Collector thu gom và xác nhận
- Luồng cập nhật trạng thái và cộng điểm thưởng

---

### Step 6: Technical Requirements
- RESTful API
- JWT-based Authentication
- Role-based Authorization
- Có thể mở rộng real-time (WebSocket / SSE – optional)

---

## 5. Output (Format)

Trình bày kết quả theo cấu trúc:

1. System Overview
2. Entity & Domain Model
3. Database Schema
4. API Design (Endpoint + Method + Role)
5. Collection Status Flow
6. Waste Type Suggestion Logic
7. Reward Point Logic
8. Security & Permission Notes
9. Future Extension Suggestions

---

## 6. Output Requirement

- Hệ thống phải:
- Gợi ý loại rác: **Organic / Recyclable / Hazardous…**
- Yêu cầu người dùng **xác nhận lại trước khi gửi báo cáo**
- Nội dung rõ ràng, có khả năng triển khai thực tế
- Phù hợp với vai trò **Back End Developer**

---

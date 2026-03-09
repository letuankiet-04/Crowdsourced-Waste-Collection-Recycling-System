# Tài liệu Đặc tả Dữ liệu Mẫu - Phân hệ Voucher

Tài liệu này mô tả chi tiết về dữ liệu mẫu được khởi tạo cho phân hệ Voucher trong hệ thống Crowdsourced Waste Collection & Recycling System.

## 1. Cấu trúc Bảng liên quan

Dữ liệu được thêm vào các bảng sau:
- `vouchers`: Lưu trữ thông tin chung về voucher.
- `voucher_terms`: Lưu trữ các điều khoản áp dụng cho từng voucher.

## 2. Danh sách Voucher Mẫu

Dưới đây là danh sách các voucher được tạo sẵn trong file `sql/seed_data_vouchers.sql`:

### 2.1. Voucher Giảm Giá (Discount)
- **Mã Voucher**: `DISC-20`
- **Tên**: Giảm 20% phí thu gom rác
- **Giá trị hiển thị**: `20%`
- **Điểm quy đổi**: `500` điểm
- **Số lượng**: 1000
- **Điều khoản**:
  1. Áp dụng cho 1 lần thu gom.
  2. Không quy đổi thành tiền mặt.
  3. Hạn sử dụng 30 ngày sau khi đổi.

### 2.2. Quà Tặng Hiện Vật (Physical Gift)
- **Mã Voucher**: `GIFT-BAG`
- **Tên**: Tặng túi vải tái chế
- **Giá trị hiển thị**: `1 Túi`
- **Điểm quy đổi**: `1000` điểm
- **Số lượng**: 500
- **Điều khoản**:
  1. Nhận tại điểm thu gom gần nhất.
  2. Mỗi tài khoản chỉ được đổi 1 lần/tháng.
  3. Số lượng có hạn.

### 2.3. Voucher Mua Sắm (Shopping Voucher)
- **Mã Voucher**: `MART-50`
- **Tên**: Phiếu mua hàng siêu thị 50k
- **Giá trị hiển thị**: `50.000đ`
- **Điểm quy đổi**: `5000` điểm
- **Số lượng**: 200
- **Điều khoản**:
  1. Sử dụng tại hệ thống siêu thị đối tác.
  2. Hạn sử dụng 3 tháng kể từ ngày đổi.
  3. Không áp dụng chung với khuyến mãi khác.

### 2.4. Quyên Góp Từ Thiện (Donation)
- **Mã Voucher**: `DONATE-20`
- **Tên**: Quyên góp quỹ Trồng Rừng 20k
- **Giá trị hiển thị**: `20.000đ`
- **Điểm quy đổi**: `2000` điểm
- **Số lượng**: 1,000,000 (Vô hạn)
- **Điều khoản**:
  1. Số tiền sẽ được chuyển trực tiếp vào quỹ.
  2. Không hoàn lại.
  3. Nhận chứng nhận điện tử qua email.

## 3. Hướng dẫn Sử dụng

Để nạp dữ liệu này vào cơ sở dữ liệu:

1. Đảm bảo bảng `vouchers` và `voucher_terms` đã được tạo (sử dụng script `SWPver1.sql` đã cập nhật).
2. Chạy file script SQL: `sql/seed_data_vouchers.sql`.

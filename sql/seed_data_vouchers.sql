SET @now = NOW();

-- ============================================================================
-- VOUCHERS
-- ============================================================================

INSERT IGNORE INTO vouchers (voucher_code, banner_public_id, logo_public_id, banner_url, logo_url, title, value_display, points_required, valid_until, active, remaining_stock, created_at, updated_at)
VALUES ('DISC-20', NULL, NULL, 'https://res.cloudinary.com/dpsm/image/upload/v1/banners/disc20.jpg', 'https://res.cloudinary.com/dpsm/image/upload/v1/logos/disc20.png', 'Giảm 20% phí thu gom rác', '20%', 500, DATE_ADD(@now, INTERVAL 1 YEAR), 1, 1000, @now, @now);
SET @v1 = (SELECT id FROM vouchers WHERE voucher_code = 'DISC-20');

INSERT IGNORE INTO voucher_terms (voucher_id, term) VALUES 
(@v1, 'Áp dụng cho 1 lần thu gom'),
(@v1, 'Không quy đổi thành tiền mặt'),
(@v1, 'Hạn sử dụng 30 ngày sau khi đổi');

INSERT IGNORE INTO vouchers (voucher_code, banner_public_id, logo_public_id, banner_url, logo_url, title, value_display, points_required, valid_until, active, remaining_stock, created_at, updated_at)
VALUES ('GIFT-BAG', NULL, NULL, 'https://res.cloudinary.com/dpsm/image/upload/v1/banners/tote.jpg', 'https://res.cloudinary.com/dpsm/image/upload/v1/logos/tote.png', 'Tặng túi vải tái chế', '1 Túi', 1000, DATE_ADD(@now, INTERVAL 1 YEAR), 1, 500, @now, @now);
SET @v2 = (SELECT id FROM vouchers WHERE voucher_code = 'GIFT-BAG');

INSERT IGNORE INTO voucher_terms (voucher_id, term) VALUES 
(@v2, 'Nhận tại điểm thu gom gần nhất'),
(@v2, 'Mỗi tài khoản chỉ được đổi 1 lần/tháng'),
(@v2, 'Số lượng có hạn');

INSERT IGNORE INTO vouchers (voucher_code, banner_public_id, logo_public_id, banner_url, logo_url, title, value_display, points_required, valid_until, active, remaining_stock, created_at, updated_at)
VALUES ('MART-50', NULL, NULL, 'https://res.cloudinary.com/dpsm/image/upload/v1/banners/mart50.jpg', 'https://res.cloudinary.com/dpsm/image/upload/v1/logos/mart50.png', 'Phiếu mua hàng siêu thị 50k', '50.000đ', 5000, DATE_ADD(@now, INTERVAL 1 YEAR), 1, 200, @now, @now);
SET @v3 = (SELECT id FROM vouchers WHERE voucher_code = 'MART-50');

INSERT IGNORE INTO voucher_terms (voucher_id, term) VALUES 
(@v3, 'Sử dụng tại hệ thống siêu thị đối tác'),
(@v3, 'Hạn sử dụng 3 tháng kể từ ngày đổi'),
(@v3, 'Không áp dụng chung với khuyến mãi khác');

INSERT IGNORE INTO vouchers (voucher_code, banner_public_id, logo_public_id, banner_url, logo_url, title, value_display, points_required, valid_until, active, remaining_stock, created_at, updated_at)
VALUES ('DONATE-20', NULL, NULL, 'https://res.cloudinary.com/dpsm/image/upload/v1/banners/forest.jpg', 'https://res.cloudinary.com/dpsm/image/upload/v1/logos/forest.png', 'Quyên góp quỹ Trồng Rừng 20k', '20.000đ', 2000, DATE_ADD(@now, INTERVAL 5 YEAR), 1, 1000000, @now, @now);
SET @v4 = (SELECT id FROM vouchers WHERE voucher_code = 'DONATE-20');

INSERT IGNORE INTO voucher_terms (voucher_id, term) VALUES 
(@v4, 'Số tiền sẽ được chuyển trực tiếp vào quỹ'),
(@v4, 'Không hoàn lại'),
(@v4, 'Nhận chứng nhận điện tử qua email');

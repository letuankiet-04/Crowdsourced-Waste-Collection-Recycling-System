SET NOCOUNT ON;

DECLARE @now DATETIME2 = SYSDATETIME();

-- Vouchers
IF NOT EXISTS (SELECT 1 FROM vouchers WHERE voucher_code = 'DISC-20')
BEGIN
    INSERT INTO vouchers (voucher_code, banner_public_id, logo_public_id, banner_url, logo_url, title, value_display, points_required, valid_until, active, remaining_stock, created_at, updated_at)
    VALUES ('DISC-20', NULL, NULL, 'https://res.cloudinary.com/dpsm/image/upload/v1/banners/disc20.jpg', 'https://res.cloudinary.com/dpsm/image/upload/v1/logos/disc20.png', N'Giảm 20% phí thu gom rác', N'20%', 500, DATEADD(YEAR, 1, @now), 1, 1000, @now, @now);
    
    DECLARE @v1 INT = SCOPE_IDENTITY();
    
    INSERT INTO voucher_terms (voucher_id, term) VALUES 
    (@v1, N'Áp dụng cho 1 lần thu gom'),
    (@v1, N'Không quy đổi thành tiền mặt'),
    (@v1, N'Hạn sử dụng 30 ngày sau khi đổi');
END

IF NOT EXISTS (SELECT 1 FROM vouchers WHERE voucher_code = 'GIFT-BAG')
BEGIN
    INSERT INTO vouchers (voucher_code, banner_public_id, logo_public_id, banner_url, logo_url, title, value_display, points_required, valid_until, active, remaining_stock, created_at, updated_at)
    VALUES ('GIFT-BAG', NULL, NULL, 'https://res.cloudinary.com/dpsm/image/upload/v1/banners/tote.jpg', 'https://res.cloudinary.com/dpsm/image/upload/v1/logos/tote.png', N'Tặng túi vải tái chế', N'1 Túi', 1000, DATEADD(YEAR, 1, @now), 1, 500, @now, @now);
    
    DECLARE @v2 INT = SCOPE_IDENTITY();
    
    INSERT INTO voucher_terms (voucher_id, term) VALUES 
    (@v2, N'Nhận tại điểm thu gom gần nhất'),
    (@v2, N'Mỗi tài khoản chỉ được đổi 1 lần/tháng'),
    (@v2, N'Số lượng có hạn');
END

IF NOT EXISTS (SELECT 1 FROM vouchers WHERE voucher_code = 'MART-50')
BEGIN
    INSERT INTO vouchers (voucher_code, banner_public_id, logo_public_id, banner_url, logo_url, title, value_display, points_required, valid_until, active, remaining_stock, created_at, updated_at)
    VALUES ('MART-50', NULL, NULL, 'https://res.cloudinary.com/dpsm/image/upload/v1/banners/mart50.jpg', 'https://res.cloudinary.com/dpsm/image/upload/v1/logos/mart50.png', N'Phiếu mua hàng siêu thị 50k', N'50.000đ', 5000, DATEADD(YEAR, 1, @now), 1, 200, @now, @now);
    
    DECLARE @v3 INT = SCOPE_IDENTITY();
    
    INSERT INTO voucher_terms (voucher_id, term) VALUES 
    (@v3, N'Sử dụng tại hệ thống siêu thị đối tác'),
    (@v3, N'Hạn sử dụng 3 tháng kể từ ngày đổi'),
    (@v3, N'Không áp dụng chung với khuyến mãi khác');
END

IF NOT EXISTS (SELECT 1 FROM vouchers WHERE voucher_code = 'DONATE-20')
BEGIN
    INSERT INTO vouchers (voucher_code, banner_public_id, logo_public_id, banner_url, logo_url, title, value_display, points_required, valid_until, active, remaining_stock, created_at, updated_at)
    VALUES ('DONATE-20', NULL, NULL, 'https://res.cloudinary.com/dpsm/image/upload/v1/banners/forest.jpg', 'https://res.cloudinary.com/dpsm/image/upload/v1/logos/forest.png', N'Quyên góp quỹ Trồng Rừng 20k', N'20.000đ', 2000, DATEADD(YEAR, 5, @now), 1, 1000000, @now, @now);
    
    DECLARE @v4 INT = SCOPE_IDENTITY();
    
    INSERT INTO voucher_terms (voucher_id, term) VALUES 
    (@v4, N'Số tiền sẽ được chuyển trực tiếp vào quỹ'),
    (@v4, N'Không hoàn lại'),
    (@v4, N'Nhận chứng nhận điện tử qua email');
END

USE Waste;

SET @now = NOW();

-- ============================================================================
-- 1. ROLES
-- ============================================================================

INSERT IGNORE INTO roles (role_code, role_name, description, is_active, created_at)
VALUES ('ADMIN', 'Administrator', 'Administrator', 1, @now);
SET @roleAdminId = (SELECT id FROM roles WHERE role_code = 'ADMIN');

INSERT IGNORE INTO roles (role_code, role_name, description, is_active, created_at)
VALUES ('CITIZEN', 'Citizen User', 'Citizen User', 1, @now);
SET @roleCitizenId = (SELECT id FROM roles WHERE role_code = 'CITIZEN');

INSERT IGNORE INTO roles (role_code, role_name, description, is_active, created_at)
VALUES ('COLLECTOR', 'Waste Collector', 'Waste Collector', 1, @now);
SET @roleCollectorId = (SELECT id FROM roles WHERE role_code = 'COLLECTOR');

INSERT IGNORE INTO roles (role_code, role_name, description, is_active, created_at)
VALUES ('ENTERPRISE', 'Recycling Enterprise', 'Recycling Enterprise', 1, @now);
SET @roleEnterpriseId = (SELECT id FROM roles WHERE role_code = 'ENTERPRISE');

-- ============================================================================
-- 2. ENTERPRISE
-- ============================================================================

INSERT IGNORE INTO enterprise (name, address, ward, city, phone, email, license_number, tax_code, capacity_kg_per_day, supported_waste_type_codes, service_wards, service_cities, status, total_collected_weight, created_at, updated_at)
VALUES ('Demo Recycling Enterprise', '12 Nguyễn Huệ', 'Bến Nghé', 'HCM', '0900000000', 'enterprise@demo.com', 'LIC-DEMO-001', 'TAX-DEMO-001', 8000.00, 'RECYCLABLE', 'Bến Nghé;Đa Kao', 'HCM', 'active', 0.00, @now, @now);
SET @enterpriseId = (SELECT id FROM enterprise WHERE email = 'enterprise@demo.com');

SET @bcrypt = '$2a$10$7EqJtq98hPqEX7fNZaFWoO5rEo/pZ5lHppZArYrusS4x2ma/p3d.';

-- ============================================================================
-- 3. USERS
-- ============================================================================

-- Admin User
INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES ('admin@demo.com', @bcrypt, 'Admin Demo', '0909000000', NULL, @roleAdminId, NULL, 'active', NULL, @now, @now);
SET @adminUserId = (SELECT id FROM users WHERE email = 'admin@demo.com');

-- Enterprise User
INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES ('enterprise@demo.com', @bcrypt, 'Enterprise Demo', '0908000000', NULL, @roleEnterpriseId, @enterpriseId, 'active', NULL, @now, @now);
SET @enterpriseUserId = (SELECT id FROM users WHERE email = 'enterprise@demo.com');

-- Collector Users
INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES ('collector1@demo.com', @bcrypt, 'Collector 01', '0901111111', NULL, @roleCollectorId, NULL, 'active', NULL, @now, @now);
SET @collectorUserId1 = (SELECT id FROM users WHERE email = 'collector1@demo.com');

INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES ('collector2@demo.com', @bcrypt, 'Collector 02', '0901111112', NULL, @roleCollectorId, NULL, 'active', NULL, @now, @now);
SET @collectorUserId2 = (SELECT id FROM users WHERE email = 'collector2@demo.com');

INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES ('collector3@demo.com', @bcrypt, 'Collector 03', '0901111113', NULL, @roleCollectorId, NULL, 'active', NULL, @now, @now);
SET @collectorUserId3 = (SELECT id FROM users WHERE email = 'collector3@demo.com');

INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES ('collector4@demo.com', @bcrypt, 'Collector 04', '0901111114', NULL, @roleCollectorId, NULL, 'active', NULL, @now, @now);
SET @collectorUserId4 = (SELECT id FROM users WHERE email = 'collector4@demo.com');

-- Citizen Users
INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES ('citizen1@demo.com', @bcrypt, 'Citizen 01', '0902222201', NULL, @roleCitizenId, NULL, 'active', NULL, @now, @now);
SET @citizenUserId1 = (SELECT id FROM users WHERE email = 'citizen1@demo.com');

INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES ('citizen2@demo.com', @bcrypt, 'Citizen 02', '0902222202', NULL, @roleCitizenId, NULL, 'active', NULL, @now, @now);
SET @citizenUserId2 = (SELECT id FROM users WHERE email = 'citizen2@demo.com');

INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES ('citizen3@demo.com', @bcrypt, 'Citizen 03', '0902222203', NULL, @roleCitizenId, NULL, 'active', NULL, @now, @now);
SET @citizenUserId3 = (SELECT id FROM users WHERE email = 'citizen3@demo.com');

INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES ('citizen4@demo.com', @bcrypt, 'Citizen 04', '0902222204', NULL, @roleCitizenId, NULL, 'active', NULL, @now, @now);
SET @citizenUserId4 = (SELECT id FROM users WHERE email = 'citizen4@demo.com');

INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES ('citizen5@demo.com', @bcrypt, 'Citizen 05', '0902222205', NULL, @roleCitizenId, NULL, 'active', NULL, @now, @now);
SET @citizenUserId5 = (SELECT id FROM users WHERE email = 'citizen5@demo.com');

INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES ('citizen6@demo.com', @bcrypt, 'Citizen 06', '0902222206', NULL, @roleCitizenId, NULL, 'active', NULL, @now, @now);
SET @citizenUserId6 = (SELECT id FROM users WHERE email = 'citizen6@demo.com');

-- ============================================================================
-- 4. PROFILES
-- ============================================================================

-- Citizens
INSERT IGNORE INTO citizens (user_id, email, full_name, password_hash, address, phone, ward, city, total_points, total_reports, valid_reports)
VALUES (@citizenUserId1, 'citizen1@demo.com', 'Citizen 01', NULL, '1 Demo Address', '0902222201', 'Bến Nghé', 'HCM', 0, 0, 0);
SET @citizenId1 = (SELECT id FROM citizens WHERE user_id = @citizenUserId1);

INSERT IGNORE INTO citizens (user_id, email, full_name, password_hash, address, phone, ward, city, total_points, total_reports, valid_reports)
VALUES (@citizenUserId2, 'citizen2@demo.com', 'Citizen 02', NULL, '2 Demo Address', '0902222202', 'Bến Nghé', 'HCM', 0, 0, 0);
SET @citizenId2 = (SELECT id FROM citizens WHERE user_id = @citizenUserId2);

INSERT IGNORE INTO citizens (user_id, email, full_name, password_hash, address, phone, ward, city, total_points, total_reports, valid_reports)
VALUES (@citizenUserId3, 'citizen3@demo.com', 'Citizen 03', NULL, '3 Demo Address', '0902222203', 'Bến Nghé', 'HCM', 0, 0, 0);
SET @citizenId3 = (SELECT id FROM citizens WHERE user_id = @citizenUserId3);

INSERT IGNORE INTO citizens (user_id, email, full_name, password_hash, address, phone, ward, city, total_points, total_reports, valid_reports)
VALUES (@citizenUserId4, 'citizen4@demo.com', 'Citizen 04', NULL, '4 Demo Address', '0902222204', 'Đa Kao', 'HCM', 0, 0, 0);
SET @citizenId4 = (SELECT id FROM citizens WHERE user_id = @citizenUserId4);

INSERT IGNORE INTO citizens (user_id, email, full_name, password_hash, address, phone, ward, city, total_points, total_reports, valid_reports)
VALUES (@citizenUserId5, 'citizen5@demo.com', 'Citizen 05', NULL, '5 Demo Address', '0902222205', 'Đa Kao', 'HCM', 0, 0, 0);
SET @citizenId5 = (SELECT id FROM citizens WHERE user_id = @citizenUserId5);

INSERT IGNORE INTO citizens (user_id, email, full_name, password_hash, address, phone, ward, city, total_points, total_reports, valid_reports)
VALUES (@citizenUserId6, 'citizen6@demo.com', 'Citizen 06', NULL, '6 Demo Address', '0902222206', 'Đa Kao', 'HCM', 0, 0, 0);
SET @citizenId6 = (SELECT id FROM citizens WHERE user_id = @citizenUserId6);

-- Collectors
INSERT IGNORE INTO collectors (user_id, enterprise_id, email, full_name, employee_code, vehicle_type, vehicle_plate, status, last_location_update, total_collections, successful_collections, total_weight_collected, created_at)
VALUES (@collectorUserId1, @enterpriseId, 'collector1@demo.com', 'Collector 01', 'EMP-001', 'Xe tải nhỏ', '59C-10001', 'AVAILABLE', @now, 0, 0, 0.00, @now);
SET @collectorId1 = (SELECT id FROM collectors WHERE user_id = @collectorUserId1);

INSERT IGNORE INTO collectors (user_id, enterprise_id, email, full_name, employee_code, vehicle_type, vehicle_plate, status, last_location_update, total_collections, successful_collections, total_weight_collected, created_at)
VALUES (@collectorUserId2, @enterpriseId, 'collector2@demo.com', 'Collector 02', 'EMP-002', 'Xe ba gác', '59C-10002', 'AVAILABLE', @now, 0, 0, 0.00, @now);
SET @collectorId2 = (SELECT id FROM collectors WHERE user_id = @collectorUserId2);

INSERT IGNORE INTO collectors (user_id, enterprise_id, email, full_name, employee_code, vehicle_type, vehicle_plate, status, last_location_update, total_collections, successful_collections, total_weight_collected, created_at)
VALUES (@collectorUserId3, @enterpriseId, 'collector3@demo.com', 'Collector 03', 'EMP-003', 'Xe tải nhỏ', '59C-10003', 'AVAILABLE', @now, 0, 0, 0.00, @now);
SET @collectorId3 = (SELECT id FROM collectors WHERE user_id = @collectorUserId3);

INSERT IGNORE INTO collectors (user_id, enterprise_id, email, full_name, employee_code, vehicle_type, vehicle_plate, status, last_location_update, total_collections, successful_collections, total_weight_collected, created_at)
VALUES (@collectorUserId4, @enterpriseId, 'collector4@demo.com', 'Collector 04', 'EMP-004', 'Xe máy', '59C-10004', 'AVAILABLE', @now, 0, 0, 0.00, @now);
SET @collectorId4 = (SELECT id FROM collectors WHERE user_id = @collectorUserId4);

-- ============================================================================
-- 5. WASTE TYPES & CATEGORIES
-- ============================================================================

INSERT IGNORE INTO waste_types (code, name, base_points, sla_hours, is_recyclable, created_at)
VALUES ('RECYCLABLE', 'Rác tái chế', 20, 24, 1, @now);
SET @wtRecyclableId = (SELECT id FROM waste_types WHERE code = 'RECYCLABLE');

INSERT IGNORE INTO waste_types (code, name, base_points, sla_hours, is_recyclable, created_at)
VALUES ('HOUSEHOLD', 'Rác sinh hoạt', 5, 24, 0, @now);
SET @wtHouseholdId = (SELECT id FROM waste_types WHERE code = 'HOUSEHOLD');

INSERT IGNORE INTO waste_types (code, name, base_points, sla_hours, is_recyclable, created_at)
VALUES ('HAZARDOUS', 'Rác nguy hại', 50, 12, 0, @now);
SET @wtHazardousId = (SELECT id FROM waste_types WHERE code = 'HAZARDOUS');

INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, waste_type_id, created_at, updated_at)
VALUES
('Giấy', 'Giấy vụn, giấy trắng, giấy in', 'KG', 2250.0000, @wtRecyclableId, @now, @now),
('Bìa carton', 'Thùng carton, bìa cứng', 'KG', 2000.0000, @wtRecyclableId, @now, @now),
('Hộp sữa giấy', 'Hộp sữa/Tetra Pak', 'KG', 1800.0000, @wtRecyclableId, @now, @now),
('Báo - tạp chí', 'Báo giấy, tạp chí cũ', 'KG', 1600.0000, @wtRecyclableId, @now, @now),
('Chai nhựa PET', 'Chai nước suối, nước ngọt (PET)', 'KG', 120.0000, @wtRecyclableId, @now, @now),
('Chai nhựa HDPE', 'Can/chai nhựa cứng (HDPE)', 'KG', 150.0000, @wtRecyclableId, @now, @now),
('Nhựa PP cứng', 'Nhựa cứng PP (đồ gia dụng)', 'KG', 2500.0000, @wtRecyclableId, @now, @now),
('Nhựa mềm', 'Nhựa dẻo, màng bọc', 'KG', 1000.0000, @wtRecyclableId, @now, @now),
('Túi nilon', 'Túi nylon, bao bì mỏng', 'KG', 800.0000, @wtRecyclableId, @now, @now),
('Thủy tinh vỡ', 'Mảnh thủy tinh', 'KG', 500.0000, @wtRecyclableId, @now, @now),
('Chai thủy tinh', 'Chai thủy tinh còn nguyên', 'KG', 200.0000, @wtRecyclableId, @now, @now),
('Lon nhôm', 'Lon nước ngọt/lon bia', 'KG', 180.0000, @wtRecyclableId, @now, @now),
('Lon thiếc', 'Lon đồ hộp/thiếc', 'KG', 120.0000, @wtRecyclableId, @now, @now),
('Sắt vụn', 'Sắt thép phế liệu', 'KG', 3500.0000, @wtRecyclableId, @now, @now),
('Inox', 'Inox phế liệu', 'KG', 8000.0000, @wtRecyclableId, @now, @now),
('Đồng', 'Đồng phế liệu', 'KG', 67500.0000, @wtRecyclableId, @now, @now),
('Nhôm', 'Nhôm phế liệu', 'KG', 9000.0000, @wtRecyclableId, @now, @now),
('Dây điện', 'Dây điện/đồng bọc nhựa', 'KG', 12000.0000, @wtRecyclableId, @now, @now),
('Thiết bị điện tử nhỏ', 'Đồ điện tử nhỏ hư hỏng', 'KG', 15000.0000, @wtRecyclableId, @now, @now),
('Vải - quần áo cũ', 'Vải, quần áo cũ', 'KG', 500.0000, @wtRecyclableId, @now, @now);

-- ============================================================================
-- 6. WASTE REPORTS
-- ============================================================================

INSERT IGNORE INTO waste_reports (report_code, citizen_id, waste_type_id, description, estimated_weight, latitude, longitude, address, images, cloudinary_public_id, status, created_at, updated_at)
VALUES ('WR-DEMO-001', @citizenId1, @wtRecyclableId, 'Báo cáo rác tái chế #001', 1.20, 10.77653000, 106.70098000, 'Quận 1 - Điểm 001', 'https://example.com/wr/1.jpg', NULL, 'PENDING', DATE_SUB(@now, INTERVAL 6 HOUR), DATE_SUB(@now, INTERVAL 6 HOUR));
SET @wr1 = (SELECT id FROM waste_reports WHERE report_code = 'WR-DEMO-001');

INSERT IGNORE INTO waste_reports (report_code, citizen_id, waste_type_id, description, estimated_weight, latitude, longitude, address, images, cloudinary_public_id, status, created_at, updated_at)
VALUES ('WR-DEMO-002', @citizenId2, @wtRecyclableId, 'Báo cáo rác tái chế #002', 2.50, 10.77664000, 106.70108000, 'Quận 1 - Điểm 002', 'https://example.com/wr/2.jpg', NULL, 'ACCEPTED_ENTERPRISE', DATE_SUB(@now, INTERVAL 10 HOUR), DATE_SUB(@now, INTERVAL 8 HOUR));
SET @wr2 = (SELECT id FROM waste_reports WHERE report_code = 'WR-DEMO-002');

INSERT IGNORE INTO waste_reports (report_code, citizen_id, waste_type_id, description, estimated_weight, latitude, longitude, address, images, cloudinary_public_id, status, created_at, updated_at)
VALUES ('WR-DEMO-003', @citizenId3, @wtRecyclableId, 'Báo cáo rác tái chế #003', 0.80, 10.77675000, 106.70118000, 'Quận 1 - Điểm 003', 'https://example.com/wr/3.jpg', NULL, 'ASSIGNED', DATE_SUB(@now, INTERVAL 5 HOUR), DATE_SUB(@now, INTERVAL 4 HOUR));
SET @wr3 = (SELECT id FROM waste_reports WHERE report_code = 'WR-DEMO-003');

INSERT IGNORE INTO waste_reports (report_code, citizen_id, waste_type_id, description, estimated_weight, latitude, longitude, address, images, cloudinary_public_id, status, created_at, updated_at)
VALUES ('WR-DEMO-004', @citizenId4, @wtRecyclableId, 'Báo cáo rác tái chế #004', 3.10, 10.77686000, 106.70128000, 'Quận 1 - Điểm 004', 'https://example.com/wr/4.jpg', NULL, 'ON_THE_WAY', DATE_SUB(@now, INTERVAL 4 HOUR), DATE_SUB(@now, INTERVAL 3 HOUR));
SET @wr4 = (SELECT id FROM waste_reports WHERE report_code = 'WR-DEMO-004');

INSERT IGNORE INTO waste_reports (report_code, citizen_id, waste_type_id, description, estimated_weight, latitude, longitude, address, images, cloudinary_public_id, status, created_at, updated_at)
VALUES ('WR-DEMO-005', @citizenId5, @wtRecyclableId, 'Báo cáo rác tái chế #005', 1.80, 10.77697000, 106.70138000, 'Quận 1 - Điểm 005', 'https://example.com/wr/5.jpg', NULL, 'COLLECTED', DATE_SUB(@now, INTERVAL 12 HOUR), DATE_SUB(@now, INTERVAL 2 HOUR));
SET @wr5 = (SELECT id FROM waste_reports WHERE report_code = 'WR-DEMO-005');

INSERT IGNORE INTO waste_reports (report_code, citizen_id, waste_type_id, description, estimated_weight, latitude, longitude, address, images, cloudinary_public_id, status, created_at, updated_at)
VALUES ('WR-DEMO-006', @citizenId6, @wtRecyclableId, 'Báo cáo rác tái chế #006', 0.60, 10.77708000, 106.70148000, 'Quận 1 - Điểm 006', 'https://example.com/wr/6.jpg', NULL, 'COLLECTED', DATE_SUB(@now, INTERVAL 24 HOUR), DATE_SUB(@now, INTERVAL 1 HOUR));
SET @wr6 = (SELECT id FROM waste_reports WHERE report_code = 'WR-DEMO-006');

INSERT IGNORE INTO waste_reports (report_code, citizen_id, waste_type_id, description, estimated_weight, latitude, longitude, address, images, cloudinary_public_id, status, created_at, updated_at)
VALUES ('WR-DEMO-007', @citizenId1, @wtRecyclableId, 'Báo cáo rác tái chế #007', 2.20, 10.77719000, 106.70158000, 'Quận 1 - Điểm 007', 'https://example.com/wr/7.jpg', NULL, 'PENDING', DATE_SUB(@now, INTERVAL 2 HOUR), DATE_SUB(@now, INTERVAL 2 HOUR));
SET @wr7 = (SELECT id FROM waste_reports WHERE report_code = 'WR-DEMO-007');

INSERT IGNORE INTO waste_reports (report_code, citizen_id, waste_type_id, description, estimated_weight, latitude, longitude, address, images, cloudinary_public_id, status, created_at, updated_at)
VALUES ('WR-DEMO-008', @citizenId2, @wtRecyclableId, 'Báo cáo rác tái chế #008', 1.40, 10.77730000, 106.70168000, 'Quận 1 - Điểm 008', 'https://example.com/wr/8.jpg', NULL, 'ACCEPTED_ENTERPRISE', DATE_SUB(@now, INTERVAL 9 HOUR), DATE_SUB(@now, INTERVAL 8 HOUR));
SET @wr8 = (SELECT id FROM waste_reports WHERE report_code = 'WR-DEMO-008');

INSERT IGNORE INTO waste_reports (report_code, citizen_id, waste_type_id, description, estimated_weight, latitude, longitude, address, images, cloudinary_public_id, status, created_at, updated_at)
VALUES ('WR-DEMO-009', @citizenId3, @wtRecyclableId, 'Báo cáo rác tái chế #009', 4.00, 10.77741000, 106.70178000, 'Quận 1 - Điểm 009', 'https://example.com/wr/9.jpg', NULL, 'ASSIGNED', DATE_SUB(@now, INTERVAL 7 HOUR), DATE_SUB(@now, INTERVAL 6 HOUR));
SET @wr9 = (SELECT id FROM waste_reports WHERE report_code = 'WR-DEMO-009');

INSERT IGNORE INTO waste_reports (report_code, citizen_id, waste_type_id, description, estimated_weight, latitude, longitude, address, images, cloudinary_public_id, status, created_at, updated_at)
VALUES ('WR-DEMO-010', @citizenId4, @wtRecyclableId, 'Báo cáo rác tái chế #010', 1.00, 10.77752000, 106.70188000, 'Quận 1 - Điểm 010', 'https://example.com/wr/10.jpg', NULL, 'REJECTED', DATE_SUB(@now, INTERVAL 20 HOUR), DATE_SUB(@now, INTERVAL 19 HOUR));
SET @wr10 = (SELECT id FROM waste_reports WHERE report_code = 'WR-DEMO-010');

-- ============================================================================
-- 7. COLLECTION REQUESTS
-- ============================================================================

INSERT IGNORE INTO collection_requests (request_code, report_id, enterprise_id, collector_id, status, rejection_reason, assigned_at, accepted_at, started_at, actual_weight_kg, collected_at, completed_at, created_at, updated_at)
VALUES ('CR-DEMO-001', @wr1, @enterpriseId, NULL, 'PENDING', NULL, NULL, NULL, NULL, NULL, NULL, NULL, DATE_SUB(@now, INTERVAL 6 HOUR), DATE_SUB(@now, INTERVAL 6 HOUR));

INSERT IGNORE INTO collection_requests (request_code, report_id, enterprise_id, collector_id, status, rejection_reason, assigned_at, accepted_at, started_at, actual_weight_kg, collected_at, completed_at, created_at, updated_at)
VALUES ('CR-DEMO-002', @wr2, @enterpriseId, NULL, 'ACCEPTED_ENTERPRISE', NULL, NULL, NULL, NULL, NULL, NULL, NULL, DATE_SUB(@now, INTERVAL 10 HOUR), DATE_SUB(@now, INTERVAL 8 HOUR));

INSERT IGNORE INTO collection_requests (request_code, report_id, enterprise_id, collector_id, status, rejection_reason, assigned_at, accepted_at, started_at, actual_weight_kg, collected_at, completed_at, created_at, updated_at)
VALUES ('CR-DEMO-003', @wr3, @enterpriseId, @collectorId1, 'ASSIGNED', NULL, DATE_SUB(@now, INTERVAL 4 HOUR), NULL, NULL, NULL, NULL, NULL, DATE_SUB(@now, INTERVAL 5 HOUR), DATE_SUB(@now, INTERVAL 4 HOUR));

INSERT IGNORE INTO collection_requests (request_code, report_id, enterprise_id, collector_id, status, rejection_reason, assigned_at, accepted_at, started_at, actual_weight_kg, collected_at, completed_at, created_at, updated_at)
VALUES ('CR-DEMO-004', @wr4, @enterpriseId, @collectorId2, 'ON_THE_WAY', NULL, DATE_SUB(@now, INTERVAL 3 HOUR), DATE_SUB(@now, INTERVAL 3 HOUR), DATE_SUB(@now, INTERVAL 3 HOUR), NULL, NULL, NULL, DATE_SUB(@now, INTERVAL 4 HOUR), DATE_SUB(@now, INTERVAL 3 HOUR));

INSERT IGNORE INTO collection_requests (request_code, report_id, enterprise_id, collector_id, status, rejection_reason, assigned_at, accepted_at, started_at, actual_weight_kg, collected_at, completed_at, created_at, updated_at)
VALUES ('CR-DEMO-005', @wr5, @enterpriseId, @collectorId3, 'COLLECTED', NULL, DATE_SUB(@now, INTERVAL 3 HOUR), DATE_SUB(@now, INTERVAL 3 HOUR), DATE_SUB(@now, INTERVAL 3 HOUR), 1.75, DATE_SUB(@now, INTERVAL 2 HOUR), NULL, DATE_SUB(@now, INTERVAL 12 HOUR), DATE_SUB(@now, INTERVAL 2 HOUR));

INSERT IGNORE INTO collection_requests (request_code, report_id, enterprise_id, collector_id, status, rejection_reason, assigned_at, accepted_at, started_at, actual_weight_kg, collected_at, completed_at, created_at, updated_at)
VALUES ('CR-DEMO-006', @wr6, @enterpriseId, @collectorId4, 'COMPLETED', NULL, DATE_SUB(@now, INTERVAL 5 HOUR), DATE_SUB(@now, INTERVAL 5 HOUR), DATE_SUB(@now, INTERVAL 5 HOUR), 0.55, DATE_SUB(@now, INTERVAL 2 HOUR), DATE_SUB(@now, INTERVAL 1 HOUR), DATE_SUB(@now, INTERVAL 24 HOUR), DATE_SUB(@now, INTERVAL 1 HOUR));

INSERT IGNORE INTO collection_requests (request_code, report_id, enterprise_id, collector_id, status, rejection_reason, assigned_at, accepted_at, started_at, actual_weight_kg, collected_at, completed_at, created_at, updated_at)
VALUES ('CR-DEMO-007', @wr7, @enterpriseId, NULL, 'PENDING', NULL, NULL, NULL, NULL, NULL, NULL, NULL, DATE_SUB(@now, INTERVAL 2 HOUR), DATE_SUB(@now, INTERVAL 2 HOUR));

INSERT IGNORE INTO collection_requests (request_code, report_id, enterprise_id, collector_id, status, rejection_reason, assigned_at, accepted_at, started_at, actual_weight_kg, collected_at, completed_at, created_at, updated_at)
VALUES ('CR-DEMO-008', @wr8, @enterpriseId, NULL, 'ACCEPTED_ENTERPRISE', NULL, NULL, NULL, NULL, NULL, NULL, NULL, DATE_SUB(@now, INTERVAL 9 HOUR), DATE_SUB(@now, INTERVAL 8 HOUR));

INSERT IGNORE INTO collection_requests (request_code, report_id, enterprise_id, collector_id, status, rejection_reason, assigned_at, accepted_at, started_at, actual_weight_kg, collected_at, completed_at, created_at, updated_at)
VALUES ('CR-DEMO-009', @wr9, @enterpriseId, @collectorId1, 'ACCEPTED_COLLECTOR', NULL, DATE_SUB(@now, INTERVAL 6 HOUR), DATE_SUB(@now, INTERVAL 6 HOUR), NULL, NULL, NULL, NULL, DATE_SUB(@now, INTERVAL 7 HOUR), DATE_SUB(@now, INTERVAL 6 HOUR));

INSERT IGNORE INTO collection_requests (request_code, report_id, enterprise_id, collector_id, status, rejection_reason, assigned_at, accepted_at, started_at, actual_weight_kg, collected_at, completed_at, created_at, updated_at)
VALUES ('CR-DEMO-010', @wr10, @enterpriseId, NULL, 'REJECTED', 'Ảnh không rõ hoặc sai loại rác', NULL, NULL, NULL, NULL, NULL, NULL, DATE_SUB(@now, INTERVAL 20 HOUR), DATE_SUB(@now, INTERVAL 19 HOUR));

-- ============================================================================
-- 8. COLLECTOR REPORTS
-- ============================================================================

SET @crCompletedId = (SELECT id FROM collection_requests WHERE request_code = 'CR-DEMO-006');

INSERT IGNORE INTO collector_reports (report_code, collection_request_id, collector_id, status, collector_note, total_point, actual_weight_recyclable, collected_at, latitude, longitude, created_at)
VALUES ('CRPT-DEMO-001', @crCompletedId, @collectorId4, 'COMPLETED', 'Hoàn tất thu gom demo', 25000, 0.55, DATE_SUB(@now, INTERVAL 1 HOUR), 10.77752000, 106.70188000, DATE_SUB(@now, INTERVAL 1 HOUR));
SET @collectorReportId = (SELECT id FROM collector_reports WHERE report_code = 'CRPT-DEMO-001');

INSERT IGNORE INTO collector_report_images (collector_report_id, image_url, image_public_id, created_at)
VALUES
(@collectorReportId, 'https://example.com/collector_reports/1.jpg', 'demo_img_1', DATE_SUB(@now, INTERVAL 1 HOUR)),
(@collectorReportId, 'https://example.com/collector_reports/2.jpg', 'demo_img_2', DATE_SUB(@now, INTERVAL 1 HOUR));

SET @catPaperId = (SELECT id FROM waste_categories WHERE name = 'Giấy');
SET @catCanId = (SELECT id FROM waste_categories WHERE name = 'Lon nhôm');

INSERT IGNORE INTO collector_report_items (collector_report_id, waste_category_id, quantity, unit_snapshot, point_per_unit_snapshot, total_point, created_at)
VALUES
(@collectorReportId, @catPaperId, 0.35, 'KG', 2250.0000, 788, DATE_SUB(@now, INTERVAL 1 HOUR)),
(@collectorReportId, @catCanId, 5, 'KG', 180.0000, 900, DATE_SUB(@now, INTERVAL 1 HOUR));

-- ============================================================================
-- VERIFICATION
-- ============================================================================

SELECT
    @enterpriseId AS enterpriseId,
    @collectorId1 AS collectorId1,
    @collectorId2 AS collectorId2,
    @collectorId3 AS collectorId3,
    @collectorId4 AS collectorId4;

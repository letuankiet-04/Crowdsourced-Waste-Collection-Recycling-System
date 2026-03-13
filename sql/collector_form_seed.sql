SET @now = NOW();

-- ============================================================================
-- 1. ROLES
-- ============================================================================

INSERT IGNORE INTO roles (role_code, role_name, description, is_active, created_at)
VALUES ('ADMIN', 'Administrator', 'Administrator', 1, @now);
SET @roleAdminId = (SELECT id FROM roles WHERE role_code = 'ADMIN');

INSERT IGNORE INTO roles (role_code, role_name, description, is_active, created_at)
VALUES ('CITIZEN', 'Citizen User', 'Citizen', 1, @now);
SET @roleCitizenId = (SELECT id FROM roles WHERE role_code = 'CITIZEN');

INSERT IGNORE INTO roles (role_code, role_name, description, is_active, created_at)
VALUES ('COLLECTOR', 'Collector', 'Collector', 1, @now);
SET @roleCollectorId = (SELECT id FROM roles WHERE role_code = 'COLLECTOR');

INSERT IGNORE INTO roles (role_code, role_name, description, is_active, created_at)
VALUES ('ENTERPRISE', 'Enterprise', 'Enterprise', 1, @now);
SET @roleEnterpriseId = (SELECT id FROM roles WHERE role_code = 'ENTERPRISE');

-- ============================================================================
-- 2. ENTERPRISE
-- ============================================================================

INSERT IGNORE INTO enterprise (name, address, ward, city, phone, email, license_number, tax_code, capacity_kg_per_day, supported_waste_type_codes, service_wards, service_cities, status, total_collected_weight, created_at, updated_at)
VALUES ('Seed Enterprise', '12 Nguyễn Huệ', 'Bến Nghé', 'HCM', '0900000000', 'enterprise@seed.com', 'LIC-SEED-001', 'TAX-SEED-001', 8000.00, 'RECYCLABLE', 'Bến Nghé', 'HCM', 'active', 0.00, @now, @now);
SET @enterpriseId = (SELECT id FROM enterprise WHERE email = 'enterprise@seed.com');

SET @bcrypt = '$2a$10$7EqJtq98hPqEX7fNZaFWoO5rEo/pZ5lHppZArYrusS4x2ma/p3d.';

-- ============================================================================
-- 3. USERS
-- ============================================================================

INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES ('admin@seed.com', @bcrypt, 'Admin Seed', '0909000000', NULL, @roleAdminId, NULL, 'active', NULL, @now, @now);
SET @adminUserId = (SELECT id FROM users WHERE email = 'admin@seed.com');

INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES ('enterprise@seed.com', @bcrypt, 'Enterprise Seed', '0908000000', NULL, @roleEnterpriseId, @enterpriseId, 'active', NULL, @now, @now);
SET @enterpriseUserId = (SELECT id FROM users WHERE email = 'enterprise@seed.com');

INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES ('collector@seed.com', @bcrypt, 'Collector Seed', '0901111111', NULL, @roleCollectorId, NULL, 'active', NULL, @now, @now);
SET @collectorUserId = (SELECT id FROM users WHERE email = 'collector@seed.com');

INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES ('citizen@seed.com', @bcrypt, 'Citizen Seed', '0902222222', NULL, @roleCitizenId, NULL, 'active', NULL, @now, @now);
SET @citizenUserId = (SELECT id FROM users WHERE email = 'citizen@seed.com');

-- ============================================================================
-- 4. PROFILES
-- ============================================================================

INSERT IGNORE INTO citizens (user_id, email, full_name, password_hash, address, phone, ward, city, total_points, total_reports, valid_reports)
VALUES (@citizenUserId, 'citizen@seed.com', 'Citizen Seed', NULL, '1 Seed Address', '0902222222', 'Bến Nghé', 'HCM', 0, 0, 0);
SET @citizenId = (SELECT id FROM citizens WHERE user_id = @citizenUserId);

INSERT IGNORE INTO collectors (user_id, enterprise_id, email, full_name, employee_code, vehicle_type, vehicle_plate, status, last_location_update, total_collections, successful_collections, total_weight_collected, created_at)
VALUES (@collectorUserId, @enterpriseId, 'collector@seed.com', 'Collector Seed', 'EMP-SEED-001', 'Xe tải nhỏ', '59C-10001', 'AVAILABLE', @now, 0, 0, 0.00, @now);
SET @collectorId = (SELECT id FROM collectors WHERE user_id = @collectorUserId);

-- ============================================================================
-- 5. WASTE CATEGORIES
-- ============================================================================

INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, created_at, updated_at) VALUES ('Giấy', NULL, 'KG', 2250.0000, @now, @now);
INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, created_at, updated_at) VALUES ('Lon nhôm', NULL, 'KG', 180.0000, @now, @now);
INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, created_at, updated_at) VALUES ('Chai nhựa PET', NULL, 'KG', 120.0000, @now, @now);

SET @catPaperId = (SELECT id FROM waste_categories WHERE name = 'Giấy');
SET @catCanId = (SELECT id FROM waste_categories WHERE name = 'Lon nhôm');
SET @catPetId = (SELECT id FROM waste_categories WHERE name = 'Chai nhựa PET');

-- ============================================================================
-- 6. REPORTS & REQUESTS
-- ============================================================================

INSERT IGNORE INTO waste_reports (report_code, citizen_id, description, waste_type_id, estimated_weight, latitude, longitude, address, images, status, created_at, updated_at)
VALUES ('WR-SEED-001', @citizenId, 'Báo cáo rác tái chế #SEED-001', 1, 1.20, 10.77653000, 106.70098000, 'Quận 1 - Điểm 001', 'https://example.com/seed/wr1.jpg', 'ASSIGNED', DATE_SUB(@now, INTERVAL 6 HOUR), DATE_SUB(@now, INTERVAL 6 HOUR));
SET @wr1 = (SELECT id FROM waste_reports WHERE report_code = 'WR-SEED-001');

INSERT IGNORE INTO waste_reports (report_code, citizen_id, description, waste_type_id, estimated_weight, latitude, longitude, address, images, status, created_at, updated_at)
VALUES ('WR-SEED-002', @citizenId, 'Báo cáo rác tái chế #SEED-002', 1, 2.50, 10.77664000, 106.70108000, 'Quận 1 - Điểm 002', 'https://example.com/seed/wr2.jpg', 'COLLECTED', DATE_SUB(@now, INTERVAL 10 HOUR), DATE_SUB(@now, INTERVAL 8 HOUR));
SET @wr2 = (SELECT id FROM waste_reports WHERE report_code = 'WR-SEED-002');

INSERT IGNORE INTO waste_report_items (report_id, waste_category_id, quantity, unit_snapshot, created_at)
VALUES (@wr1, @catPaperId, 0.70, 'KG', DATE_SUB(@now, INTERVAL 6 HOUR));
INSERT IGNORE INTO waste_report_items (report_id, waste_category_id, quantity, unit_snapshot, created_at)
VALUES (@wr1, @catCanId, 2, 'KG', DATE_SUB(@now, INTERVAL 6 HOUR));

INSERT IGNORE INTO waste_report_items (report_id, waste_category_id, quantity, unit_snapshot, created_at)
VALUES (@wr2, @catPetId, 4, 'KG', DATE_SUB(@now, INTERVAL 10 HOUR));
INSERT IGNORE INTO waste_report_items (report_id, waste_category_id, quantity, unit_snapshot, created_at)
VALUES (@wr2, @catPaperId, 0.25, 'KG', DATE_SUB(@now, INTERVAL 10 HOUR));

INSERT IGNORE INTO report_images (report_id, image_url, image_type, uploaded_at) VALUES (@wr1, 'https://example.com/seed/wr1.jpg', 'BEFORE', DATE_SUB(@now, INTERVAL 6 HOUR));
INSERT IGNORE INTO report_images (report_id, image_url, image_type, uploaded_at) VALUES (@wr2, 'https://example.com/seed/wr2.jpg', 'BEFORE', DATE_SUB(@now, INTERVAL 10 HOUR));

INSERT IGNORE INTO collection_requests (request_code, report_id, enterprise_id, collector_id, status, rejection_reason, assigned_at, accepted_at, started_at, actual_weight_kg, collected_at, completed_at, created_at, updated_at)
VALUES ('CR-SEED-001', @wr1, @enterpriseId, @collectorId, 'ASSIGNED', NULL, DATE_SUB(@now, INTERVAL 5 HOUR), NULL, NULL, NULL, NULL, NULL, DATE_SUB(@now, INTERVAL 6 HOUR), DATE_SUB(@now, INTERVAL 5 HOUR));
SET @cr1 = (SELECT id FROM collection_requests WHERE request_code = 'CR-SEED-001');

INSERT IGNORE INTO collection_requests (request_code, report_id, enterprise_id, collector_id, status, rejection_reason, assigned_at, accepted_at, started_at, actual_weight_kg, collected_at, completed_at, created_at, updated_at)
VALUES ('CR-SEED-002', @wr2, @enterpriseId, @collectorId, 'COLLECTED', NULL, DATE_SUB(@now, INTERVAL 9 HOUR), DATE_SUB(@now, INTERVAL 9 HOUR), DATE_SUB(@now, INTERVAL 8 HOUR), 2.75, DATE_SUB(@now, INTERVAL 7 HOUR), NULL, DATE_SUB(@now, INTERVAL 10 HOUR), DATE_SUB(@now, INTERVAL 7 HOUR));
SET @cr2 = (SELECT id FROM collection_requests WHERE request_code = 'CR-SEED-002');

INSERT IGNORE INTO collection_tracking (collection_request_id, collector_id, action, latitude, longitude, note, images, created_at)
VALUES (@cr1, @collectorId, 'assigned', 10.77653000, 106.70098000, 'Assigned', NULL, DATE_SUB(@now, INTERVAL 5 HOUR));

INSERT IGNORE INTO collection_tracking (collection_request_id, collector_id, action, latitude, longitude, note, images, created_at)
VALUES (@cr2, @collectorId, 'assigned', 10.77664000, 106.70108000, 'Assigned', NULL, DATE_SUB(@now, INTERVAL 9 HOUR));
INSERT IGNORE INTO collection_tracking (collection_request_id, collector_id, action, latitude, longitude, note, images, created_at)
VALUES (@cr2, @collectorId, 'accepted', 10.77664000, 106.70108000, 'Accepted', NULL, DATE_SUB(@now, INTERVAL 9 HOUR));
INSERT IGNORE INTO collection_tracking (collection_request_id, collector_id, action, latitude, longitude, note, images, created_at)
VALUES (@cr2, @collectorId, 'started', 10.77664000, 106.70108000, 'Started', NULL, DATE_SUB(@now, INTERVAL 8 HOUR));
INSERT IGNORE INTO collection_tracking (collection_request_id, collector_id, action, latitude, longitude, note, images, created_at)
VALUES (@cr2, @collectorId, 'collected', 10.77664000, 106.70108000, 'Completed', '["https://example.com/seed/rep2_before.jpg","https://example.com/seed/rep2_after.jpg"]', DATE_SUB(@now, INTERVAL 7 HOUR));

INSERT IGNORE INTO collector_reports (report_code, collection_request_id, collector_id, status, collector_note, total_point, actual_weight_recyclable, collected_at, latitude, longitude, created_at)
VALUES ('CRPT-SEED-002', @cr2, @collectorId, 'COMPLETED', 'Hoàn tất thu gom seed', 5000, 2.75, DATE_SUB(@now, INTERVAL 7 HOUR), 10.77664000, 106.70108000, DATE_SUB(@now, INTERVAL 7 HOUR));
SET @collectorReportId = (SELECT id FROM collector_reports WHERE report_code = 'CRPT-SEED-002');

INSERT IGNORE INTO collector_report_images (collector_report_id, image_url, image_public_id, created_at)
VALUES
(@collectorReportId, 'https://example.com/seed/collector2_before.jpg', 'seed_collector2_before', DATE_SUB(@now, INTERVAL 7 HOUR)),
(@collectorReportId, 'https://example.com/seed/collector2_after.jpg', 'seed_collector2_after', DATE_SUB(@now, INTERVAL 7 HOUR));

INSERT IGNORE INTO collector_report_items (collector_report_id, waste_category_id, quantity, unit_snapshot, point_per_unit_snapshot, total_point, created_at)
VALUES
(@collectorReportId, @catPaperId, 0.75, 'KG', 2250.0000, 1688, DATE_SUB(@now, INTERVAL 7 HOUR)),
(@collectorReportId, @catCanId, 3, 'KG', 180.0000, 540, DATE_SUB(@now, INTERVAL 7 HOUR));

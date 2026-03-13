SET @now = NOW();

-- ============================================================================
-- 1. ROLES
-- ============================================================================

INSERT IGNORE INTO roles (role_code, role_name, description, is_active, created_at)
VALUES ('ADMIN', 'Administrator', 'System Administrator', 1, @now);
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

INSERT IGNORE INTO roles (role_code, role_name, description, is_active, created_at)
VALUES ('ENTERPRISE_ADMIN', 'Enterprise Administrator', 'Enterprise Administrator', 1, @now);
SET @roleEnterpriseAdminId = (SELECT id FROM roles WHERE role_code = 'ENTERPRISE_ADMIN');

-- ============================================================================
-- 2. PERMISSIONS
-- ============================================================================

INSERT IGNORE INTO permissions (permission_code, permission_name, module, description)
VALUES ('CREATE_REPORT', 'Create waste report', 'CITIZEN', 'Create a new waste report');
SET @permCreateReportId = (SELECT id FROM permissions WHERE permission_code = 'CREATE_REPORT');

INSERT IGNORE INTO permissions (permission_code, permission_name, module, description)
VALUES ('VIEW_OWN_REPORTS', 'View own waste reports', 'CITIZEN', 'View waste reports created by the citizen');
SET @permViewOwnReportsId = (SELECT id FROM permissions WHERE permission_code = 'VIEW_OWN_REPORTS');

INSERT IGNORE INTO permissions (permission_code, permission_name, module, description)
VALUES ('VIEW_AREA_REPORTS', 'View reports in assigned area', 'ENTERPRISE', 'View waste reports in enterprise coverage area');
SET @permViewAreaReportsId = (SELECT id FROM permissions WHERE permission_code = 'VIEW_AREA_REPORTS');

INSERT IGNORE INTO permissions (permission_code, permission_name, module, description)
VALUES ('ASSIGN_COLLECTOR', 'Assign collector to report', 'ENTERPRISE', 'Assign a collector to a collection request');
SET @permAssignCollectorId = (SELECT id FROM permissions WHERE permission_code = 'ASSIGN_COLLECTOR');

INSERT IGNORE INTO permissions (permission_code, permission_name, module, description)
VALUES ('VIEW_ASSIGNED_TASKS', 'View assigned collection tasks', 'COLLECTOR', 'View tasks assigned to the collector');
SET @permViewAssignedTasksId = (SELECT id FROM permissions WHERE permission_code = 'VIEW_ASSIGNED_TASKS');

INSERT IGNORE INTO permissions (permission_code, permission_name, module, description)
VALUES ('UPDATE_TASK_STATUS', 'Update task collection status', 'COLLECTOR', 'Update collection request status by collector');
SET @permUpdateTaskStatusId = (SELECT id FROM permissions WHERE permission_code = 'UPDATE_TASK_STATUS');

-- ============================================================================
-- 3. ROLE PERMISSIONS
-- ============================================================================

INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES (@roleCitizenId, @permCreateReportId);
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES (@roleCitizenId, @permViewOwnReportsId);
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES (@roleEnterpriseId, @permViewAreaReportsId);
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES (@roleEnterpriseAdminId, @permViewAreaReportsId);
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES (@roleEnterpriseAdminId, @permAssignCollectorId);
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES (@roleCollectorId, @permViewAssignedTasksId);
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES (@roleCollectorId, @permUpdateTaskStatusId);

-- ============================================================================
-- 4. ENTERPRISE
-- ============================================================================

INSERT IGNORE INTO enterprise (name, address, ward, city, phone, email, license_number, tax_code, capacity_kg_per_day, supported_waste_type_codes, service_wards, service_cities, status, total_collected_weight, created_at, updated_at)
VALUES ('Demo Recycling Enterprise', '12 Nguyễn Huệ', 'Bến Nghé', 'HCM', '0900000000', 'enterprise@demo.com', 'LIC-DEMO-001', 'TAX-DEMO-001', 8000.00, 'RECYCLABLE', 'Bến Nghé;Đa Kao', 'HCM', 'active', 0.00, @now, @now);
SET @enterpriseId = (SELECT id FROM enterprise WHERE email = 'enterprise@demo.com');

SET @bcrypt = '$2a$10$7EqJtq98hPqEX7fNZaFWoO5rEo/pZ5lHppZArYrusS4x2ma/p3d.';

-- ============================================================================
-- 5. USERS
-- ============================================================================

-- Admin
INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES ('admin@demo.com', @bcrypt, 'Admin Demo', '0909000000', NULL, @roleAdminId, NULL, 'active', NULL, @now, @now);
SET @adminUserId = (SELECT id FROM users WHERE email = 'admin@demo.com');

-- Enterprise Admin
INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES ('enterprise@demo.com', @bcrypt, 'Enterprise Demo', '0908000000', NULL, @roleEnterpriseAdminId, @enterpriseId, 'active', NULL, @now, @now);
SET @enterpriseUserId = (SELECT id FROM users WHERE email = 'enterprise@demo.com');

UPDATE users
SET enterprise_id = @enterpriseId, role_id = @roleEnterpriseAdminId
WHERE id = @enterpriseUserId AND (enterprise_id IS NULL OR enterprise_id <> @enterpriseId OR role_id <> @roleEnterpriseAdminId);

-- Collectors
INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES ('collector1@demo.com', @bcrypt, 'Collector 01', '0901111111', NULL, @roleCollectorId, NULL, 'active', NULL, @now, @now);
SET @collectorUserId1 = (SELECT id FROM users WHERE email = 'collector1@demo.com');

INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES ('collector2@demo.com', @bcrypt, 'Collector 02', '0901111112', NULL, @roleCollectorId, NULL, 'active', NULL, @now, @now);
SET @collectorUserId2 = (SELECT id FROM users WHERE email = 'collector2@demo.com');

-- Citizens
INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES ('citizen1@demo.com', @bcrypt, 'Citizen 01', '0902222201', NULL, @roleCitizenId, NULL, 'active', NULL, @now, @now);
SET @citizenUserId1 = (SELECT id FROM users WHERE email = 'citizen1@demo.com');

INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES ('citizen2@demo.com', @bcrypt, 'Citizen 02', '0902222202', NULL, @roleCitizenId, NULL, 'active', NULL, @now, @now);
SET @citizenUserId2 = (SELECT id FROM users WHERE email = 'citizen2@demo.com');

-- ============================================================================
-- 6. PROFILES
-- ============================================================================

-- Citizens
INSERT IGNORE INTO citizens (user_id, email, full_name, password_hash, address, phone, ward, city, total_points, total_reports, valid_reports)
VALUES (@citizenUserId1, 'citizen1@demo.com', 'Citizen 01', NULL, '1 Demo Address', '0902222201', 'Bến Nghé', 'HCM', 0, 0, 0);
SET @citizenId1 = (SELECT id FROM citizens WHERE user_id = @citizenUserId1);

INSERT IGNORE INTO citizens (user_id, email, full_name, password_hash, address, phone, ward, city, total_points, total_reports, valid_reports)
VALUES (@citizenUserId2, 'citizen2@demo.com', 'Citizen 02', NULL, '2 Demo Address', '0902222202', 'Bến Nghé', 'HCM', 0, 0, 0);
SET @citizenId2 = (SELECT id FROM citizens WHERE user_id = @citizenUserId2);

-- Collectors
INSERT IGNORE INTO collectors (user_id, enterprise_id, email, full_name, employee_code, vehicle_type, vehicle_plate, status, last_location_update, total_collections, successful_collections, total_weight_collected, created_at)
VALUES (@collectorUserId1, @enterpriseId, 'collector1@demo.com', 'Collector 01', 'EMP-001', 'Xe tải nhỏ', '59C-10001', 'AVAILABLE', @now, 0, 0, 0.00, @now);
SET @collectorId1 = (SELECT id FROM collectors WHERE user_id = @collectorUserId1);

INSERT IGNORE INTO collectors (user_id, enterprise_id, email, full_name, employee_code, vehicle_type, vehicle_plate, status, last_location_update, total_collections, successful_collections, total_weight_collected, created_at)
VALUES (@collectorUserId2, @enterpriseId, 'collector2@demo.com', 'Collector 02', 'EMP-002', 'Xe ba gác', '59C-10002', 'AVAILABLE', @now, 0, 0, 0.00, @now);
SET @collectorId2 = (SELECT id FROM collectors WHERE user_id = @collectorUserId2);

-- ============================================================================
-- 7. WASTE CATEGORIES
-- ============================================================================

INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, created_at, updated_at) VALUES ('Giấy', NULL, 'KG', 2250.0000, @now, @now);
INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, created_at, updated_at) VALUES ('Báo', NULL, 'KG', 3600.0000, @now, @now);
INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, created_at, updated_at) VALUES ('Giấy, hồ sơ', NULL, 'KG', 3150.0000, @now, @now);
INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, created_at, updated_at) VALUES ('Giấy tập', NULL, 'KG', 3600.0000, @now, @now);
INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, created_at, updated_at) VALUES ('Lon bia', NULL, 'KG', 180.0000, @now, @now);
INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, created_at, updated_at) VALUES ('Sắt', NULL, 'KG', 3600.0000, @now, @now);
INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, created_at, updated_at) VALUES ('Sắt lon', NULL, 'KG', 1440.0000, @now, @now);
INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, created_at, updated_at) VALUES ('Inox', NULL, 'KG', 5400.0000, @now, @now);
INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, created_at, updated_at) VALUES ('Đồng', NULL, 'KG', 67500.0000, @now, @now);
INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, created_at, updated_at) VALUES ('Nhôm', NULL, 'KG', 16200.0000, @now, @now);
INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, created_at, updated_at) VALUES ('Chai thủy tinh', NULL, 'KG', 450.0000, @now, @now);
INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, created_at, updated_at) VALUES ('Bao bì, hỗn hợp', NULL, 'KG', 1600.0000, @now, @now);
INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, created_at, updated_at) VALUES ('Meca', NULL, 'KG', 450.0000, @now, @now);
INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, created_at, updated_at) VALUES ('Mủ', NULL, 'KG', 3600.0000, @now, @now);
INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, created_at, updated_at) VALUES ('Mủ bình', NULL, 'KG', 4500.0000, @now, @now);
INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, created_at, updated_at) VALUES ('Mủ tôn', NULL, 'KG', 1800.0000, @now, @now);
INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, created_at, updated_at) VALUES ('Mủ đen', NULL, 'KG', 150.0000, @now, @now);
INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, created_at, updated_at) VALUES ('Lon nhôm', NULL, 'KG', 180.0000, @now, @now);
INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, created_at, updated_at) VALUES ('Chai nhựa PET', NULL, 'KG', 120.0000, @now, @now);

SET @catPaperId = (SELECT id FROM waste_categories WHERE name = 'Giấy');
SET @catCanId = (SELECT id FROM waste_categories WHERE name = 'Lon nhôm');
SET @catPetId = (SELECT id FROM waste_categories WHERE name = 'Chai nhựa PET');
SET @catCopperId = (SELECT id FROM waste_categories WHERE name = 'Đồng');

-- ============================================================================
-- 8. REPORTS & REQUESTS
-- ============================================================================

INSERT IGNORE INTO waste_reports (report_code, citizen_id, description, waste_type_id, estimated_weight, latitude, longitude, address, images, cloudinary_public_id, status, created_at, updated_at)
VALUES ('WR-DEMO-001', @citizenId1, 'Báo cáo rác tái chế #001', 1, 1.20, 10.77653000, 106.70098000, 'Quận 1 - Điểm 001', 'https://example.com/wr/1.jpg', NULL, 'PENDING', DATE_SUB(@now, INTERVAL 6 HOUR), DATE_SUB(@now, INTERVAL 6 HOUR));
SET @wr1 = (SELECT id FROM waste_reports WHERE report_code = 'WR-DEMO-001');

INSERT IGNORE INTO waste_reports (report_code, citizen_id, description, waste_type_id, estimated_weight, latitude, longitude, address, images, cloudinary_public_id, status, created_at, updated_at)
VALUES ('WR-DEMO-002', @citizenId2, 'Báo cáo rác tái chế #002', 1, 2.50, 10.77664000, 106.70108000, 'Quận 1 - Điểm 002', 'https://example.com/wr/2.jpg', NULL, 'ASSIGNED', DATE_SUB(@now, INTERVAL 10 HOUR), DATE_SUB(@now, INTERVAL 8 HOUR));
SET @wr2 = (SELECT id FROM waste_reports WHERE report_code = 'WR-DEMO-002');

INSERT IGNORE INTO waste_report_items (report_id, waste_category_id, quantity, unit_snapshot, created_at)
VALUES (@wr1, @catPaperId, 0.70, 'KG', DATE_SUB(@now, INTERVAL 6 HOUR));
INSERT IGNORE INTO waste_report_items (report_id, waste_category_id, quantity, unit_snapshot, created_at)
VALUES (@wr1, @catCanId, 2, 'KG', DATE_SUB(@now, INTERVAL 6 HOUR));

INSERT IGNORE INTO waste_report_items (report_id, waste_category_id, quantity, unit_snapshot, created_at)
VALUES (@wr2, @catPetId, 4, 'KG', DATE_SUB(@now, INTERVAL 10 HOUR));
INSERT IGNORE INTO waste_report_items (report_id, waste_category_id, quantity, unit_snapshot, created_at)
VALUES (@wr2, @catCopperId, 0.25, 'KG', DATE_SUB(@now, INTERVAL 10 HOUR));

INSERT IGNORE INTO report_images (report_id, image_url, image_type, uploaded_at) VALUES (@wr1, 'https://example.com/wr/1.jpg', 'BEFORE', DATE_SUB(@now, INTERVAL 6 HOUR));
INSERT IGNORE INTO report_images (report_id, image_url, image_type, uploaded_at) VALUES (@wr2, 'https://example.com/wr/2.jpg', 'BEFORE', DATE_SUB(@now, INTERVAL 10 HOUR));

INSERT IGNORE INTO collection_requests (request_code, report_id, enterprise_id, collector_id, status, rejection_reason, assigned_at, accepted_at, started_at, actual_weight_kg, collected_at, completed_at, created_at, updated_at)
VALUES ('CR-DEMO-001', @wr1, @enterpriseId, NULL, 'PENDING', NULL, NULL, NULL, NULL, NULL, NULL, NULL, DATE_SUB(@now, INTERVAL 6 HOUR), DATE_SUB(@now, INTERVAL 6 HOUR));
SET @cr1 = (SELECT id FROM collection_requests WHERE request_code = 'CR-DEMO-001');

INSERT IGNORE INTO collection_requests (request_code, report_id, enterprise_id, collector_id, status, rejection_reason, assigned_at, accepted_at, started_at, actual_weight_kg, collected_at, completed_at, created_at, updated_at)
VALUES ('CR-DEMO-002', @wr2, @enterpriseId, @collectorId1, 'ASSIGNED', NULL, DATE_SUB(@now, INTERVAL 9 HOUR), NULL, NULL, NULL, NULL, NULL, DATE_SUB(@now, INTERVAL 10 HOUR), DATE_SUB(@now, INTERVAL 9 HOUR));
SET @cr2 = (SELECT id FROM collection_requests WHERE request_code = 'CR-DEMO-002');

INSERT IGNORE INTO collection_tracking (collection_request_id, collector_id, action, latitude, longitude, note, images, created_at)
VALUES (@cr2, @collectorId1, 'assigned', 10.77664000, 106.70108000, 'Demo assigned', NULL, DATE_SUB(@now, INTERVAL 9 HOUR));

-- ============================================================================
-- 9. COLLECTOR REPORTS
-- ============================================================================

INSERT IGNORE INTO collector_reports (report_code, collection_request_id, collector_id, status, collector_note, total_point, actual_weight_recyclable, collected_at, latitude, longitude, created_at)
VALUES ('CRPT-DEMO-001', @cr2, @collectorId1, 'COMPLETED', 'Hoàn tất thu gom demo', 5000, 0.95, DATE_SUB(@now, INTERVAL 1 HOUR), 10.77664000, 106.70108000, DATE_SUB(@now, INTERVAL 1 HOUR));
SET @collectorReportId = (SELECT id FROM collector_reports WHERE report_code = 'CRPT-DEMO-001');

INSERT IGNORE INTO collector_report_items (collector_report_id, waste_category_id, quantity, unit_snapshot, point_per_unit_snapshot, total_point, created_at)
VALUES
(@collectorReportId, @catPaperId, 0.70, 'KG', 2250.0000, 1575, DATE_SUB(@now, INTERVAL 1 HOUR)),
(@collectorReportId, @catCanId, 2, 'KG', 180.0000, 360, DATE_SUB(@now, INTERVAL 1 HOUR));

-- ============================================================================
-- 10. POINTS & FEEDBACK & LEADERBOARD
-- ============================================================================

INSERT IGNORE INTO point_transactions (citizen_id, report_id, collection_request_id, points, transaction_type, description, balance_after, created_by, created_at)
VALUES (@citizenId2, @wr2, @cr2, 5000, 'EARN', 'Điểm thưởng thu gom demo', 5000, @adminUserId, DATE_SUB(@now, INTERVAL 1 HOUR));

INSERT IGNORE INTO feedbacks (feedback_code, citizen_id, collection_request_id, feedback_type, subject, content, images, severity, status, assigned_to, assigned_at, resolution, resolved_by, resolved_at, responses, created_at, updated_at)
VALUES ('FB-DEMO-001', @citizenId1, @cr2, 'SERVICE', 'Chậm trễ thu gom', 'Yêu cầu đã được gán nhưng chưa thấy cập nhật', NULL, 'MEDIUM', 'OPEN', @enterpriseUserId, DATE_SUB(@now, INTERVAL 2 HOUR), NULL, NULL, NULL, NULL, DATE_SUB(@now, INTERVAL 2 HOUR), DATE_SUB(@now, INTERVAL 2 HOUR));

INSERT IGNORE INTO leaderboard (citizen_id, ward, city, period_type, period_start, period_end, total_points, total_reports, valid_reports, total_weight_kg, rank_position, updated_at)
VALUES
(@citizenId2, 'Bến Nghé', 'HCM', 'MONTHLY', DATE_FORMAT(@now, '%Y-%m-01'), LAST_DAY(@now), 5000, 1, 1, 0.95, 1, @now);

INSERT IGNORE INTO invalidated_tokens (id, expiry_time) VALUES ('demo-token-1', DATE_ADD(@now, INTERVAL 1 DAY));

-- ============================================================================
-- VERIFICATION
-- ============================================================================

SELECT
    @enterpriseId AS enterpriseId,
    @adminUserId AS adminUserId,
    @enterpriseUserId AS enterpriseUserId,
    @collectorId1 AS collectorId1,
    @citizenId1 AS citizenId1,
    @citizenId2 AS citizenId2,
    @wr1 AS reportId1,
    @wr2 AS reportId2;

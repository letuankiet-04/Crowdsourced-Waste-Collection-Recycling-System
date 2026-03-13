-- ============================================================================
-- POSTMAN COLLECTOR SEED DATA (MySQL)
-- ============================================================================

SET @now = NOW();

-- ============================================================================
-- 1. ROLES
-- ============================================================================

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
VALUES ('Test Enterprise', '1 Test Street', 'Ward 1', 'HCM', '0900000000', 'enterprise@test.com', 'LIC-TEST-001', 'TAX-TEST-001', 5000.00, 'RECYCLABLE', 'Ward 1', 'HCM', 'active', 0.00, @now, @now);
SET @enterpriseId = (SELECT id FROM enterprise WHERE email = 'enterprise@test.com');

-- ============================================================================
-- 3. USERS
-- ============================================================================

-- Collector User
INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES (
    'collector@test.com',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoO5rEo/pZ5lHppZArYrusS4x2ma/p3d.',
    'Test Collector',
    '0901111111',
    NULL,
    @roleCollectorId,
    NULL,
    'active',
    NULL,
    @now,
    @now
);
SET @collectorUserId = (SELECT id FROM users WHERE email = 'collector@test.com');

-- Citizen User
INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES (
    'citizen@test.com',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoO5rEo/pZ5lHppZArYrusS4x2ma/p3d.',
    'Test Citizen',
    '0902222222',
    NULL,
    @roleCitizenId,
    NULL,
    'active',
    NULL,
    @now,
    @now
);
SET @citizenUserId = (SELECT id FROM users WHERE email = 'citizen@test.com');

-- Enterprise User
INSERT IGNORE INTO users (email, password_hash, full_name, phone, avatar_url, role_id, enterprise_id, status, last_login, created_at, updated_at)
VALUES (
    'enterprise@test.com',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoO5rEo/pZ5lHppZArYrusS4x2ma/p3d.',
    'Test Enterprise',
    '0903333333',
    NULL,
    @roleEnterpriseId,
    @enterpriseId,
    'active',
    NULL,
    @now,
    @now
);
SET @enterpriseUserId = (SELECT id FROM users WHERE email = 'enterprise@test.com');

-- Update enterprise_id if not set correctly (though the insert above sets it)
UPDATE users
SET enterprise_id = @enterpriseId
WHERE id = @enterpriseUserId AND (enterprise_id IS NULL OR enterprise_id <> @enterpriseId);

-- ============================================================================
-- 4. PROFILES (Citizens & Collectors)
-- ============================================================================

-- Citizen Profile
INSERT IGNORE INTO citizens (user_id, email, full_name, password_hash, address, phone, ward, city, total_points, total_reports, valid_reports)
VALUES (@citizenUserId, 'citizen@test.com', 'Test Citizen', NULL, '10 Test Address', '0902222222', 'Ward 1', 'HCM', 0, 0, 0);
SET @citizenId = (SELECT id FROM citizens WHERE user_id = @citizenUserId);

-- Collector Profile
INSERT IGNORE INTO collectors (user_id, enterprise_id, email, full_name, employee_code, vehicle_type, vehicle_plate, status, last_location_update, total_collections, successful_collections, total_weight_collected, created_at)
VALUES (@collectorUserId, @enterpriseId, 'collector@test.com', 'Test Collector', 'EMP-TEST-001', 'Xe tải nhỏ', '59C-00001', 'AVAILABLE', @now, 0, 0, 0.00, @now);
SET @collectorId = (SELECT id FROM collectors WHERE user_id = @collectorUserId);

-- ============================================================================
-- 5. WASTE TYPES & CATEGORIES
-- ============================================================================

-- Waste Type: RECYCLABLE
INSERT IGNORE INTO waste_types (code, name, base_points, sla_hours, is_recyclable, created_at)
VALUES ('RECYCLABLE', 'Recyclable Waste', 20, 24, 1, @now);
SET @wtRecyclableId = (SELECT id FROM waste_types WHERE code = 'RECYCLABLE');

-- Category: Paper
INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, waste_type_id, created_at, updated_at)
VALUES ('Giấy', 'Giấy vụn, bìa carton', 'KG', 2250.0000, @wtRecyclableId, @now, @now);
SET @catPaperId = (SELECT id FROM waste_categories WHERE name = 'Giấy');

-- Category: Aluminum Can
INSERT IGNORE INTO waste_categories (name, description, unit, point_per_unit, waste_type_id, created_at, updated_at)
VALUES ('Lon nhôm', 'Lon nước ngọt/lon bia', 'KG', 180.0000, @wtRecyclableId, @now, @now);
SET @catCanId = (SELECT id FROM waste_categories WHERE name = 'Lon nhôm');

-- ============================================================================
-- 6. WASTE REPORTS
-- ============================================================================

-- Report 1: ASSIGNED
INSERT IGNORE INTO waste_reports (report_code, citizen_id, waste_type_id, description, estimated_weight, latitude, longitude, address, images, cloudinary_public_id, status, created_at, updated_at)
VALUES ('WR-PM-ASSIGNED', @citizenId, @wtRecyclableId, 'Postman seed report (assigned)', 1.00, 10.77653000, 106.70098000, 'PM address assigned', 'https://example.com/reports/WR-PM-ASSIGNED.jpg', NULL, 'PENDING', DATE_SUB(@now, INTERVAL 2 HOUR), DATE_SUB(@now, INTERVAL 2 HOUR));
SET @reportAssignedId = (SELECT id FROM waste_reports WHERE report_code = 'WR-PM-ASSIGNED');

-- Report 2: ACCEPTED
INSERT IGNORE INTO waste_reports (report_code, citizen_id, waste_type_id, description, estimated_weight, latitude, longitude, address, images, cloudinary_public_id, status, created_at, updated_at)
VALUES ('WR-PM-ACCEPTED', @citizenId, @wtRecyclableId, 'Postman seed report (accepted_collector)', 1.10, 10.77653100, 106.70098100, 'PM address accepted', 'https://example.com/reports/WR-PM-ACCEPTED.jpg', NULL, 'PENDING', DATE_SUB(@now, INTERVAL 3 HOUR), DATE_SUB(@now, INTERVAL 3 HOUR));
SET @reportAcceptedId = (SELECT id FROM waste_reports WHERE report_code = 'WR-PM-ACCEPTED');

-- Report 3: ON_THE_WAY
INSERT IGNORE INTO waste_reports (report_code, citizen_id, waste_type_id, description, estimated_weight, latitude, longitude, address, images, cloudinary_public_id, status, created_at, updated_at)
VALUES ('WR-PM-ONWAY', @citizenId, @wtRecyclableId, 'Postman seed report (on_the_way)', 2.50, 10.77653200, 106.70098200, 'PM address onway', 'https://example.com/reports/WR-PM-ONWAY.jpg', NULL, 'PENDING', DATE_SUB(@now, INTERVAL 4 HOUR), DATE_SUB(@now, INTERVAL 4 HOUR));
SET @reportOnWayId = (SELECT id FROM waste_reports WHERE report_code = 'WR-PM-ONWAY');

-- Report 4: COLLECTED
INSERT IGNORE INTO waste_reports (report_code, citizen_id, waste_type_id, description, estimated_weight, latitude, longitude, address, images, cloudinary_public_id, status, created_at, updated_at)
VALUES ('WR-PM-COLLECTED', @citizenId, @wtRecyclableId, 'Postman seed report (collected)', 3.00, 10.77653300, 106.70098300, 'PM address collected', 'https://example.com/reports/WR-PM-COLLECTED.jpg', NULL, 'PENDING', DATE_SUB(@now, INTERVAL 6 HOUR), DATE_SUB(@now, INTERVAL 6 HOUR));
SET @reportCollectedId = (SELECT id FROM waste_reports WHERE report_code = 'WR-PM-COLLECTED');

-- ============================================================================
-- 7. COLLECTION REQUESTS
-- ============================================================================

-- Request 1: ASSIGNED
INSERT IGNORE INTO collection_requests (request_code, report_id, enterprise_id, collector_id, status, rejection_reason, assigned_at, accepted_at, started_at, actual_weight_kg, collected_at, completed_at, created_at, updated_at)
VALUES ('CR-PM-001', @reportAssignedId, @enterpriseId, @collectorId, 'assigned', NULL, DATE_SUB(@now, INTERVAL 30 MINUTE), NULL, NULL, NULL, NULL, NULL, DATE_SUB(@now, INTERVAL 2 HOUR), DATE_SUB(@now, INTERVAL 30 MINUTE));
SET @crAssignedId = (SELECT id FROM collection_requests WHERE request_code = 'CR-PM-001');

-- Request 2: ACCEPTED
INSERT IGNORE INTO collection_requests (request_code, report_id, enterprise_id, collector_id, status, rejection_reason, assigned_at, accepted_at, started_at, actual_weight_kg, collected_at, completed_at, created_at, updated_at)
VALUES ('CR-PM-002', @reportAcceptedId, @enterpriseId, @collectorId, 'accepted_collector', NULL, DATE_SUB(@now, INTERVAL 3 HOUR), DATE_SUB(@now, INTERVAL 20 MINUTE), NULL, NULL, NULL, NULL, DATE_SUB(@now, INTERVAL 3 HOUR), DATE_SUB(@now, INTERVAL 20 MINUTE));
SET @crAcceptedId = (SELECT id FROM collection_requests WHERE request_code = 'CR-PM-002');

-- Request 3: ON_THE_WAY
INSERT IGNORE INTO collection_requests (request_code, report_id, enterprise_id, collector_id, status, rejection_reason, assigned_at, accepted_at, started_at, actual_weight_kg, collected_at, completed_at, created_at, updated_at)
VALUES ('CR-PM-003', @reportOnWayId, @enterpriseId, @collectorId, 'on_the_way', NULL, DATE_SUB(@now, INTERVAL 4 HOUR), DATE_SUB(@now, INTERVAL 3 HOUR), DATE_SUB(@now, INTERVAL 45 MINUTE), NULL, NULL, NULL, DATE_SUB(@now, INTERVAL 4 HOUR), DATE_SUB(@now, INTERVAL 45 MINUTE));
SET @crOnWayId = (SELECT id FROM collection_requests WHERE request_code = 'CR-PM-003');

-- Request 4: COLLECTED
INSERT IGNORE INTO collection_requests (request_code, report_id, enterprise_id, collector_id, status, rejection_reason, assigned_at, accepted_at, started_at, actual_weight_kg, collected_at, completed_at, created_at, updated_at)
VALUES ('CR-PM-004', @reportCollectedId, @enterpriseId, @collectorId, 'collected', NULL, DATE_SUB(@now, INTERVAL 6 HOUR), DATE_SUB(@now, INTERVAL 5 HOUR), DATE_SUB(@now, INTERVAL 4 HOUR), 4.25, DATE_SUB(@now, INTERVAL 10 MINUTE), NULL, DATE_SUB(@now, INTERVAL 6 HOUR), DATE_SUB(@now, INTERVAL 10 MINUTE));
SET @crCollectedId = (SELECT id FROM collection_requests WHERE request_code = 'CR-PM-004');

-- ============================================================================
-- VERIFICATION
-- ============================================================================

SELECT
    @collectorId AS collectorId,
    @enterpriseId AS enterpriseId,
    @citizenId AS citizenId,
    @catPaperId AS categoryPaperId,
    @catCanId AS categoryCanId,
    @crAssignedId AS requestAssignedId,
    @crAcceptedId AS requestAcceptedId,
    @crOnWayId AS requestOnTheWayId,
    @crCollectedId AS requestCollectedId;

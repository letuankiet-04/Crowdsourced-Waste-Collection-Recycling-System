-- ============================================================================
-- GENERATED TEST DATA FOR COLLECTOR WORKFLOW
-- Corrected to match Entity definitions exactly (MySQL)
-- ============================================================================

-- ============================================================================
-- 1. ROLES
-- Table: roles
-- Entity: Role
-- ============================================================================
INSERT IGNORE INTO roles (id, role_code, role_name, description, is_active, created_at) VALUES (1, 'ADMIN', 'Administrator', 'System Administrator', 1, NOW());
INSERT IGNORE INTO roles (id, role_code, role_name, description, is_active, created_at) VALUES (2, 'ENTERPRISE', 'Enterprise', 'Waste Collection Enterprise', 1, NOW());
INSERT IGNORE INTO roles (id, role_code, role_name, description, is_active, created_at) VALUES (3, 'COLLECTOR', 'Collector', 'Waste Collector', 1, NOW());
INSERT IGNORE INTO roles (id, role_code, role_name, description, is_active, created_at) VALUES (4, 'CITIZEN', 'Citizen', 'Resident/User', 1, NOW());

-- ============================================================================
-- 2. USERS
-- Table: users
-- Entity: User
-- ============================================================================

-- Enterprise User
INSERT IGNORE INTO users (id, email, password_hash, full_name, phone, role_id, status, created_at, updated_at)
VALUES (201, 'enterprise@example.com', '$2a$10$dummyHash', 'Green Earth Enterprise', '0901111111', 2, 'active', NOW(), NOW());

-- Collector User
INSERT IGNORE INTO users (id, email, password_hash, full_name, phone, role_id, status, created_at, updated_at)
VALUES (301, 'collector@example.com', '$2a$10$dummyHash', 'Nguyen Van Collector', '0902222222', 3, 'active', NOW(), NOW());

-- Citizen User
INSERT IGNORE INTO users (id, email, password_hash, full_name, phone, role_id, status, created_at, updated_at)
VALUES (401, 'citizen@example.com', '$2a$10$dummyHash', 'Le Thi Citizen', '0903333333', 4, 'active', NOW(), NOW());

-- ============================================================================
-- 3. ENTERPRISES
-- Table: enterprise (Singular name in Entity)
-- Entity: Enterprise
-- ============================================================================

INSERT IGNORE INTO enterprise (id, name, address, ward, city, phone, email, license_number, tax_code, capacity_kg_per_day, status, created_at, updated_at)
VALUES (1, 'Green Earth Enterprise', '123 Green St', 'Ward 1', 'HCM City', '0901111111', 'enterprise@example.com', 'LIC-001', 'TAX-001', 5000.00, 'active', NOW(), NOW());

-- Update User link
UPDATE users SET enterprise_id = 1 WHERE id = 201;

-- ============================================================================
-- 4. CITIZENS
-- Table: citizens
-- Entity: Citizen
-- ============================================================================

INSERT IGNORE INTO citizens (id, user_id, email, full_name, password_hash, address, phone, ward, city, total_points, total_reports, valid_reports)
VALUES (1, 401, 'citizen@example.com', 'Le Thi Citizen', '$2a$10$dummyHash', '456 Citizen Rd', '0903333333', 'Ward 2', 'HCM City', 100, 5, 5);

-- ============================================================================
-- 5. COLLECTORS
-- Table: collectors
-- Entity: Collector
-- ============================================================================

INSERT IGNORE INTO collectors (id, user_id, enterprise_id, email, full_name, employee_code, vehicle_type, vehicle_plate, status, last_location_update, total_collections, successful_collections, total_weight_collected, created_at)
VALUES (1, 301, 1, 'collector@example.com', 'Nguyen Van Collector', 'COL-001', 'TRUCK', '59C-12345', 'AVAILABLE', NOW(), 10, 10, 500.50, NOW());

-- ============================================================================
-- 6. WASTE TYPES
-- Table: waste_types
-- Entity: WasteType
-- ============================================================================

INSERT IGNORE INTO waste_types (id, code, name, base_points, is_recyclable, created_at)
VALUES (1, 'PLASTIC', 'Plastic Waste', 10, 1, NOW());

-- ============================================================================
-- 7. WASTE REPORTS
-- Table: waste_reports
-- Entity: WasteReport
-- ============================================================================

INSERT IGNORE INTO waste_reports (id, report_code, citizen_id, waste_type_id, description, latitude, longitude, address, status, images, created_at, updated_at)
VALUES (1, 'WR-20260205-001', 1, 1, 'Pile of plastic bottles', 10.762622, 106.660172, '456 Citizen Rd', 'PENDING', '["https://res.cloudinary.com/demo/image/upload/sample.jpg"]', NOW(), NOW());

-- ============================================================================
-- 8. COLLECTION REQUESTS
-- Table: collection_requests
-- Entity: CollectionRequest
-- ============================================================================

INSERT IGNORE INTO collection_requests (id, request_code, report_id, enterprise_id, collector_id, status, assigned_at, accepted_at, started_at, collected_at, actual_weight_kg, created_at, updated_at)
VALUES (1, 'REQ-20260205-001', 1, 1, 1, 'COLLECTED', 
        DATE_SUB(NOW(), INTERVAL 2 HOUR), 
        DATE_SUB(NOW(), INTERVAL 1 HOUR), 
        DATE_SUB(NOW(), INTERVAL 30 MINUTE), 
        NOW(), 
        15.50, -- actual_weight_kg from Entity
        DATE_SUB(NOW(), INTERVAL 2 HOUR), 
        NOW());

-- ============================================================================
-- 9. COLLECTOR REPORTS
-- Table: collector_reports
-- Entity: CollectorReport
-- ============================================================================

INSERT IGNORE INTO collector_reports (id, collection_request_id, collector_id, status, collector_note, actual_weight, collected_at, latitude, longitude, created_at)
VALUES (1, 1, 1, 'COMPLETED', 
        'Verified weight on site. All clear.', 
        15.50, -- actual_weight from Entity (NEW FIELD)
        NOW(), 
        10.762622, 
        106.660172, 
        NOW());

-- ============================================================================
-- 10. COLLECTOR REPORT IMAGES
-- Table: collector_report_images
-- Entity: CollectorReportImage
-- ============================================================================

INSERT IGNORE INTO collector_report_images (id, collector_report_id, image_url, image_public_id, created_at)
VALUES (1, 1, 'https://res.cloudinary.com/demo/image/upload/report_sample.jpg', 'report_sample_id', NOW());

-- ============================================================================
-- VERIFICATION
-- ============================================================================
-- PRINT 'Data Generation Complete.'; -- PRINT is not valid in MySQL script, using SELECT
SELECT 'Data Generation Complete.' AS status;
-- PRINT '--- Collector Reports ---';
SELECT '--- Collector Reports ---' AS section;
SELECT id, status, actual_weight, collector_note FROM collector_reports;

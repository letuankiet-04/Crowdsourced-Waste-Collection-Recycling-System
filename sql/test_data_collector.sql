-- ============================================================================
-- TEST DATA FOR COLLECTOR WORKFLOW (MySQL)
-- CollectorStatus enum: ONLINE, OFFLINE, SUSPEND
-- DB lưu theo enum name
-- ============================================================================

SET @now = NOW();

-- ============================================================================
-- 1. ROLES (đảm bảo có role COLLECTOR và ENTERPRISE)
-- ============================================================================

INSERT IGNORE INTO roles (role_code, role_name) VALUES ('COLLECTOR', 'Collector');
INSERT IGNORE INTO roles (role_code, role_name) VALUES ('ENTERPRISE', 'Enterprise');

SET @roleCollectorId = (SELECT id FROM roles WHERE role_code = 'COLLECTOR');

-- ============================================================================
-- 2. ENTERPRISE (đảm bảo có enterprise_id = 1)
-- ============================================================================

INSERT IGNORE INTO enterprise (id, name, address, phone, email, status, created_at, updated_at)
VALUES (1, 'Công ty Thu Gom Rác ABC', '123 Đường ABC, Quận 1, TP.HCM', '0901234567', 'enterprise1@example.com', 'active', @now, @now);

SET @enterpriseId = 1;

-- ============================================================================
-- 3. USERS (đảm bảo có user_id: 101, 102, 103, 104)
-- ============================================================================

INSERT IGNORE INTO users (id, email, password_hash, full_name, phone, role_id, status, created_at, updated_at)
VALUES
    (101, 'collector1@example.com', '$2a$10$dummyHash', 'Nguyễn Văn A', '0901111111', @roleCollectorId, 'active', '2024-01-15 08:00:00', '2024-01-15 08:00:00'),
    (102, 'collector2@example.com', '$2a$10$dummyHash', 'Trần Thị B', '0902222222', @roleCollectorId, 'active', '2024-03-20 09:00:00', '2024-03-20 09:00:00'),
    (103, 'collector3@example.com', '$2a$10$dummyHash', 'Lê Văn C', '0903333333', @roleCollectorId, 'inactive', '2023-11-01 10:00:00', '2023-11-01 10:00:00'),
    (104, 'collector4@example.com', '$2a$10$dummyHash', 'Phạm Thị D', '0904444444', @roleCollectorId, 'active', '2024-06-10 09:00:00', '2024-06-10 09:00:00');

-- ============================================================================
-- 4. COLLECTORS
-- ============================================================================

INSERT IGNORE INTO collectors (user_id, enterprise_id, email, full_name, employee_code,
                               vehicle_type, vehicle_plate, status,
                               last_location_update, created_at)
VALUES
    (101, @enterpriseId, 'collector1@example.com', 'Nguyễn Văn A', 'COL-001',
     'TRUCK', '29A-12345', 'ONLINE',
     '2026-02-05 07:00:00', '2024-01-15 08:00:00'),

    (102, @enterpriseId, 'collector2@example.com', 'Trần Thị B', 'COL-002',
     'MOTORCYCLE', '29B-98765', 'ONLINE',
     '2026-02-05 07:15:00', '2024-03-20 09:00:00'),

    (103, @enterpriseId, 'collector3@example.com', 'Lê Văn C', 'COL-003',
     'TRUCK', '29C-54321', 'OFFLINE',
     '2026-02-04 18:00:00', '2023-11-01 10:00:00'),

    (104, @enterpriseId, 'collector4@example.com', 'Phạm Thị D', 'COL-004',
     'MOTORCYCLE', '29D-11111', 'SUSPEND',
     '2026-02-03 16:00:00', '2024-06-10 09:00:00');

-- ============================================================================
-- VERIFICATION
-- ============================================================================

SELECT '=== COLLECTOR STATUSES ===' AS section;
SELECT
    employee_code,
    full_name,
    status,
    CASE
        WHEN status = 'ONLINE' THEN '✅ Đang online'
        WHEN status = 'OFFLINE' THEN '⏸️ Đang offline'
        WHEN status = 'SUSPEND' THEN '❌ Bị tạm ngừng'
    END AS status_desc
FROM collectors
WHERE enterprise_id = @enterpriseId
ORDER BY
    CASE status
        WHEN 'ONLINE' THEN 1
        WHEN 'OFFLINE' THEN 2
        WHEN 'SUSPEND' THEN 3
        ELSE 99
    END,
    id ASC;

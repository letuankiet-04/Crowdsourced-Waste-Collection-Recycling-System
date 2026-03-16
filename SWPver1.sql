DROP DATABASE IF EXISTS Waste;
CREATE DATABASE Waste CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE Waste;

-- Roles
CREATE TABLE roles (
    id INT AUTO_INCREMENT NOT NULL,
    role_code VARCHAR(20) NOT NULL,
    role_name VARCHAR(50) NOT NULL,
    description VARCHAR(255) NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uq_roles_role_code UNIQUE (role_code)
);

-- Permissions
CREATE TABLE permissions (
    id INT AUTO_INCREMENT NOT NULL,
    permission_code VARCHAR(100) NOT NULL,
    permission_name VARCHAR(255) NOT NULL,
    module VARCHAR(50) NOT NULL,
    description VARCHAR(500) NULL,
    CONSTRAINT pk_permissions PRIMARY KEY (id),
    CONSTRAINT uq_permissions_permission_code UNIQUE (permission_code)
);

-- Role Permissions
CREATE TABLE role_permissions (
    id INT AUTO_INCREMENT NOT NULL,
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    CONSTRAINT pk_role_permissions PRIMARY KEY (id),
    CONSTRAINT uq_role_permission UNIQUE (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles(id),
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions(id)
);

-- Enterprise
CREATE TABLE enterprise (
    id INT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500) NULL,
    ward VARCHAR(100) NULL,
    city VARCHAR(100) NULL,
    phone VARCHAR(20) NULL,
    email VARCHAR(255) NULL,
    license_number VARCHAR(50) NULL,
    tax_code VARCHAR(50) NULL,
    capacity_kg_per_day DECIMAL(10, 2) NULL,
    supported_waste_type_codes VARCHAR(255) NULL,
    service_wards TEXT NULL,
    service_cities TEXT NULL,
    status VARCHAR(20) NULL,
    total_collected_weight DECIMAL(10, 2) DEFAULT 0,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    CONSTRAINT pk_enterprise PRIMARY KEY (id)
);

-- Users
CREATE TABLE users (
    id INT AUTO_INCREMENT NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NULL,
    avatar_url VARCHAR(500) NULL,
    role_id INT NOT NULL,
    enterprise_id INT NULL,
    status VARCHAR(20) NULL,
    last_login DATETIME NULL,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT uq_users_enterprise_id UNIQUE (enterprise_id),
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id),
    CONSTRAINT fk_users_enterprise FOREIGN KEY (enterprise_id) REFERENCES enterprise(id)
);

-- Citizens
CREATE TABLE citizens (
    id INT AUTO_INCREMENT NOT NULL,
    user_id INT NOT NULL,
    email VARCHAR(255) NULL,
    full_name VARCHAR(255) NULL,
    password_hash VARCHAR(255) NULL,
    address VARCHAR(500) NULL,
    phone VARCHAR(20) NULL,
    ward VARCHAR(100) NULL,
    city VARCHAR(100) NULL,
    total_points INT NULL,
    total_reports INT NULL,
    valid_reports INT NULL,
    CONSTRAINT pk_citizens PRIMARY KEY (id),
    CONSTRAINT uq_citizens_user_id UNIQUE (user_id),
    CONSTRAINT fk_citizens_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Waste Types
CREATE TABLE waste_types (
    id INT AUTO_INCREMENT NOT NULL,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500) NULL,
    base_points DECIMAL(10,2) NULL,
    point_per_kg DECIMAL(10,2) NULL,
    sla_hours INT NULL,
    is_recyclable BOOLEAN DEFAULT FALSE,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    CONSTRAINT pk_waste_types PRIMARY KEY (id),
    CONSTRAINT uq_waste_types_code UNIQUE (code)
);

-- Waste Categories
CREATE TABLE waste_categories (
    id INT AUTO_INCREMENT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500) NULL,
    unit VARCHAR(20) NULL,
    point_per_unit DECIMAL(19,4) NULL,
    waste_type_id INT NULL,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    CONSTRAINT pk_waste_categories PRIMARY KEY (id),
    CONSTRAINT uq_waste_categories_name UNIQUE (name),
    CONSTRAINT fk_waste_categories_waste_type FOREIGN KEY (waste_type_id) REFERENCES waste_types(id)
);

-- Waste Reports
CREATE TABLE waste_reports (
    id INT AUTO_INCREMENT NOT NULL,
    report_code VARCHAR(50) NOT NULL,
    citizen_id INT NOT NULL,
    description VARCHAR(1000) NULL,
    waste_type_id INT NOT NULL,
    estimated_weight DECIMAL(10,2) NULL,
    latitude DECIMAL(10,8) NOT NULL,
    longitude DECIMAL(11,8) NOT NULL,
    address VARCHAR(500) NULL,
    images TEXT NULL,
    cloudinary_public_id VARCHAR(255) NULL,
    status VARCHAR(20) NULL,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    accepted_at DATETIME NULL,
    rejection_reason VARCHAR(500) NULL,
    CONSTRAINT pk_waste_reports PRIMARY KEY (id),
    CONSTRAINT uq_waste_reports_report_code UNIQUE (report_code),
    CONSTRAINT fk_waste_reports_citizen FOREIGN KEY (citizen_id) REFERENCES citizens(id),
    CONSTRAINT fk_waste_reports_waste_type FOREIGN KEY (waste_type_id) REFERENCES waste_types(id)
);

-- Waste Report Items
CREATE TABLE waste_report_items (
    id INT AUTO_INCREMENT NOT NULL,
    report_id INT NOT NULL,
    waste_category_id INT NOT NULL,
    quantity DECIMAL(19,4) NULL,
    unit_snapshot VARCHAR(20) NULL,
    created_at DATETIME NULL,
    CONSTRAINT pk_waste_report_items PRIMARY KEY (id),
    CONSTRAINT uq_waste_report_items UNIQUE (report_id, waste_category_id),
    CONSTRAINT fk_waste_report_items_report FOREIGN KEY (report_id) REFERENCES waste_reports(id),
    CONSTRAINT fk_waste_report_items_category FOREIGN KEY (waste_category_id) REFERENCES waste_categories(id)
);

-- Report Images
CREATE TABLE report_images (
    id INT AUTO_INCREMENT NOT NULL,
    report_id INT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    image_type VARCHAR(20) NULL,
    uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_report_images PRIMARY KEY (id),
    CONSTRAINT fk_report_images_report FOREIGN KEY (report_id) REFERENCES waste_reports(id)
);

-- Collectors
CREATE TABLE collectors (
    id INT AUTO_INCREMENT NOT NULL,
    user_id INT NOT NULL,
    enterprise_id INT NOT NULL,
    email VARCHAR(255) NULL,
    full_name VARCHAR(255) NULL,
    employee_code VARCHAR(50) NULL,
    vehicle_type VARCHAR(50) NULL,
    vehicle_plate VARCHAR(20) NULL,
    status VARCHAR(20) NULL,
    last_location_update DATETIME NULL,
    violation_count INT NULL,
    total_collections INT DEFAULT 0,
    successful_collections INT DEFAULT 0,
    total_weight_collected DECIMAL(10,2) DEFAULT 0,
    created_at DATETIME NULL,
    CONSTRAINT pk_collectors PRIMARY KEY (id),
    CONSTRAINT uq_collectors_user_id UNIQUE (user_id),
    CONSTRAINT fk_collectors_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_collectors_enterprise FOREIGN KEY (enterprise_id) REFERENCES enterprise(id)
);

-- Collection Requests
CREATE TABLE collection_requests (
    id INT AUTO_INCREMENT NOT NULL,
    request_code VARCHAR(20) NOT NULL,
    report_id INT NOT NULL,
    enterprise_id INT NOT NULL,
    collector_id INT NULL,
    status VARCHAR(20) NULL,
    rejection_reason VARCHAR(500) NULL,
    assigned_at DATETIME NULL,
    accepted_at DATETIME NULL,
    started_at DATETIME NULL,
    actual_weight_kg DECIMAL(10,2) NULL,
    collected_at DATETIME NULL,
    completed_at DATETIME NULL,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    sla_violated BOOLEAN DEFAULT FALSE,
    CONSTRAINT pk_collection_requests PRIMARY KEY (id),
    CONSTRAINT uq_collection_requests_request_code UNIQUE (request_code),
    CONSTRAINT fk_collection_requests_report FOREIGN KEY (report_id) REFERENCES waste_reports(id),
    CONSTRAINT fk_collection_requests_enterprise FOREIGN KEY (enterprise_id) REFERENCES enterprise(id),
    CONSTRAINT fk_collection_requests_collector FOREIGN KEY (collector_id) REFERENCES collectors(id)
);

-- Collection Tracking
CREATE TABLE collection_tracking (
    id INT AUTO_INCREMENT NOT NULL,
    collection_request_id INT NOT NULL,
    collector_id INT NOT NULL,
    action VARCHAR(50) NOT NULL,
    latitude DECIMAL(10,8) NULL,
    longitude DECIMAL(11,8) NULL,
    note VARCHAR(500) NULL,
    images TEXT NULL,
    created_at DATETIME NULL,
    CONSTRAINT pk_collection_tracking PRIMARY KEY (id),
    CONSTRAINT fk_collection_tracking_request FOREIGN KEY (collection_request_id) REFERENCES collection_requests(id),
    CONSTRAINT fk_collection_tracking_collector FOREIGN KEY (collector_id) REFERENCES collectors(id)
);

-- Collector Reports
CREATE TABLE collector_reports (
    id INT AUTO_INCREMENT NOT NULL,
    report_code VARCHAR(20) NULL,
    collection_request_id INT NOT NULL,
    collector_id INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    collector_note VARCHAR(1000) NULL,
    total_point INT NULL,
    actual_weight DECIMAL(10,2) NULL,
    actual_weight_recyclable DECIMAL(10,2) NULL,
    collected_at DATETIME NULL,
    latitude DECIMAL(10,8) NULL,
    longitude DECIMAL(11,8) NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_collector_reports PRIMARY KEY (id),
    CONSTRAINT uq_collector_reports_code UNIQUE (report_code),
    CONSTRAINT fk_collector_reports_request FOREIGN KEY (collection_request_id) REFERENCES collection_requests(id),
    CONSTRAINT fk_collector_reports_collector FOREIGN KEY (collector_id) REFERENCES collectors(id)
);

-- Collector Report Images
CREATE TABLE collector_report_images (
    id INT AUTO_INCREMENT NOT NULL,
    collector_report_id INT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    image_public_id VARCHAR(255) NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_collector_report_images PRIMARY KEY (id),
    CONSTRAINT fk_collector_report_images_report FOREIGN KEY (collector_report_id) REFERENCES collector_reports(id)
);

-- Collector Report Items
CREATE TABLE collector_report_items (
    id INT AUTO_INCREMENT NOT NULL,
    collector_report_id INT NOT NULL,
    waste_category_id INT NOT NULL,
    quantity DECIMAL(19,4) NOT NULL,
    unit_snapshot VARCHAR(20) NOT NULL,
    point_per_unit_snapshot DECIMAL(19,4) NOT NULL,
    total_point INT NOT NULL,
    created_at DATETIME NULL,
    CONSTRAINT pk_collector_report_items PRIMARY KEY (id),
    CONSTRAINT fk_collector_report_items_report FOREIGN KEY (collector_report_id) REFERENCES collector_reports(id),
    CONSTRAINT fk_collector_report_items_category FOREIGN KEY (waste_category_id) REFERENCES waste_categories(id)
);

-- Feedbacks
CREATE TABLE feedbacks (
    id INT AUTO_INCREMENT NOT NULL,
    feedback_code VARCHAR(20) NOT NULL,
    citizen_id INT NOT NULL,
    collection_request_id INT NULL,
    feedback_type VARCHAR(20) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    severity VARCHAR(20) NULL,
    status VARCHAR(20) NULL,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    CONSTRAINT pk_feedbacks PRIMARY KEY (id),
    CONSTRAINT uq_feedbacks_feedback_code UNIQUE (feedback_code),
    CONSTRAINT fk_feedbacks_citizen FOREIGN KEY (citizen_id) REFERENCES citizens(id),
    CONSTRAINT fk_feedbacks_collection_request FOREIGN KEY (collection_request_id) REFERENCES collection_requests(id)
);

-- Invalidated Tokens
CREATE TABLE invalidated_tokens (
    id VARCHAR(255) NOT NULL,
    expiry_time DATETIME NULL,
    CONSTRAINT pk_invalidated_tokens PRIMARY KEY (id)
);

-- Leaderboard
CREATE TABLE leaderboard (
    id INT AUTO_INCREMENT NOT NULL,
    citizen_id INT NOT NULL,
    ward VARCHAR(100) NULL,
    city VARCHAR(100) NULL,
    period_type VARCHAR(20) NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    total_points INT NOT NULL,
    total_reports INT NOT NULL,
    valid_reports INT NOT NULL,
    total_weight_kg DECIMAL(10,2) NULL,
    rank_position INT NULL,
    updated_at DATETIME NULL,
    CONSTRAINT pk_leaderboard PRIMARY KEY (id),
    CONSTRAINT fk_leaderboard_citizen FOREIGN KEY (citizen_id) REFERENCES citizens(id)
);

-- Point Transactions
CREATE TABLE point_transactions (
    id INT AUTO_INCREMENT NOT NULL,
    citizen_id INT NOT NULL,
    report_id INT NULL,
    collection_request_id INT NULL,
    points INT NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    description VARCHAR(500) NULL,
    balance_after INT NOT NULL,
    created_by INT NULL,
    created_at DATETIME NULL,
    CONSTRAINT pk_point_transactions PRIMARY KEY (id),
    CONSTRAINT fk_point_transactions_citizen FOREIGN KEY (citizen_id) REFERENCES citizens(id),
    CONSTRAINT fk_point_transactions_report FOREIGN KEY (report_id) REFERENCES waste_reports(id),
    CONSTRAINT fk_point_transactions_collection_request FOREIGN KEY (collection_request_id) REFERENCES collection_requests(id),
    CONSTRAINT fk_point_transactions_created_by FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Vouchers
CREATE TABLE vouchers (
    id INT AUTO_INCREMENT NOT NULL,
    voucher_code VARCHAR(10) NULL,
    banner_public_id VARCHAR(255) NULL,
    logo_public_id VARCHAR(255) NULL,
    banner_url VARCHAR(1000) NULL,
    logo_url VARCHAR(1000) NULL,
    title VARCHAR(255) NOT NULL,
    value_display VARCHAR(100) NULL,
    points_required INT NOT NULL,
    valid_until DATE NULL,
    active BOOLEAN NOT NULL,
    remaining_stock INT NULL,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    CONSTRAINT pk_vouchers PRIMARY KEY (id),
    CONSTRAINT uq_vouchers_code UNIQUE (voucher_code)
);

-- Voucher Terms
CREATE TABLE voucher_terms (
    voucher_id INT NOT NULL,
    term VARCHAR(500) NULL,
    CONSTRAINT fk_voucher_terms_voucher FOREIGN KEY (voucher_id) REFERENCES vouchers(id)
);

-- Voucher Redemptions
CREATE TABLE voucher_redemptions (
    id INT AUTO_INCREMENT NOT NULL,
    citizen_id INT NOT NULL,
    voucher_id INT NOT NULL,
    redemption_code VARCHAR(64) NOT NULL,
    points_spent INT NOT NULL,
    status VARCHAR(30) NOT NULL,
    redeemed_at DATETIME NOT NULL,
    CONSTRAINT pk_voucher_redemptions PRIMARY KEY (id),
    CONSTRAINT fk_voucher_redemptions_citizen FOREIGN KEY (citizen_id) REFERENCES citizens(id),
    CONSTRAINT fk_voucher_redemptions_voucher FOREIGN KEY (voucher_id) REFERENCES vouchers(id)
);

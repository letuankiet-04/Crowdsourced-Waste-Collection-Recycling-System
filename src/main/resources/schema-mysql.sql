SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS voucher_terms;
DROP TABLE IF EXISTS voucher_redemptions;
DROP TABLE IF EXISTS collector_report_images;
DROP TABLE IF EXISTS collector_report_items;
DROP TABLE IF EXISTS collector_reports;
DROP TABLE IF EXISTS collection_tracking;
DROP TABLE IF EXISTS point_transactions;
DROP TABLE IF EXISTS collector_feedbacks;
DROP TABLE IF EXISTS feedbacks;
DROP TABLE IF EXISTS collection_requests;
DROP TABLE IF EXISTS report_images;
DROP TABLE IF EXISTS waste_report_items;
DROP TABLE IF EXISTS waste_reports;
DROP TABLE IF EXISTS collectors;
DROP TABLE IF EXISTS leaderboard;
DROP TABLE IF EXISTS citizens;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS role_permissions;
DROP TABLE IF EXISTS permissions;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS vouchers;
DROP TABLE IF EXISTS waste_categories;
DROP TABLE IF EXISTS point_rule;
DROP TABLE IF EXISTS invalidated_tokens;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE roles (
  id INT NOT NULL AUTO_INCREMENT,
  role_code VARCHAR(20) NOT NULL,
  role_name VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_roles_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE permissions (
  id INT NOT NULL AUTO_INCREMENT,
  permission_code VARCHAR(100) NOT NULL,
  permission_name VARCHAR(255) NOT NULL,
  module VARCHAR(50) NOT NULL,
  description VARCHAR(500),
  PRIMARY KEY (id),
  UNIQUE KEY uk_permissions_permission_code (permission_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE enterprise (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  address VARCHAR(500),
  phone VARCHAR(20),
  email VARCHAR(255),
  service_wards TEXT,
  service_cities TEXT,
  status VARCHAR(20),
  created_at DATETIME,
  updated_at DATETIME,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE users (
  id INT NOT NULL AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  full_name VARCHAR(255) NOT NULL,
  phone VARCHAR(20),
  role_id INT NOT NULL,
  enterprise_id INT,
  status VARCHAR(20),
  created_at DATETIME,
  updated_at DATETIME,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_email (email),
  UNIQUE KEY uk_users_enterprise_id (enterprise_id),
  KEY idx_users_role_id (role_id),
  CONSTRAINT fk_users_role_id FOREIGN KEY (role_id) REFERENCES roles (id),
  CONSTRAINT fk_users_enterprise_id FOREIGN KEY (enterprise_id) REFERENCES enterprise (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE role_permissions (
  id INT NOT NULL AUTO_INCREMENT,
  role_id INT NOT NULL,
  permission_id INT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uq_role_permission (role_id, permission_id),
  KEY idx_role_permissions_role_id (role_id),
  KEY idx_role_permissions_permission_id (permission_id),
  CONSTRAINT fk_role_permissions_role_id FOREIGN KEY (role_id) REFERENCES roles (id),
  CONSTRAINT fk_role_permissions_permission_id FOREIGN KEY (permission_id) REFERENCES permissions (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE citizens (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  email VARCHAR(255),
  full_name VARCHAR(255),
  password_hash VARCHAR(255),
  address VARCHAR(500),
  phone VARCHAR(20),
  ward VARCHAR(100),
  city VARCHAR(100),
  total_points INT,
  total_reports INT,
  valid_reports INT,
  PRIMARY KEY (id),
  UNIQUE KEY uk_citizens_user_id (user_id),
  CONSTRAINT fk_citizens_user_id FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE collectors (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  enterprise_id INT NOT NULL,
  email VARCHAR(255),
  full_name VARCHAR(255),
  employee_code VARCHAR(50),
  vehicle_type VARCHAR(50),
  vehicle_plate VARCHAR(20),
  status VARCHAR(20),
  last_location_update DATETIME,
  violation_count INT,
  created_at DATETIME,
  PRIMARY KEY (id),
  UNIQUE KEY uk_collectors_user_id (user_id),
  KEY idx_collectors_enterprise_id (enterprise_id),
  CONSTRAINT fk_collectors_user_id FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_collectors_enterprise_id FOREIGN KEY (enterprise_id) REFERENCES enterprise (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE waste_categories (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(500),
  unit VARCHAR(20),
  point_per_unit DECIMAL(19,4),
  created_at DATETIME,
  updated_at DATETIME,
  PRIMARY KEY (id),
  UNIQUE KEY uk_waste_categories_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE waste_reports (
  id INT NOT NULL AUTO_INCREMENT,
  report_code VARCHAR(50) NOT NULL,
  citizen_id INT NOT NULL,
  description VARCHAR(1000),
  waste_type VARCHAR(20) NOT NULL,
  latitude DECIMAL(10,8) NOT NULL,
  longitude DECIMAL(11,8) NOT NULL,
  address VARCHAR(500),
  images TEXT,
  cloudinary_public_id VARCHAR(255),
  status VARCHAR(20),
  created_at DATETIME,
  updated_at DATETIME,
  accepted_at DATETIME,
  rejection_reason VARCHAR(500),
  PRIMARY KEY (id),
  UNIQUE KEY uk_waste_reports_report_code (report_code),
  KEY idx_wr_citizen_status_created (citizen_id, status, created_at),
  KEY idx_wr_status_created (status, created_at),
  CONSTRAINT fk_waste_reports_citizen_id FOREIGN KEY (citizen_id) REFERENCES citizens (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE waste_report_items (
  id INT NOT NULL AUTO_INCREMENT,
  report_id INT NOT NULL,
  waste_category_id INT NOT NULL,
  unit_snapshot VARCHAR(20),
  quantity DECIMAL(19,4),
  created_at DATETIME,
  PRIMARY KEY (id),
  KEY idx_waste_report_items_report_id (report_id),
  KEY idx_waste_report_items_waste_category_id (waste_category_id),
  CONSTRAINT fk_waste_report_items_report_id FOREIGN KEY (report_id) REFERENCES waste_reports (id),
  CONSTRAINT fk_waste_report_items_waste_category_id FOREIGN KEY (waste_category_id) REFERENCES waste_categories (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE report_images (
  id INT NOT NULL AUTO_INCREMENT,
  report_id INT NOT NULL,
  image_url VARCHAR(500) NOT NULL,
  image_type VARCHAR(20),
  uploaded_at DATETIME,
  PRIMARY KEY (id),
  KEY idx_report_images_report_id (report_id),
  CONSTRAINT fk_report_images_report_id FOREIGN KEY (report_id) REFERENCES waste_reports (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE collection_requests (
  id INT NOT NULL AUTO_INCREMENT,
  request_code VARCHAR(20) NOT NULL,
  report_id INT NOT NULL,
  enterprise_id INT NOT NULL,
  collector_id INT,
  status VARCHAR(20),
  rejection_reason VARCHAR(500),
  assigned_at DATETIME,
  accepted_at DATETIME,
  started_at DATETIME,
  actual_weight_kg DECIMAL(10,2),
  collected_at DATETIME,
  completed_at DATETIME,
  created_at DATETIME,
  updated_at DATETIME,
  sla_violated TINYINT(1),
  PRIMARY KEY (id),
  UNIQUE KEY uk_collection_requests_request_code (request_code),
  KEY idx_cr_collector_status (collector_id, status),
  KEY idx_cr_enterprise_status_created (enterprise_id, status, created_at),
  KEY idx_cr_status_assigned_at (status, assigned_at),
  KEY idx_cr_sla_started_flag (sla_violated, started_at),
  KEY idx_cr_report_id (report_id),
  CONSTRAINT fk_collection_requests_report_id FOREIGN KEY (report_id) REFERENCES waste_reports (id),
  CONSTRAINT fk_collection_requests_enterprise_id FOREIGN KEY (enterprise_id) REFERENCES enterprise (id),
  CONSTRAINT fk_collection_requests_collector_id FOREIGN KEY (collector_id) REFERENCES collectors (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE collection_tracking (
  id INT NOT NULL AUTO_INCREMENT,
  collection_request_id INT NOT NULL,
  collector_id INT NOT NULL,
  action VARCHAR(50) NOT NULL,
  latitude DECIMAL(10,8),
  longitude DECIMAL(11,8),
  note VARCHAR(500),
  created_at DATETIME,
  PRIMARY KEY (id),
  KEY idx_collection_tracking_collection_request_id (collection_request_id),
  KEY idx_collection_tracking_collector_id (collector_id),
  CONSTRAINT fk_collection_tracking_collection_request_id FOREIGN KEY (collection_request_id) REFERENCES collection_requests (id),
  CONSTRAINT fk_collection_tracking_collector_id FOREIGN KEY (collector_id) REFERENCES collectors (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE collector_reports (
  id INT NOT NULL AUTO_INCREMENT,
  report_code VARCHAR(20),
  collection_request_id INT NOT NULL,
  collector_id INT NOT NULL,
  status VARCHAR(20) NOT NULL,
  collector_note VARCHAR(1000),
  total_point INT,
  collected_at DATETIME,
  latitude DECIMAL(10,8),
  longitude DECIMAL(11,8),
  created_at DATETIME,
  PRIMARY KEY (id),
  UNIQUE KEY uk_collector_reports_report_code (report_code),
  KEY idx_collector_reports_collection_request_id (collection_request_id),
  KEY idx_collector_reports_collector_id (collector_id),
  CONSTRAINT fk_collector_reports_collection_request_id FOREIGN KEY (collection_request_id) REFERENCES collection_requests (id),
  CONSTRAINT fk_collector_reports_collector_id FOREIGN KEY (collector_id) REFERENCES collectors (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE collector_report_items (
  id INT NOT NULL AUTO_INCREMENT,
  collector_report_id INT NOT NULL,
  waste_category_id INT NOT NULL,
  quantity DECIMAL(19,4) NOT NULL,
  unit_snapshot VARCHAR(20) NOT NULL,
  point_per_unit_snapshot DECIMAL(19,4) NOT NULL,
  total_point INT NOT NULL,
  created_at DATETIME,
  PRIMARY KEY (id),
  KEY idx_collector_report_items_collector_report_id (collector_report_id),
  KEY idx_collector_report_items_waste_category_id (waste_category_id),
  CONSTRAINT fk_collector_report_items_collector_report_id FOREIGN KEY (collector_report_id) REFERENCES collector_reports (id),
  CONSTRAINT fk_collector_report_items_waste_category_id FOREIGN KEY (waste_category_id) REFERENCES waste_categories (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE collector_report_images (
  id INT NOT NULL AUTO_INCREMENT,
  collector_report_id INT NOT NULL,
  image_url VARCHAR(500) NOT NULL,
  image_public_id VARCHAR(255),
  created_at DATETIME,
  PRIMARY KEY (id),
  KEY idx_collector_report_images_collector_report_id (collector_report_id),
  CONSTRAINT fk_collector_report_images_collector_report_id FOREIGN KEY (collector_report_id) REFERENCES collector_reports (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE point_transactions (
  id INT NOT NULL AUTO_INCREMENT,
  citizen_id INT NOT NULL,
  report_id INT,
  collection_request_id INT,
  points INT NOT NULL,
  transaction_type VARCHAR(30) NOT NULL,
  description VARCHAR(500),
  balance_after INT NOT NULL,
  created_by INT,
  created_at DATETIME,
  PRIMARY KEY (id),
  KEY idx_pt_citizen_created (citizen_id, created_at),
  KEY idx_pt_citizen_type_created (citizen_id, transaction_type, created_at),
  KEY idx_point_transactions_report_id (report_id),
  KEY idx_point_transactions_collection_request_id (collection_request_id),
  KEY idx_point_transactions_created_by (created_by),
  CONSTRAINT fk_point_transactions_citizen_id FOREIGN KEY (citizen_id) REFERENCES citizens (id),
  CONSTRAINT fk_point_transactions_report_id FOREIGN KEY (report_id) REFERENCES waste_reports (id),
  CONSTRAINT fk_point_transactions_collection_request_id FOREIGN KEY (collection_request_id) REFERENCES collection_requests (id),
  CONSTRAINT fk_point_transactions_created_by FOREIGN KEY (created_by) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE vouchers (
  id INT NOT NULL AUTO_INCREMENT,
  voucher_code VARCHAR(10),
  banner_public_id VARCHAR(255),
  logo_public_id VARCHAR(255),
  banner_url VARCHAR(1000),
  logo_url VARCHAR(1000),
  title VARCHAR(255) NOT NULL,
  value_display VARCHAR(100),
  points_required INT NOT NULL,
  valid_until DATE,
  active TINYINT(1) NOT NULL,
  remaining_stock INT,
  created_at DATETIME,
  updated_at DATETIME,
  PRIMARY KEY (id),
  UNIQUE KEY uk_vouchers_voucher_code (voucher_code),
  KEY idx_vc_active_id (active, id),
  KEY idx_vc_title (title)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE voucher_terms (
  voucher_id INT NOT NULL,
  term VARCHAR(500),
  KEY idx_voucher_terms_voucher_id (voucher_id),
  CONSTRAINT fk_voucher_terms_voucher_id FOREIGN KEY (voucher_id) REFERENCES vouchers (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE voucher_redemptions (
  id INT NOT NULL AUTO_INCREMENT,
  citizen_id INT NOT NULL,
  voucher_id INT NOT NULL,
  redemption_code VARCHAR(64) NOT NULL,
  points_spent INT NOT NULL,
  status VARCHAR(30) NOT NULL,
  redeemed_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  KEY idx_voucher_redemptions_citizen_id (citizen_id),
  KEY idx_voucher_redemptions_voucher_id (voucher_id),
  CONSTRAINT fk_voucher_redemptions_citizen_id FOREIGN KEY (citizen_id) REFERENCES citizens (id),
  CONSTRAINT fk_voucher_redemptions_voucher_id FOREIGN KEY (voucher_id) REFERENCES vouchers (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE feedbacks (
  id INT NOT NULL AUTO_INCREMENT,
  feedback_code VARCHAR(20) NOT NULL,
  citizen_id INT NOT NULL,
  collection_request_id INT,
  feedback_type VARCHAR(20) NOT NULL,
  subject VARCHAR(255) NOT NULL,
  content TEXT NOT NULL,
  resolution VARCHAR(255),
  status VARCHAR(20),
  rating INT,
  created_at DATETIME,
  updated_at DATETIME,
  PRIMARY KEY (id),
  UNIQUE KEY uk_feedbacks_feedback_code (feedback_code),
  KEY idx_fb_citizen_created (citizen_id, created_at),
  KEY idx_fb_status_created (status, created_at),
  KEY idx_feedbacks_collection_request_id (collection_request_id),
  CONSTRAINT fk_feedbacks_citizen_id FOREIGN KEY (citizen_id) REFERENCES citizens (id),
  CONSTRAINT fk_feedbacks_collection_request_id FOREIGN KEY (collection_request_id) REFERENCES collection_requests (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE collector_feedbacks (
  id INT NOT NULL AUTO_INCREMENT,
  feedback_code VARCHAR(50) NOT NULL,
  collector_id INT NOT NULL,
  collection_request_id INT,
  feedback_type VARCHAR(50) NOT NULL,
  subject VARCHAR(255) NOT NULL,
  content TEXT NOT NULL,
  resolution VARCHAR(255),
  status VARCHAR(20),
  rating INT,
  created_at DATETIME,
  updated_at DATETIME,
  PRIMARY KEY (id),
  UNIQUE KEY uk_collector_feedbacks_feedback_code (feedback_code),
  KEY idx_collector_feedbacks_collector_id (collector_id),
  KEY idx_collector_feedbacks_collection_request_id (collection_request_id),
  CONSTRAINT fk_collector_feedbacks_collector_id FOREIGN KEY (collector_id) REFERENCES collectors (id),
  CONSTRAINT fk_collector_feedbacks_collection_request_id FOREIGN KEY (collection_request_id) REFERENCES collection_requests (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE leaderboard (
  id INT NOT NULL AUTO_INCREMENT,
  citizen_id INT NOT NULL,
  ward VARCHAR(100),
  city VARCHAR(100),
  period_type VARCHAR(20) NOT NULL,
  period_start DATE NOT NULL,
  period_end DATE NOT NULL,
  total_points INT NOT NULL,
  total_reports INT NOT NULL,
  valid_reports INT NOT NULL,
  total_weight_kg DECIMAL(10,2),
  rank_position INT,
  updated_at DATETIME,
  PRIMARY KEY (id),
  KEY idx_leaderboard_citizen_id (citizen_id),
  CONSTRAINT fk_leaderboard_citizen_id FOREIGN KEY (citizen_id) REFERENCES citizens (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE point_rule (
  id INT NOT NULL,
  content TEXT,
  updated_at DATETIME,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE invalidated_tokens (
  id VARCHAR(255) NOT NULL,
  expiry_time DATETIME,
  PRIMARY KEY (id),
  KEY idx_inv_token_expiry (expiry_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

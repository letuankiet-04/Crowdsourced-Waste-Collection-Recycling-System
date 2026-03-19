CREATE TABLE roles (
  id INT NOT NULL AUTO_INCREMENT,
  role_code VARCHAR(20) NOT NULL,
  role_name VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (role_code)
);

CREATE TABLE permissions (
  id INT NOT NULL AUTO_INCREMENT,
  permission_code VARCHAR(100) NOT NULL,
  permission_name VARCHAR(255) NOT NULL,
  module VARCHAR(50) NOT NULL,
  description VARCHAR(500),
  PRIMARY KEY (id),
  UNIQUE (permission_code)
);

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
);

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
  UNIQUE (email),
  UNIQUE (enterprise_id),
  FOREIGN KEY (role_id) REFERENCES roles (id),
  FOREIGN KEY (enterprise_id) REFERENCES enterprise (id)
);

CREATE TABLE role_permissions (
  id INT NOT NULL AUTO_INCREMENT,
  role_id INT NOT NULL,
  permission_id INT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (role_id, permission_id),
  FOREIGN KEY (role_id) REFERENCES roles (id),
  FOREIGN KEY (permission_id) REFERENCES permissions (id)
);

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
  UNIQUE (user_id),
  FOREIGN KEY (user_id) REFERENCES users (id)
);

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
  UNIQUE (user_id),
  FOREIGN KEY (user_id) REFERENCES users (id),
  FOREIGN KEY (enterprise_id) REFERENCES enterprise (id)
);

CREATE TABLE waste_categories (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(500),
  unit VARCHAR(20),
  point_per_unit DECIMAL(19,4),
  created_at DATETIME,
  updated_at DATETIME,
  PRIMARY KEY (id),
  UNIQUE (name)
);

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
  UNIQUE (report_code),
  FOREIGN KEY (citizen_id) REFERENCES citizens (id)
);

CREATE TABLE waste_report_items (
  id INT NOT NULL AUTO_INCREMENT,
  report_id INT NOT NULL,
  waste_category_id INT NOT NULL,
  unit_snapshot VARCHAR(20),
  quantity DECIMAL(19,4),
  created_at DATETIME,
  PRIMARY KEY (id),
  FOREIGN KEY (report_id) REFERENCES waste_reports (id),
  FOREIGN KEY (waste_category_id) REFERENCES waste_categories (id)
);

CREATE TABLE report_images (
  id INT NOT NULL AUTO_INCREMENT,
  report_id INT NOT NULL,
  image_url VARCHAR(500) NOT NULL,
  image_type VARCHAR(20),
  uploaded_at DATETIME,
  PRIMARY KEY (id),
  FOREIGN KEY (report_id) REFERENCES waste_reports (id)
);

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
  UNIQUE (request_code),
  FOREIGN KEY (report_id) REFERENCES waste_reports (id),
  FOREIGN KEY (enterprise_id) REFERENCES enterprise (id),
  FOREIGN KEY (collector_id) REFERENCES collectors (id)
);

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
  FOREIGN KEY (collection_request_id) REFERENCES collection_requests (id),
  FOREIGN KEY (collector_id) REFERENCES collectors (id)
);

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
  UNIQUE (report_code),
  FOREIGN KEY (collection_request_id) REFERENCES collection_requests (id),
  FOREIGN KEY (collector_id) REFERENCES collectors (id)
);

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
  FOREIGN KEY (collector_report_id) REFERENCES collector_reports (id),
  FOREIGN KEY (waste_category_id) REFERENCES waste_categories (id)
);

CREATE TABLE collector_report_images (
  id INT NOT NULL AUTO_INCREMENT,
  collector_report_id INT NOT NULL,
  image_url VARCHAR(500) NOT NULL,
  image_public_id VARCHAR(255),
  created_at DATETIME,
  PRIMARY KEY (id),
  FOREIGN KEY (collector_report_id) REFERENCES collector_reports (id)
);

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
  FOREIGN KEY (citizen_id) REFERENCES citizens (id),
  FOREIGN KEY (report_id) REFERENCES waste_reports (id),
  FOREIGN KEY (collection_request_id) REFERENCES collection_requests (id),
  FOREIGN KEY (created_by) REFERENCES users (id)
);

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
  UNIQUE (voucher_code)
);

CREATE TABLE voucher_terms (
  voucher_id INT NOT NULL,
  term VARCHAR(500),
  FOREIGN KEY (voucher_id) REFERENCES vouchers (id)
);

CREATE TABLE voucher_redemptions (
  id INT NOT NULL AUTO_INCREMENT,
  citizen_id INT NOT NULL,
  voucher_id INT NOT NULL,
  redemption_code VARCHAR(64) NOT NULL,
  points_spent INT NOT NULL,
  status VARCHAR(30) NOT NULL,
  redeemed_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (citizen_id) REFERENCES citizens (id),
  FOREIGN KEY (voucher_id) REFERENCES vouchers (id)
);

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
  UNIQUE (feedback_code),
  FOREIGN KEY (citizen_id) REFERENCES citizens (id),
  FOREIGN KEY (collection_request_id) REFERENCES collection_requests (id)
);

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
  UNIQUE (feedback_code),
  FOREIGN KEY (collector_id) REFERENCES collectors (id),
  FOREIGN KEY (collection_request_id) REFERENCES collection_requests (id)
);

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
  FOREIGN KEY (citizen_id) REFERENCES citizens (id)
);

CREATE TABLE point_rule (
  id INT NOT NULL,
  content TEXT,
  updated_at DATETIME,
  PRIMARY KEY (id)
);

CREATE TABLE invalidated_tokens (
  id VARCHAR(255) NOT NULL,
  expiry_time DATETIME,
  PRIMARY KEY (id)
);


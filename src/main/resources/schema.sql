-- H2 compatible schema for tests

-- Users and identities
CREATE TABLE IF NOT EXISTS users (
  id INT NOT NULL AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  is_admin BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS user_configs (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  custom_css CLOB,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT uq_user_configs_user UNIQUE (user_id),
  CONSTRAINT fk_user_configs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Identity/provider mapping
CREATE TABLE IF NOT EXISTS user_identity (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  email VARCHAR(255) NOT NULL,
  customer_id VARCHAR(255) DEFAULT NULL,
  provider VARCHAR(64) NOT NULL,
  activated BOOLEAN NOT NULL DEFAULT FALSE,
  data VARCHAR(10000) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT uq_user_identity_email UNIQUE (email),
  CONSTRAINT uq_user_identity_customer UNIQUE (customer_id),
  CONSTRAINT fk_user_identity_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Enum for habit status
CREATE TABLE IF NOT EXISTS habit_status_enum (
  status_name VARCHAR(32) NOT NULL,
  PRIMARY KEY (status_name)
);

INSERT INTO habit_status_enum (status_name) VALUES ('ACTIVE'), ('ARCHIVED'), ('SOFT_DELETED');

-- Habits
CREATE TABLE IF NOT EXISTS habits (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  name VARCHAR(50) NOT NULL,
  tags VARCHAR(1000) NOT NULL,
  star BOOLEAN NOT NULL DEFAULT FALSE,
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  period_type CHAR(1) DEFAULT NULL,
  period_count INT DEFAULT NULL,
  target_count INT DEFAULT NULL,
  order_index INT DEFAULT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_habits_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT ck_habits_status FOREIGN KEY (status) REFERENCES habit_status_enum(status_name)
);

-- Habit completions
CREATE TABLE IF NOT EXISTS habit_completions (
  id INT NOT NULL AUTO_INCREMENT,
  habit_id INT NOT NULL,
  completion_date DATE NOT NULL,
  done BOOLEAN NOT NULL DEFAULT TRUE,
  note VARCHAR(1024) DEFAULT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT uq_habit_day UNIQUE (habit_id, completion_date),
  CONSTRAINT fk_habit_completions_habit FOREIGN KEY (habit_id) REFERENCES habits(id) ON DELETE CASCADE
);

-- User images/assets
CREATE TABLE IF NOT EXISTS user_images (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  blob BLOB NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_user_images_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Optional audit events
CREATE TABLE IF NOT EXISTS audit_events (
  event_id INT NOT NULL AUTO_INCREMENT,
  type VARCHAR(64) NOT NULL,
  payload VARCHAR(10000) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (event_id)
); 
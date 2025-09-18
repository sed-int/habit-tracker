-- H2 compatible schema for tests

-- Users and identities
CREATE TABLE IF NOT EXISTS users (
  id INT NOT NULL AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  is_admin TINYINT(1) NOT NULL DEFAULT 0,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS user_configs (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  custom_css LONGTEXT COMMENT 'User-defined CSS for UI theming',
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  UNIQUE KEY uq_user_configs_user (user_id),
  CONSTRAINT fk_user_configs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Identity/provider mapping
CREATE TABLE IF NOT EXISTS user_identity (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  email VARCHAR(255) NOT NULL,
  customer_id VARCHAR(255) DEFAULT NULL,
  provider VARCHAR(64) NOT NULL COMMENT 'OAuth provider (google/kakao/naver)',
  activated TINYINT(1) NOT NULL DEFAULT 0,
  data JSON NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  UNIQUE KEY uq_user_identity_email (email),
  UNIQUE KEY uq_user_identity_customer (customer_id),
  KEY idx_user_identity_user (user_id),
  CONSTRAINT fk_user_identity_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Enum for habit status
CREATE TABLE IF NOT EXISTS habit_status_enum (
  value VARCHAR(32) NOT NULL,
  PRIMARY KEY (value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT IGNORE INTO habit_status_enum (value) VALUES ('ACTIVE'), ('ARCHIVED'), ('SOFT_DELETED');

-- Habits
CREATE TABLE IF NOT EXISTS habits (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  name VARCHAR(50) NOT NULL,
  tags JSON NOT NULL COMMENT 'Array of tag strings',
  star TINYINT(1) NOT NULL DEFAULT 0,
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  period_type CHAR(1) DEFAULT NULL, -- D/W/M/Y
  period_count INT DEFAULT NULL,
  target_count INT DEFAULT NULL,
  order_index INT DEFAULT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  KEY idx_habits_user_status (user_id, status),
  KEY idx_habits_user_order (user_id, order_index),
  CONSTRAINT fk_habits_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT ck_habits_status FOREIGN KEY (status) REFERENCES habit_status_enum(value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Habit completions
CREATE TABLE IF NOT EXISTS habit_completions (
  id INT NOT NULL AUTO_INCREMENT,
  habit_id INT NOT NULL,
  `day` DATE NOT NULL COMMENT 'Calendar date (YYYY-MM-DD); unique per habit with uq_habit_day',
  done TINYINT(1) NOT NULL DEFAULT 1,
  note VARCHAR(1024) DEFAULT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  UNIQUE KEY uq_habit_day (habit_id, `day`),
  KEY idx_habit_completions_habit_day (habit_id, `day`),
  CONSTRAINT fk_habit_completions_habit FOREIGN KEY (habit_id) REFERENCES habits(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- User images/assets
CREATE TABLE IF NOT EXISTS user_images (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  `blob` LONGBLOB NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  KEY idx_user_images_user (user_id),
  CONSTRAINT fk_user_images_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Optional audit events
CREATE TABLE IF NOT EXISTS audit_events (
  event_id INT NOT NULL AUTO_INCREMENT,
  type VARCHAR(64) NOT NULL,
  payload JSON NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci; 
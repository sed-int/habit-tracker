-- Test data for development
-- This file is automatically executed by Spring Boot on startup

-- Insert test users
INSERT INTO users (email, password_hash, active, admin, failed_login_attempts, created_at, updated_at)
VALUES
  ('test@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, false, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, true, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('user@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, false, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
-- Password for all users: "password"

-- Insert test habits for user 1 (test@example.com)
INSERT INTO habits (user_id, title, description, tags, star, status, period_type, period_count, target_count, order_index, created_at, updated_at)
VALUES
  (1, 'Morning Meditation', 'Meditate for 10 minutes every morning', 'health,mindfulness', true, 'ACTIVE', 'D', 1, 30, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (1, 'Read Books', 'Read at least 20 pages daily', 'education,reading', true, 'ACTIVE', 'D', 1, 365, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (1, 'Exercise', 'Go to the gym or workout at home', 'fitness,health', false, 'ACTIVE', 'D', 1, 100, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (1, 'Learn Spanish', 'Practice Spanish on Duolingo', 'language,education', false, 'ACTIVE', 'D', 1, 90, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (1, 'Write Journal', 'Daily journaling before bed', 'mental-health,reflection', false, 'ARCHIVED', 'D', 1, 50, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert test habits for user 3 (user@example.com)
INSERT INTO habits (user_id, title, description, tags, star, status, period_type, period_count, target_count, order_index, created_at, updated_at)
VALUES
  (3, 'Drink Water', 'Drink 8 glasses of water daily', 'health,hydration', true, 'ACTIVE', 'D', 1, 30, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (3, 'Practice Piano', 'Practice piano for 30 minutes', 'music,skill', true, 'ACTIVE', 'D', 1, 60, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert some habit completions
INSERT INTO habit_completions (habit_id, completion_date, done, note, created_at, updated_at)
VALUES
  (1, CURRENT_DATE, true, 'Great session today!', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (1, DATEADD('DAY', -1, CURRENT_DATE), true, 'Felt calm and focused', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (1, DATEADD('DAY', -2, CURRENT_DATE), true, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, CURRENT_DATE, true, 'Read 25 pages of "Atomic Habits"', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, DATEADD('DAY', -1, CURRENT_DATE), true, 'Finished chapter 3', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (3, DATEADD('DAY', -1, CURRENT_DATE), true, 'Leg day - felt strong', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (6, CURRENT_DATE, true, '2L so far', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
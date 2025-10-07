# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build System

This project uses **Gradle** with Java 25 and Spring Boot 3.5.5.

### Common Commands

**Build the project:**
```bash
./gradlew build
```

**Run tests:**
```bash
./gradlew test
```

**Run a specific test class:**
```bash
./gradlew test --tests com.example.demo_habit.controller.UserControllerTest
```

**Run the application:**
```bash
./gradlew bootRun
```

**Clean build artifacts:**
```bash
./gradlew clean
```

## Architecture Overview

This is a Spring Boot REST API backend for habit tracking with OAuth2 authentication planned.

### Package Structure

- `controller/` - REST API endpoints (base: `/api/v1`)
- `service/` - Business logic layer
- `domain/` - JPA entities (User, Habit, etc.)
- `repository/` - Spring Data JPA repositories
- `dto/` - Request/response objects
- `config/` - Spring configuration (currently in `common/config/`)
- `common/` - Shared utilities and configurations

### Database

**Primary Database:** MySQL 8.x (planned production)
**Test Database:** H2 (in-memory for tests)

The schema is defined in `src/main/resources/schema.sql` and includes:
- `users` - User accounts with email/password authentication
- `user_identity` - OAuth provider mappings (Google OAuth2 planned)
- `habits` - Habit records with tags, status, and period tracking
- `habit_completions` - Daily completion records
- `user_configs`, `user_images`, `audit_events` - Supporting tables

### Domain Model Key Points

**User Entity** (`domain/User.java`):
- Has login failure tracking with automatic deactivation after 3 failed attempts
- Uses `activate()` and `recordLoginFailure()` methods to manage account status
- `active` flag controls whether user can log in

**Habit Entity** (`domain/Habit.java`):
- Links to User via `@ManyToOne` relationship
- Status field references `habit_status_enum` table (ACTIVE, ARCHIVED, SOFT_DELETED)
- Has period tracking (`periodType`, `periodCount`, `targetCount`)
- Tag system stored as comma-separated string

### Security Configuration

**Current State** (`common/config/SecurityConfig.java`):
- CSRF is disabled
- All endpoints permit all requests (authentication not yet implemented)
- This is temporary; OAuth2 + JWT authentication is planned

**Planned Authentication:**
- Email/password login with BCrypt hashing (see `UserService`)
- Google OAuth2 integration
- JWT token-based auth with Redis for token management
- Spring Security enabled for protected endpoints

### Service Layer Patterns

**UserService** (`service/UserService.java`):
- Registration flow creates inactive users by default
- Email confirmation process is stubbed (`confirmRegistration` returns true)
- Password encoding uses BCryptPasswordEncoder
- Duplicate email check throws RuntimeException (should be converted to custom exception)

### Known Development Status

- User registration and basic CRUD is implemented
- Authentication/authorization is stubbed but not functional
- Habit CRUD operations are not yet implemented
- Email confirmation system is planned but not implemented
- API documentation with Swagger is planned but not yet configured

### Testing

- Tests use H2 in-memory database
- Schema is compatible with both H2 and MySQL
- Tests are located in `src/test/java/com/example/demo_habit/`
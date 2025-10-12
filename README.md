# Habit Tracker

Full-stack habit tracking application with Spring Boot backend and React frontend.

## Structure

```
habit-tracker/
├── backend/          # Spring Boot API
└── frontend/         # React web app
```

## Getting Started

### Prerequisites

- Java 25
- Node.js 18+
- npm or yarn

### Backend (Spring Boot)

```bash
cd backend
./gradlew bootRun
```

Backend runs on `http://localhost:8080`

### Frontend (React)

```bash
cd frontend
npm install
npm start
```

Frontend runs on `http://localhost:3000`

## Tech Stack

**Backend:**
- Java 25
- Spring Boot 3.5.5
- Spring Data JPA
- Spring Security (OAuth2 planned)
- H2 Database (dev)

**Frontend:**
- React 19
- CSS3

## API Endpoints

Base URL: `http://localhost:8080/api/v1`

- `GET /habits?userId={id}` - List user's habits
- `POST /habits?userId={id}` - Create habit
- `PUT /habits/{id}?userId={id}` - Update habit
- `DELETE /habits/{id}?userId={id}` - Delete habit

## Development

- Backend H2 console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (empty)

- Test data is auto-loaded on startup (see `backend/src/main/resources/data.sql`)
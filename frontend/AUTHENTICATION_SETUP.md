# Authentication Setup - Testing Guide

## What Was Implemented

### Backend (Spring Boot + JWT)

1. **JWT Token Generation and Validation**
   - `JwtUtil.java` - Generates and validates JWT tokens with 24-hour expiration
   - Tokens include userId and email claims

2. **Authentication Service**
   - `AuthService.java` - Handles login logic with password verification
   - Tracks failed login attempts
   - Validates user account status (active/inactive)

3. **Authentication Controller**
   - `AuthController.java` at `/api/v1/auth`
   - POST `/api/v1/auth/login` - Login endpoint
   - POST `/api/v1/auth/register` - Registration endpoint
   - POST `/api/v1/auth/logout` - Logout (TODO: token revocation)
   - POST `/api/v1/auth/refresh` - Refresh token (TODO)

4. **JWT Authentication Filter**
   - `JwtAuthenticationFilter.java` - Intercepts all requests
   - Validates JWT tokens from Authorization header
   - Sets Spring Security context with user authentication

5. **Security Configuration**
   - Stateless session management
   - Public endpoints: `/api/v1/auth/**`, `/h2-console/**`
   - All other endpoints require JWT authentication

### Frontend (React)

1. **Authentication Service**
   - `authService.js` - Handles login, register, logout
   - Stores token and user info in localStorage
   - Provides helper methods for auth headers

2. **Login Component**
   - `Login.js` - Login/Register form with toggle
   - Email and password validation
   - Error handling and loading states
   - Beautiful gradient styling

3. **Protected Routes**
   - App.js now checks authentication state
   - Shows login page if not authenticated
   - Shows main app with habits if authenticated
   - All API calls include Authorization header

4. **User Interface Updates**
   - Header shows logged-in user email
   - Logout button in header
   - Session expiration handling (401/403 responses)

## How to Test

### Prerequisites

1. Backend should be running on `http://localhost:8080`
2. Frontend should be running on `http://localhost:3000`

### Test Steps

#### 1. Register a New User

First, you need to register a user. You can do this via:

**Option A: Using HTTP Client (backend/src/main/resources/http/UserController.http)**
```http
POST http://localhost:8080/api/v1/auth/register
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "password123"
}
```

**Option B: Using curl**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

Expected response:
```json
{
  "id": 1,
  "message": "User registered successfully"
}
```

#### 2. Test Frontend Login

1. Open your browser to `http://localhost:3000`
2. You should see the Login page with a gradient background
3. Click "Register" link at the bottom
4. Enter an email and password (min 4 characters)
5. Click "Register" button
6. You should see an alert "Registration successful! Please login."
7. The form switches to Login mode
8. Enter the same email and password
9. Click "Login" button
10. You should be redirected to the main Habit Tracker app
11. The header should show your email and a Logout button

#### 3. Test Authenticated Features

Once logged in:

1. **Create a Habit** - Click "+ New Habit" and create a habit
2. **View Habits** - Your habits should load with the JWT token
3. **Calendar View** - Click "📅 View Calendar" on a habit
4. **Logout** - Click the "Logout" button in the header
5. You should be redirected back to the Login page

#### 4. Test Session Persistence

1. Login to the app
2. Refresh the page (F5)
3. You should remain logged in (token is stored in localStorage)
4. Open browser DevTools > Application > Local Storage
5. You should see: `token`, `userId`, `email`, `isAdmin`

#### 5. Test Session Expiration

The token expires after 24 hours. To test expiration:

1. Login to the app
2. Open browser DevTools > Console
3. Run: `localStorage.setItem('token', 'invalid-token')`
4. Try to create a habit or refresh the page
5. You should see "Session expired. Please login again."
6. You should be redirected to the Login page

#### 6. Test Invalid Credentials

1. On the Login page, enter wrong email or password
2. Click "Login"
3. You should see an error message: "Invalid email or password"

## API Endpoints

### Public Endpoints (No Authentication Required)

- `POST /api/v1/auth/login` - Login with email and password
- `POST /api/v1/auth/register` - Register new user

### Protected Endpoints (Requires JWT Token)

- `GET /api/v1/habits?userId={userId}` - Get all habits for user
- `POST /api/v1/habits?userId={userId}` - Create new habit
- `PUT /api/v1/habits/{id}?userId={userId}` - Update habit
- `DELETE /api/v1/habits/{id}?userId={userId}` - Delete habit
- `GET /api/v1/habit-completions/habit/{habitId}` - Get completions
- (All other habit completion endpoints)

## JWT Token Format

The Authorization header format:
```
Authorization: Bearer <jwt-token>
```

Example token payload:
```json
{
  "userId": 1,
  "sub": "test@example.com",
  "iat": 1696888888,
  "exp": 1696975288
}
```

## Troubleshooting

### Error: "Invalid email or password"
- Make sure the user is registered first
- Check that password matches what you registered with
- Check backend logs for more details

### Error: "Session expired. Please login again."
- Token has expired (24 hours)
- Token is invalid
- Login again to get a new token

### Frontend shows "Loading..." forever
- Check that backend is running on port 8080
- Check browser console for CORS errors
- Check Network tab for failed API calls

### CORS errors
- Backend has `@CrossOrigin` annotation on controllers
- Should work for localhost:3000 by default

### Can't see habits after login
- Make sure you're using the correct userId
- Check that the user owns the habits
- Check Network tab to see if token is being sent

## Security Notes

### Current Implementation
- JWT tokens stored in localStorage
- 24-hour token expiration
- BCrypt password hashing
- Failed login attempt tracking

### Future Improvements (TODOs)
- [ ] Implement token revocation on logout
- [ ] Add refresh token mechanism
- [ ] Move token to HttpOnly cookies (more secure than localStorage)
- [ ] Add rate limiting for login attempts
- [ ] Add email verification
- [ ] Add password reset functionality
- [ ] Add 2FA support

## Files Modified/Created

### Backend
- `backend/src/main/java/com/example/demo_habit/common/security/JwtUtil.java` - NEW
- `backend/src/main/java/com/example/demo_habit/common/security/JwtAuthenticationFilter.java` - NEW
- `backend/src/main/java/com/example/demo_habit/service/AuthService.java` - NEW
- `backend/src/main/java/com/example/demo_habit/controller/AuthController.java` - NEW
- `backend/src/main/java/com/example/demo_habit/dto/LoginRequest.java` - NEW
- `backend/src/main/java/com/example/demo_habit/dto/LoginResponse.java` - NEW
- `backend/src/main/java/com/example/demo_habit/common/config/SecurityConfig.java` - UPDATED
- `backend/build.gradle` - UPDATED (added JWT dependencies)

### Frontend
- `frontend/src/services/authService.js` - NEW
- `frontend/src/components/Login.js` - NEW
- `frontend/src/components/Login.css` - NEW
- `frontend/src/App.js` - UPDATED (authentication state management)
- `frontend/src/App.css` - UPDATED (user-info and logout styles)

## Next Steps

1. Test the complete login flow
2. If everything works, consider implementing:
   - Token refresh mechanism
   - Remember me functionality
   - Password strength validation
   - Email verification
3. Consider moving to HttpOnly cookies for better security
4. Add proper error handling and user feedback
5. Add loading spinners for better UX
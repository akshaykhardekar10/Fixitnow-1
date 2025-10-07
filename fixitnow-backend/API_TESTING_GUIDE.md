# FixItNow Backend API Testing Guide

## Database Schema Verification

The application uses Hibernate with `ddl-auto=update`, so the database tables will be created automatically when you start the application.

### Expected User Table Schema:
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    location VARCHAR(200),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

## API Endpoints

### 1. Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
    "name": "John Doe",
    "email": "john@example.com", 
    "password": "password123",
    "role": "PROVIDER",
    "location": "New York, NY"
}
```

#### Login User
```http
POST /api/auth/login
Content-Type: application/json

{
    "email": "john@example.com",
    "password": "password123"
}
```

### 2. Test Endpoints (No Authentication Required)

#### Get All Users with Details
```http
GET /api/test/users
```

#### Get Users by Role
```http
GET /api/test/users/by-role/PROVIDER
GET /api/test/users/by-role/CUSTOMER
GET /api/test/users/by-role/ADMIN
```

#### Get Users with Location Data
```http
GET /api/test/users/with-location
```

#### Get Database Schema Info
```http
GET /api/test/database-schema-info
```

### 3. User Management Endpoints

#### Get Current User Profile (Requires Authentication)
```http
GET /api/users/profile
Authorization: Bearer <your-jwt-token>
```

#### Update Current User Profile (Requires Authentication)
```http
PUT /api/users/profile
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
    "name": "Updated Name",
    "location": "Updated Location"
}
```

#### Get All Providers (Public)
```http
GET /api/users/providers
```

### 4. Admin Endpoints (Requires ADMIN Role)

#### Get All Users (Admin Only)
```http
GET /api/admin/users
Authorization: Bearer <admin-jwt-token>
```

#### Promote User to Admin
```http
POST /api/admin/users/{userId}/promote
Authorization: Bearer <admin-jwt-token>
```

#### Demote Admin to Customer
```http
POST /api/admin/users/{userId}/demote
Authorization: Bearer <admin-jwt-token>
```

#### Change User Role
```http
PUT /api/admin/users/{userId}/role
Authorization: Bearer <admin-jwt-token>
Content-Type: application/json

{
    "role": "ADMIN"
}
```

#### Delete User
```http
DELETE /api/admin/users/{userId}
Authorization: Bearer <admin-jwt-token>
```

#### Get User Statistics
```http
GET /api/admin/users/stats
Authorization: Bearer <admin-jwt-token>
```

## Testing Procedure

### Step 1: Test Registration with All Fields
1. Register a customer:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Customer",
    "email": "alice@example.com",
    "password": "password123",
    "role": "CUSTOMER",
    "location": "Los Angeles, CA"
  }'
```

2. Register a provider:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Bob Provider", 
    "email": "bob@example.com",
    "password": "password123",
    "role": "PROVIDER",
    "location": "Chicago, IL"
  }'
```

### Step 2: Verify Data Storage
```bash
# Check all users
curl http://localhost:8080/api/test/users

# Check users with location
curl http://localhost:8080/api/test/users/with-location

# Check providers specifically
curl http://localhost:8080/api/test/users/by-role/PROVIDER
```

### Step 3: Test Login and Role Verification
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "bob@example.com",
    "password": "password123"
  }'
```

### Step 4: Create First Admin User
Since you need an admin to promote others, you'll need to manually create one:

1. Register a regular user first
2. Manually update the database to set their role to ADMIN:
```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';
```

3. Or modify the registration endpoint temporarily to allow admin creation

### Step 5: Test Admin Functions
Once you have an admin user:
```bash
# Login as admin to get token
ADMIN_TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@example.com", "password": "password123"}' \
  | jq -r '.accessToken')

# Promote a user to admin
curl -X POST http://localhost:8080/api/admin/users/2/promote \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# Get user stats
curl http://localhost:8080/api/admin/users/stats \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

## Expected Results

1. **Location Field**: Should be saved and retrievable in user profiles
2. **Role Selection**: CUSTOMER/PROVIDER roles should be properly saved and returned in login response
3. **Admin Promotion**: Only admins should be able to promote users to admin role
4. **Database Schema**: All fields should be automatically created in the database

## Troubleshooting

1. **Database Connection**: Ensure MySQL is running and credentials in `application.properties` are correct
2. **JWT Tokens**: Make sure to include `Bearer ` prefix in Authorization headers
3. **Role Permissions**: Admin endpoints require ADMIN role, user endpoints require authentication
4. **CORS**: Frontend should be able to access all endpoints from `http://localhost:5173`

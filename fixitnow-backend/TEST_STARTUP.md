# FixItNow Backend Testing Startup Guide

## Quick Start Testing

### 1. Start the Application
```bash
cd fixitnow-backend
mvn spring-boot:run
```

The application will:
- ✅ Auto-create database tables
- ✅ Create default admin user (admin@fixitnow.com / admin123)
- ✅ Display user statistics in console

### 2. Test Field Mapping (No Auth Required)
```bash
# Check field mapping documentation
curl http://localhost:8080/api/validate/field-mapping

# Create test users with all fields
curl -X POST http://localhost:8080/api/validate/test-user-creation
```

### 3. Test Frontend Registration Flow
```bash
# Test customer registration with location
curl -X POST http://localhost:8080/api/validate/registration-flow \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Customer",
    "email": "john@example.com",
    "password": "password123",
    "role": "CUSTOMER", 
    "location": "New York, NY, USA"
  }'

# Test provider registration with location
curl -X POST http://localhost:8080/api/validate/registration-flow \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Provider",
    "email": "jane@example.com", 
    "password": "password123",
    "role": "PROVIDER",
    "location": "Los Angeles, CA, USA"
  }'
```

### 4. Verify Data Storage
```bash
# Check all users with details
curl http://localhost:8080/api/test/users

# Check users with location data
curl http://localhost:8080/api/test/users/with-location

# Check providers specifically
curl http://localhost:8080/api/test/users/by-role/PROVIDER

# Check customers specifically  
curl http://localhost:8080/api/test/users/by-role/CUSTOMER
```

### 5. Test Login and Role Verification
```bash
# Login as customer
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'

# Login as provider
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane@example.com", 
    "password": "password123"
  }'

# Login as admin
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@fixitnow.com",
    "password": "admin123"
  }'
```

### 6. Test Admin Functions
```bash
# Get admin token
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@fixitnow.com", "password": "admin123"}' \
  | jq -r '.accessToken')

echo "Admin Token: $ADMIN_TOKEN"

# Get all users (admin only)
curl http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# Get user statistics
curl http://localhost:8080/api/admin/users/stats \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# Promote user ID 2 to admin (replace 2 with actual user ID)
curl -X POST http://localhost:8080/api/admin/users/2/promote \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# Change user role
curl -X PUT http://localhost:8080/api/admin/users/3/role \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"role": "PROVIDER"}'
```

## Expected Results Checklist

### ✅ Database Schema Creation
- [ ] `users` table created with all columns
- [ ] Unique constraint on email
- [ ] Proper data types for all fields

### ✅ Registration Flow
- [ ] Name field saved correctly
- [ ] Email field saved and validated
- [ ] Password encrypted and saved
- [ ] Role (CUSTOMER/PROVIDER) saved correctly
- [ ] Location field saved when provided
- [ ] Timestamps (createdAt, updatedAt) auto-generated

### ✅ Login Flow  
- [ ] Login returns JWT token
- [ ] Login response includes user role
- [ ] Role-based authentication works

### ✅ Admin Functions
- [ ] Only admins can access admin endpoints
- [ ] User promotion to admin works
- [ ] Role changes work correctly
- [ ] User statistics display correctly

### ✅ Frontend Integration
- [ ] All frontend fields map to backend
- [ ] Location capture from frontend saves to DB
- [ ] Role selection from dropdown saves correctly
- [ ] Login redirects based on role

## Troubleshooting

### Database Issues
```bash
# Check if MySQL is running
mysql -u root -p

# Verify database exists
SHOW DATABASES;
USE fixitnow1;
SHOW TABLES;
DESCRIBE users;
```

### Application Logs
Check console output for:
- ✅ Default admin user creation message
- ✅ User statistics on startup
- ❌ Any SQL errors or connection issues

### Common Issues
1. **Port 8080 in use**: Change `server.port` in application.properties
2. **Database connection**: Verify MySQL credentials
3. **JWT errors**: Check token format in Authorization header
4. **CORS issues**: Verify frontend URL in CORS configuration

## Success Indicators

When everything is working correctly, you should see:

1. **Console Output**:
```
✅ Default admin user created:
   Email: admin@fixitnow.com
   Password: admin123
   Role: ADMIN
📊 Current User Statistics:
   Total Users: 1
   Admins: 1
   Providers: 0
   Customers: 0
```

2. **API Responses**: All endpoints return proper JSON with expected fields
3. **Database**: All user data properly stored with correct data types
4. **Authentication**: JWT tokens work for protected endpoints
5. **Authorization**: Role-based access control functions correctly

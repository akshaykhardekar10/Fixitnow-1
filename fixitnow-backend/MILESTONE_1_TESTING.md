# Milestone 1: Authentication & Basic Setup - API Testing

## Week 1 Requirements Testing

### ✅ **1. JWT Authentication APIs**

#### **Register API**
```http
POST /api/auth/register
Content-Type: application/json

{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "role": "CUSTOMER",
    "location": "New York, NY"
}
```

**Expected Output:**
```json
{
    "message": "User registered",
    "email": "john@example.com",
    "role": "CUSTOMER"
}
```

#### **Login API**
```http
POST /api/auth/login
Content-Type: application/json

{
    "email": "john@example.com",
    "password": "password123"
}
```

**Expected Output:**
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "role": "CUSTOMER"
}
```

### ✅ **2. User Model with Roles**

**Database Schema:**
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('CUSTOMER', 'PROVIDER', 'ADMIN') NOT NULL,
    location VARCHAR(200),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**Supported Roles:**
- `CUSTOMER` - End users who book services
- `PROVIDER` - Service providers who offer services  
- `ADMIN` - System administrators

### ⚠️ **3. Role-Based Routing Support**

**Current Status:** ✅ Backend supports role-based authentication
**Frontend Integration:** Login API returns role for frontend routing

**Test Role-Based Access:**
```bash
# Test customer access
curl -H "Authorization: Bearer <customer-token>" http://localhost:8080/api/users/profile

# Test provider access  
curl -H "Authorization: Bearer <provider-token>" http://localhost:8080/api/users/profile

# Test admin access
curl -H "Authorization: Bearer <admin-token>" http://localhost:8080/api/admin/users
```

### ✅ **4. Location Capture Support**

**Current API Support:**
- Registration accepts location field
- Location stored in database
- Location retrievable in user profile

**Test Location Storage:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Location Test User",
    "email": "location@test.com", 
    "password": "test123",
    "role": "PROVIDER",
    "location": "40.7128,-74.0060"
  }'
```

## Week 2 Requirements - IMPLEMENTATION COMPLETE

### ✅ **5. Service Provider Registration Form APIs**

#### **Get Service Categories**
```http
GET /api/categories
```
**Expected Output:**
```json
[
  {
    "id": 1,
    "name": "Home Cleaning",
    "description": "Professional home cleaning services",
    "iconUrl": "🏠",
    "active": true
  },
  {
    "id": 2,
    "name": "Plumbing", 
    "description": "Plumbing repair and installation services",
    "iconUrl": "🔧",
    "active": true
  }
]
```

#### **Create/Update Provider Profile**
```http
POST /api/providers/profile
Authorization: Bearer <provider-token>
Content-Type: application/json

{
    "serviceCategoryId": 1,
    "bio": "Experienced home cleaner with 5+ years",
    "skills": "Deep cleaning, Window cleaning, Carpet cleaning",
    "serviceArea": "New York, Brooklyn, Queens",
    "hourlyRate": 25.00,
    "available": true,
    "certifications": ["Certified Cleaner", "Insured"]
}
```

**Expected Output:**
```json
{
    "message": "Provider profile saved successfully",
    "profile": {
        "id": 1,
        "userId": 2,
        "userName": "John Provider",
        "serviceCategoryId": 1,
        "serviceCategoryName": "Home Cleaning",
        "bio": "Experienced home cleaner with 5+ years",
        "skills": "Deep cleaning, Window cleaning, Carpet cleaning",
        "serviceArea": "New York, Brooklyn, Queens",
        "hourlyRate": 25.00,
        "rating": 0.0,
        "totalReviews": 0,
        "available": true,
        "verified": false
    }
}
```

#### **Get Providers by Category**
```http
GET /api/providers/category/1
```

#### **Get Providers by Location**
```http
GET /api/providers/location?location=New York
```

#### **Update Provider Availability**
```http
PUT /api/providers/availability
Authorization: Bearer <provider-token>
Content-Type: application/json

{
    "available": false
}
```

## Testing Commands

### **Start Application & Test Basic Flow**
```bash
# 1. Start application
cd fixitnow-backend
mvn spring-boot:run

# 2. Health check
curl http://localhost:8080/api/health

# 3. Test registration
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Customer",
    "email": "customer@test.com",
    "password": "test123", 
    "role": "CUSTOMER",
    "location": "New York, NY"
  }'

# 4. Test login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "customer@test.com",
    "password": "test123"
  }'

# 5. Test provider registration
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Provider",
    "email": "provider@test.com",
    "password": "test123",
    "role": "PROVIDER", 
    "location": "Los Angeles, CA"
  }'
```

## Complete Testing Flow for Week 2 Provider Registration

### **Step 1: Test Service Categories**
```bash
# Get all service categories (should return 6 default categories)
curl http://localhost:8080/api/categories

# Get specific category
curl http://localhost:8080/api/categories/1
```

### **Step 2: Test Provider Registration Flow**
```bash
# 1. Register as provider
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Provider",
    "email": "jane@provider.com",
    "password": "provider123",
    "role": "PROVIDER",
    "location": "Los Angeles, CA"
  }'

# 2. Login as provider
PROVIDER_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "jane@provider.com", "password": "provider123"}' \
  | jq -r '.accessToken')

# 3. Create provider profile
curl -X POST http://localhost:8080/api/providers/profile \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "serviceCategoryId": 1,
    "bio": "Professional house cleaner with 5+ years experience",
    "skills": "Deep cleaning, Window cleaning, Carpet cleaning, Kitchen sanitization",
    "serviceArea": "Los Angeles, Beverly Hills, Santa Monica, West Hollywood",
    "hourlyRate": 30.00,
    "available": true,
    "certifications": ["Certified Professional Cleaner", "Insured & Bonded"]
  }'

# 4. Get provider profile
curl -H "Authorization: Bearer $PROVIDER_TOKEN" \
  http://localhost:8080/api/providers/profile

# 5. Update availability
curl -X PUT http://localhost:8080/api/providers/availability \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"available": false}'
```

### **Step 3: Test Provider Discovery**
```bash
# Get all available providers
curl http://localhost:8080/api/providers

# Get providers by category (Home Cleaning)
curl http://localhost:8080/api/providers/category/1

# Get providers by location
curl "http://localhost:8080/api/providers/location?location=Los Angeles"

# Get verified providers
curl http://localhost:8080/api/providers/verified
```

## Current Status Summary

### ✅ **COMPLETED (Week 1 & 2)**
- [x] JWT authentication (login/register)
- [x] User model with roles (customer, provider, admin)
- [x] Location capture support
- [x] Role-based authentication
- [x] Service provider registration form APIs
- [x] Category management APIs
- [x] Skills management (as part of provider profile)
- [x] Service area management (as part of provider profile)
- [x] Provider profile CRUD operations
- [x] Provider discovery by category/location

### ✅ **MILESTONE 1 COMPLETE**
- [x] **Week 1**: Authentication & Basic Setup
- [x] **Week 2**: Provider Registration & Service Categories

## API Endpoints Summary

### **Authentication APIs**
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/auth/me` - Current user info

### **Service Category APIs**
- `GET /api/categories` - Get active categories
- `GET /api/categories/{id}` - Get category by ID
- `POST /api/categories` - Create category (Admin only)
- `PUT /api/categories/{id}` - Update category (Admin only)

### **Provider APIs**
- `GET /api/providers` - Get available providers
- `GET /api/providers/category/{id}` - Get providers by category
- `GET /api/providers/location?location=X` - Get providers by location
- `POST /api/providers/profile` - Create/update provider profile
- `GET /api/providers/profile` - Get current provider profile
- `PUT /api/providers/availability` - Update availability

### **Admin APIs**
- `GET /api/admin/users` - Manage users
- `POST /api/admin/users/{id}/promote` - Promote to admin

## Expected Frontend Integration

The frontend should now be able to:

1. **Registration Flow**: 
   - Capture user details (name, email, password, role, location)
   - Send to `/api/auth/register`
   - Handle success/error responses

2. **Provider Registration Flow**:
   - Get service categories from `/api/categories`
   - Show category dropdown
   - Collect provider details (bio, skills, service area, hourly rate)
   - Send to `/api/providers/profile` after login

3. **Provider Discovery**:
   - List providers by category
   - Filter by location
   - Show provider details (bio, skills, rating, availability)

4. **Role-based Navigation**:
   - Customer → Services page
   - Provider → Provider dashboard
   - Admin → Admin dashboard

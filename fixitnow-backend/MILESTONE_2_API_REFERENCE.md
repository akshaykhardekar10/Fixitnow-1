# Milestone 2 - Complete API Reference Guide

## 🔗 Base URL
```
http://localhost:8080
```

## 📚 Table of Contents
1. [Service Subcategory APIs](#service-subcategory-apis)
2. [Service Listing APIs](#service-listing-apis)
3. [Review & Rating APIs](#review--rating-apis)
4. [Request/Response Examples](#requestresponse-examples)

---

## Service Subcategory APIs

### 1. Get All Active Subcategories
**GET** `/api/subcategories`

Get all active subcategories across all categories.

**Authentication:** Not required

**Response:**
```json
[
    {
        "id": 1,
        "name": "Deep Cleaning",
        "description": "Thorough cleaning of entire home",
        "categoryId": 1,
        "categoryName": "Home Cleaning",
        "active": true,
        "createdAt": "2024-10-13T10:00:00Z",
        "updatedAt": "2024-10-13T10:00:00Z"
    }
]
```

### 2. Get Subcategories by Category
**GET** `/api/subcategories/category/{categoryId}`

Get all active subcategories for a specific category.

**Authentication:** Not required

**Path Parameters:**
- `categoryId` (Long) - Category ID

**Example:**
```bash
curl http://localhost:8080/api/subcategories/category/1
```

### 3. Get Subcategory by ID
**GET** `/api/subcategories/{id}`

Get details of a specific subcategory.

**Authentication:** Not required

**Path Parameters:**
- `id` (Long) - Subcategory ID

### 4. Create Subcategory (Admin)
**POST** `/api/subcategories`

Create a new subcategory.

**Authentication:** Required (ADMIN role)

**Request Body:**
```json
{
    "name": "Pool Cleaning",
    "description": "Swimming pool cleaning and maintenance",
    "categoryId": 1
}
```

**Response:**
```json
{
    "message": "Subcategory created successfully",
    "subcategory": {
        "id": 25,
        "name": "Pool Cleaning",
        "description": "Swimming pool cleaning and maintenance",
        "categoryId": 1,
        "categoryName": "Home Cleaning",
        "active": true
    }
}
```

### 5. Update Subcategory (Admin)
**PUT** `/api/subcategories/{id}`

Update an existing subcategory.

**Authentication:** Required (ADMIN role)

### 6. Delete Subcategory (Admin)
**DELETE** `/api/subcategories/{id}`

Delete a subcategory.

**Authentication:** Required (ADMIN role)

---

## Service Listing APIs

### 1. Get All Services (Paginated)
**GET** `/api/services`

Browse all active service listings with pagination.

**Authentication:** Not required

**Query Parameters:**
- `page` (int, default: 0) - Page number
- `size` (int, default: 20) - Items per page

**Example:**
```bash
curl "http://localhost:8080/api/services?page=0&size=10"
```

**Response:**
```json
{
    "content": [
        {
            "id": 1,
            "providerProfileId": 3,
            "providerName": "Jane Doe",
            "providerRating": 4.8,
            "providerTotalReviews": 45,
            "providerImageUrl": "https://example.com/jane.jpg",
            "categoryId": 1,
            "categoryName": "Home Cleaning",
            "subcategoryId": 1,
            "subcategoryName": "Deep Cleaning",
            "title": "Professional Deep Cleaning Service",
            "description": "Complete deep cleaning of your home...",
            "price": 75.00,
            "pricingType": "HOURLY",
            "serviceLocation": "New York, Brooklyn, Queens",
            "imageUrls": ["https://example.com/img1.jpg"],
            "estimatedDuration": "3-4 hours",
            "availabilityDays": ["Monday", "Tuesday", "Wednesday"],
            "active": true,
            "viewCount": 120,
            "bookingCount": 15,
            "createdAt": "2024-10-10T10:00:00Z",
            "updatedAt": "2024-10-13T14:30:00Z"
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 10
    },
    "totalPages": 5,
    "totalElements": 50,
    "last": false,
    "first": true,
    "number": 0,
    "size": 10
}
```

### 2. Get Service by ID
**GET** `/api/services/{id}`

Get detailed information about a specific service. This endpoint automatically increments the view count.

**Authentication:** Not required

**Path Parameters:**
- `id` (Long) - Service listing ID

**Example:**
```bash
curl http://localhost:8080/api/services/5
```

### 3. Get Services by Category
**GET** `/api/services/category/{categoryId}`

Get all services under a specific category.

**Authentication:** Not required

**Query Parameters:**
- `page` (int, default: 0)
- `size` (int, default: 20)

**Example:**
```bash
curl "http://localhost:8080/api/services/category/1?page=0&size=20"
```

### 4. Get Services by Subcategory
**GET** `/api/services/subcategory/{subcategoryId}`

Get all services under a specific subcategory.

**Authentication:** Not required

**Query Parameters:**
- `page` (int, default: 0)
- `size` (int, default: 20)

### 5. Get Services by Provider
**GET** `/api/services/provider/{providerProfileId}`

Get all active services offered by a specific provider.

**Authentication:** Not required

**Path Parameters:**
- `providerProfileId` (Long) - Provider profile ID

**Example:**
```bash
curl http://localhost:8080/api/services/provider/3
```

### 6. Advanced Service Search
**POST** `/api/services/search`

Search services with multiple filters and sorting options.

**Authentication:** Not required

**Request Body:**
```json
{
    "keyword": "cleaning",
    "categoryId": 1,
    "subcategoryId": 1,
    "location": "New York",
    "minPrice": 50.00,
    "maxPrice": 150.00,
    "minRating": 4.0,
    "verifiedOnly": true,
    "sortBy": "rating",
    "sortOrder": "desc",
    "page": 0,
    "size": 20
}
```

**Field Descriptions:**
- `keyword` - Search in title and description
- `categoryId` - Filter by category
- `subcategoryId` - Filter by subcategory
- `location` - Filter by service location
- `minPrice` - Minimum price
- `maxPrice` - Maximum price
- `minRating` - Minimum provider rating
- `verifiedOnly` - Show only verified providers
- `sortBy` - Sort field: "price", "rating", "popular", "recent"
- `sortOrder` - "asc" or "desc"
- `page` - Page number
- `size` - Items per page

**Example:**
```bash
curl -X POST http://localhost:8080/api/services/search \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "cleaning",
    "categoryId": 1,
    "minPrice": 50,
    "maxPrice": 150,
    "sortBy": "price",
    "sortOrder": "asc",
    "page": 0,
    "size": 20
  }'
```

### 7. Create Service Listing (Provider)
**POST** `/api/services`

Create a new service listing. Provider must have a profile.

**Authentication:** Required (PROVIDER role)

**Request Body:**
```json
{
    "categoryId": 1,
    "subcategoryId": 1,
    "title": "Premium Deep Cleaning Service",
    "description": "Professional deep cleaning service with eco-friendly products. We clean every corner of your home including hard-to-reach areas.",
    "price": 85.00,
    "pricingType": "HOURLY",
    "serviceLocation": "Manhattan, Brooklyn, Queens",
    "imageUrls": [
        "https://example.com/cleaning1.jpg",
        "https://example.com/cleaning2.jpg"
    ],
    "estimatedDuration": "3-4 hours",
    "availabilityDays": ["Monday", "Tuesday", "Wednesday", "Friday"],
    "active": true
}
```

**Pricing Types:**
- `HOURLY` - Price per hour
- `FIXED` - One-time fixed price
- `PER_DAY` - Price per day

**Example:**
```bash
curl -X POST http://localhost:8080/api/services \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_PROVIDER_TOKEN" \
  -d '{
    "categoryId": 1,
    "subcategoryId": 1,
    "title": "Premium Deep Cleaning",
    "description": "Professional cleaning service",
    "price": 85.00,
    "pricingType": "HOURLY",
    "serviceLocation": "Manhattan, NY",
    "estimatedDuration": "3-4 hours",
    "availabilityDays": ["Monday", "Wednesday", "Friday"]
  }'
```

**Response:**
```json
{
    "message": "Service created successfully",
    "service": {
        "id": 25,
        "title": "Premium Deep Cleaning",
        "categoryId": 1,
        "subcategoryId": 1,
        "price": 85.00,
        "active": true
    }
}
```

### 8. Update Service Listing (Provider)
**PUT** `/api/services/{id}`

Update an existing service listing. Only the service owner can update.

**Authentication:** Required (PROVIDER role)

**Path Parameters:**
- `id` (Long) - Service listing ID

**Request Body:** Same as Create Service

### 9. Delete Service Listing (Provider)
**DELETE** `/api/services/{id}`

Delete a service listing. Only the service owner can delete.

**Authentication:** Required (PROVIDER role)

**Path Parameters:**
- `id` (Long) - Service listing ID

**Response:**
```json
{
    "message": "Service deleted successfully"
}
```

---

## Review & Rating APIs

### 1. Get Reviews for Service
**GET** `/api/reviews/service/{serviceListingId}`

Get all reviews for a specific service with pagination.

**Authentication:** Not required

**Path Parameters:**
- `serviceListingId` (Long) - Service listing ID

**Query Parameters:**
- `page` (int, default: 0)
- `size` (int, default: 10)

**Example:**
```bash
curl "http://localhost:8080/api/reviews/service/5?page=0&size=10"
```

**Response:**
```json
{
    "content": [
        {
            "id": 1,
            "serviceListingId": 5,
            "serviceTitle": "Professional Deep Cleaning",
            "providerProfileId": 3,
            "providerName": "Jane Doe",
            "customerId": 10,
            "customerName": "John Smith",
            "rating": 5,
            "comment": "Excellent service! Very professional and thorough.",
            "createdAt": "2024-10-13T14:30:00Z",
            "updatedAt": "2024-10-13T14:30:00Z"
        }
    ],
    "totalPages": 3,
    "totalElements": 25,
    "number": 0,
    "size": 10
}
```

### 2. Get Reviews for Provider
**GET** `/api/reviews/provider/{providerProfileId}`

Get all reviews for a specific provider across all their services.

**Authentication:** Not required

**Path Parameters:**
- `providerProfileId` (Long) - Provider profile ID

**Query Parameters:**
- `page` (int, default: 0)
- `size` (int, default: 10)

### 3. Get My Reviews (Customer)
**GET** `/api/reviews/my-reviews`

Get all reviews submitted by the authenticated customer.

**Authentication:** Required (CUSTOMER role)

**Example:**
```bash
curl http://localhost:8080/api/reviews/my-reviews \
  -H "Authorization: Bearer YOUR_CUSTOMER_TOKEN"
```

### 4. Check if Reviewed
**GET** `/api/reviews/check/{serviceListingId}`

Check if the authenticated customer has already reviewed a service.

**Authentication:** Required (CUSTOMER role)

**Path Parameters:**
- `serviceListingId` (Long) - Service listing ID

**Response:**
```json
{
    "hasReviewed": true
}
```

### 5. Submit Review (Customer)
**POST** `/api/reviews`

Submit a review for a service. Customers can only review each service once.

**Authentication:** Required (CUSTOMER role)

**Request Body:**
```json
{
    "serviceListingId": 5,
    "rating": 5,
    "comment": "Excellent service! Very professional and thorough. Would definitely recommend."
}
```

**Validation:**
- `rating`: Required, must be between 1-5
- `comment`: Optional, max 1000 characters
- Customer cannot review the same service twice

**Example:**
```bash
curl -X POST http://localhost:8080/api/reviews \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_CUSTOMER_TOKEN" \
  -d '{
    "serviceListingId": 5,
    "rating": 5,
    "comment": "Excellent service!"
  }'
```

**Response:**
```json
{
    "message": "Review submitted successfully",
    "review": {
        "id": 15,
        "serviceListingId": 5,
        "serviceTitle": "Professional Deep Cleaning",
        "providerName": "Jane Doe",
        "customerName": "John Smith",
        "rating": 5,
        "comment": "Excellent service!",
        "createdAt": "2024-10-13T14:30:00Z"
    }
}
```

**Note:** Provider's rating is automatically recalculated after review submission.

### 6. Update Review (Customer)
**PUT** `/api/reviews/{id}`

Update an existing review. Only the review author can update.

**Authentication:** Required (CUSTOMER role)

**Path Parameters:**
- `id` (Long) - Review ID

**Request Body:**
```json
{
    "rating": 4,
    "comment": "Updated comment after re-evaluation"
}
```

### 7. Delete Review (Customer)
**DELETE** `/api/reviews/{id}`

Delete a review. Only the review author can delete.

**Authentication:** Required (CUSTOMER role)

**Path Parameters:**
- `id` (Long) - Review ID

**Response:**
```json
{
    "message": "Review deleted successfully"
}
```

---

## Request/Response Examples

### Complete Provider Service Listing Flow

#### Step 1: Provider Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "provider@example.com",
    "password": "password123"
  }'
```

Response:
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "role": "PROVIDER"
}
```

#### Step 2: Get Available Categories
```bash
curl http://localhost:8080/api/categories
```

#### Step 3: Get Subcategories for Selected Category
```bash
curl http://localhost:8080/api/subcategories/category/1
```

#### Step 4: Create Service Listing
```bash
curl -X POST http://localhost:8080/api/services \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..." \
  -d '{
    "categoryId": 1,
    "subcategoryId": 1,
    "title": "Professional Deep Cleaning Service",
    "description": "Complete deep cleaning service for homes and offices",
    "price": 75.00,
    "pricingType": "HOURLY",
    "serviceLocation": "New York, NY",
    "estimatedDuration": "3-4 hours",
    "availabilityDays": ["Monday", "Tuesday", "Wednesday"]
  }'
```

### Complete Customer Service Booking Flow

#### Step 1: Browse Services by Category
```bash
curl "http://localhost:8080/api/services/category/1?page=0&size=10"
```

#### Step 2: Search with Filters
```bash
curl -X POST http://localhost:8080/api/services/search \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "cleaning",
    "categoryId": 1,
    "location": "New York",
    "minPrice": 50,
    "maxPrice": 100,
    "minRating": 4.0,
    "sortBy": "rating",
    "sortOrder": "desc"
  }'
```

#### Step 3: View Service Details
```bash
curl http://localhost:8080/api/services/5
```

#### Step 4: Check Provider Reviews
```bash
curl "http://localhost:8080/api/reviews/provider/3?page=0&size=10"
```

#### Step 5: Submit Review (After Service Completion)
```bash
curl -X POST http://localhost:8080/api/reviews \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer CUSTOMER_TOKEN" \
  -d '{
    "serviceListingId": 5,
    "rating": 5,
    "comment": "Great service!"
  }'
```

---

## Error Responses

### 400 Bad Request
```json
{
    "error": "Invalid request data or validation failed"
}
```

### 401 Unauthorized
```json
{
    "error": "Authentication required"
}
```

### 403 Forbidden
```json
{
    "error": "You don't have permission to access this resource"
}
```

### 404 Not Found
```json
{
    "error": "Resource not found"
}
```

### 409 Conflict
```json
{
    "error": "You have already reviewed this service"
}
```

---

## Important Notes

1. **Authentication**: Include JWT token in Authorization header: `Bearer YOUR_TOKEN`
2. **Pagination**: Default page size is 20, max recommended is 100
3. **Image URLs**: Store actual URLs from cloud storage (AWS S3, Cloudinary, etc.)
4. **Location Format**: Use consistent format like "City, State" or "City, State, Country"
5. **Price Format**: Decimal with 2 decimal places (e.g., 75.00)
6. **Rating Range**: 1-5 stars (integers only)
7. **View Tracking**: Automatic when GET /api/services/{id} is called
8. **Rating Updates**: Automatic when reviews are created/updated/deleted
9. **Duplicate Reviews**: One customer can only review each service once
10. **Service Ownership**: Providers can only modify their own services

---

## Postman Collection

Import this URL into Postman for easy testing:
```
Coming soon - Will be added with test collection
```

---

**For more details, see MILESTONE_2_COMPLETE.md**

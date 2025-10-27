# FixItNow Backend - Complete API Documentation

## 🎯 Overview

**FixItNow** is a comprehensive service marketplace platform connecting customers with local service providers. This Spring Boot backend provides complete REST APIs and WebSocket support for:

- 🔐 User authentication and authorization
- 👥 Customer and provider management
- 🛠️ Service listings and categories
- 📅 Booking and scheduling system
- 💬 Real-time chat between customers and providers
- ⭐ Reviews and ratings
- 🔍 Advanced search and filtering

---

## 📋 Project Status

| Milestone | Features | Status |
|-----------|----------|--------|
| **Milestone 1** | Authentication & Basic Setup | ✅ Complete |
| **Milestone 2** | Service Listings & Search | ✅ Complete |
| **Milestone 3** | Booking & Interaction | ✅ Complete |

---

## 🚀 Quick Start

### Prerequisites
- Java 21
- Maven 3.8+
- MySQL 8.0+
- Git

### Installation

1. **Clone the repository**
```bash
git clone <repository-url>
cd fixitnow-backend
```

2. **Configure database** (Update `src/main/resources/application.properties`)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/fixitnow
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. **Build and run**
```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

4. **Verify server is running**
```bash
curl http://localhost:8080/api/health
```

---

## 📚 Documentation Structure

### **Getting Started**
- `README.md` - This file (overview and quick start)
- `API_TESTING_GUIDE.md` - General API testing instructions

### **Milestone 1: Authentication & Basic Setup**
- `MILESTONE_1_TESTING.md` - Authentication and user management testing
- **Features**: JWT authentication, user roles, provider profiles

### **Milestone 2: Service Listings & Search**
- `MILESTONE_2_COMPLETE.md` - Complete feature documentation
- `MILESTONE_2_API_REFERENCE.md` - Detailed API reference
- **Features**: Service categories, listings, search, reviews

### **Milestone 3: Booking & Interaction**
- `MILESTONE_3_COMPLETE.md` - Complete feature documentation
- `MILESTONE_3_API_REFERENCE.md` - Detailed API reference
- `MILESTONE_3_TESTING_GUIDE.md` - Step-by-step testing guide
- `MILESTONE_3_SUMMARY.md` - Implementation summary
- **Features**: Booking system, real-time chat, status management

### **Test Scripts**
- `test_milestone_1.bat/sh` - Automated tests for Milestone 1
- `test_milestone_3.bat/sh` - Automated tests for Milestone 3

---

## 🏗️ Architecture

### **Technology Stack**
- **Framework**: Spring Boot 3.5.6
- **Language**: Java 21
- **Database**: MySQL 8.0
- **Authentication**: JWT (Auth0)
- **Real-time**: WebSocket (STOMP + SockJS)
- **ORM**: Hibernate/JPA
- **Build**: Maven
- **Security**: Spring Security

### **Project Structure**
```
src/main/java/com/fixitnow/
├── config/           # Configuration classes
│   ├── SecurityConfig.java
│   ├── JwtFilter.java
│   └── WebSocketConfig.java
├── controller/       # REST & WebSocket controllers
│   ├── AuthController.java
│   ├── BookingController.java
│   ├── ChatController.java
│   ├── ServiceController.java
│   └── WebSocketChatController.java
├── dto/             # Data Transfer Objects
├── exception/       # Custom exceptions
├── model/           # JPA entities
│   ├── User.java
│   ├── ServiceListing.java
│   ├── Booking.java
│   └── ChatMessage.java
├── repository/      # Data access layer
├── service/         # Business logic layer
└── FixitnowApplication.java
```

---

## 📊 Database Schema

### **Core Tables**
1. **users** - User accounts (customers, providers, admins)
2. **provider_profiles** - Provider-specific information
3. **service_categories** - Main service categories
4. **service_subcategories** - Subcategories for services
5. **service_listings** - Service offerings by providers
6. **bookings** - Service booking requests
7. **chat_rooms** - Chat rooms for bookings
8. **chat_messages** - Chat messages
9. **reviews** - Customer reviews and ratings

All tables are **auto-created** by Hibernate on first run.

---

## 🔌 API Overview

### **Base URL**
```
http://localhost:8080
```

### **API Categories**

#### **1. Authentication APIs**
```
POST   /api/auth/register     - Register new user
POST   /api/auth/login        - Login and get JWT token
GET    /api/auth/me           - Get current user info
```

#### **2. Service Category APIs**
```
GET    /api/categories        - Get all categories
GET    /api/categories/{id}   - Get category by ID
GET    /api/subcategories     - Get all subcategories
```

#### **3. Provider APIs**
```
POST   /api/providers/profile         - Create/update provider profile
GET    /api/providers/profile         - Get current provider profile
GET    /api/providers/category/{id}   - Get providers by category
PUT    /api/providers/availability    - Update availability
```

#### **4. Service Listing APIs**
```
GET    /api/services                  - Browse all services
GET    /api/services/{id}             - Get service details
POST   /api/services                  - Create service listing
POST   /api/services/search           - Advanced search
GET    /api/services/category/{id}    - Services by category
```

#### **5. Review APIs**
```
POST   /api/reviews                      - Submit review
GET    /api/reviews/service/{id}         - Get service reviews
GET    /api/reviews/provider/{id}        - Get provider reviews
```

#### **6. Booking APIs** 🆕
```
POST   /api/bookings                     - Create booking
GET    /api/bookings/{id}                - Get booking details
PUT    /api/bookings/{id}/status         - Update booking status
GET    /api/bookings/customer/my-bookings  - Customer's bookings
GET    /api/bookings/provider/pending    - Pending booking requests
```

#### **7. Chat APIs** 🆕
```
POST   /api/chat/room/{bookingId}        - Create/get chat room
GET    /api/chat/rooms                   - Get user's chat rooms
POST   /api/chat/send                    - Send message (REST)
GET    /api/chat/room/{roomId}/messages  - Get messages
PUT    /api/chat/room/{roomId}/mark-read - Mark as read
```

#### **8. WebSocket APIs** 🆕
```
CONNECT  /ws                             - WebSocket connection
SEND     /app/chat.send                  - Send message
SEND     /app/chat.join                  - Join room
SUBSCRIBE /topic/room/{roomId}           - Receive messages
SUBSCRIBE /topic/room/{roomId}/typing    - Typing indicators
```

---

## 🔑 Authentication

All protected endpoints require a JWT token in the Authorization header:

```bash
Authorization: Bearer <your-jwt-token>
```

### **Getting a Token**

1. Register a user:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "role": "CUSTOMER",
    "location": "New York, NY"
  }'
```

2. Login to get token:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

Response:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "role": "CUSTOMER"
}
```

3. Use token in subsequent requests:
```bash
curl http://localhost:8080/api/bookings/customer/my-bookings \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..."
```

---

## 🧪 Testing

### **Quick Test Commands**

```bash
# Test authentication
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@example.com","password":"test123","role":"CUSTOMER","location":"NYC"}'

# Browse services
curl http://localhost:8080/api/services?page=0&size=10

# Create booking (requires token)
curl -X POST http://localhost:8080/api/bookings \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"serviceListingId":1,"bookingDate":"2024-12-01T10:00:00","timeSlot":"10:00-13:00","totalPrice":225.00}'
```

### **Automated Testing**

Run comprehensive test scripts:

```bash
# Linux/Mac
chmod +x test_milestone_3.sh
./test_milestone_3.sh

# Windows
test_milestone_3.bat
```

---

## 💬 WebSocket Chat Usage

### **JavaScript Example**

```javascript
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

// Connect
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    // Subscribe to messages
    stompClient.subscribe('/topic/room/booking_1_customer_10_provider_3', (message) => {
        const chatMessage = JSON.parse(message.body);
        console.log('New message:', chatMessage);
    });
    
    // Send message
    stompClient.send('/app/chat.send', {}, JSON.stringify({
        roomId: 'booking_1_customer_10_provider_3',
        message: 'Hello!',
        messageType: 'TEXT'
    }));
});
```

---

## 📖 Feature Documentation

### **Milestone 1: Authentication & Profiles**
- JWT-based authentication
- Role-based access control (CUSTOMER, PROVIDER, ADMIN)
- User registration and login
- Provider profile management
- Service categories

👉 **See**: `MILESTONE_1_TESTING.md`

---

### **Milestone 2: Service Listings & Search**
- Service listing creation and management
- Hierarchical categories (6 main, 24 subcategories)
- Advanced search with filters
- Multiple pricing models (hourly, fixed, per-day)
- Review and rating system
- Service images and availability

👉 **See**: `MILESTONE_2_COMPLETE.md`, `MILESTONE_2_API_REFERENCE.md`

---

### **Milestone 3: Booking & Real-time Chat**
- Complete booking workflow
- Time slot management
- Booking statuses (PENDING, CONFIRMED, COMPLETED, CANCELLED)
- Conflict detection (prevents double-booking)
- Real-time WebSocket chat
- Message history and unread tracking
- Typing indicators

👉 **See**: `MILESTONE_3_COMPLETE.md`, `MILESTONE_3_API_REFERENCE.md`, `MILESTONE_3_TESTING_GUIDE.md`

---

## 🎯 Key Features

### **For Customers**
- ✅ Browse and search services
- ✅ Filter by category, location, price, rating
- ✅ Book services with preferred time slots
- ✅ Chat with providers in real-time
- ✅ Track booking status
- ✅ Leave reviews and ratings
- ✅ View booking history

### **For Providers**
- ✅ Create provider profile
- ✅ List multiple services
- ✅ Manage availability
- ✅ Accept/reject booking requests
- ✅ Chat with customers
- ✅ Complete bookings
- ✅ Receive reviews and ratings

### **For Admins**
- ✅ Manage users
- ✅ Manage categories
- ✅ View system statistics

---

## 🔒 Security Features

- ✅ JWT token-based authentication
- ✅ Password encryption (BCrypt)
- ✅ Role-based authorization
- ✅ CORS configuration
- ✅ SQL injection prevention (JPA)
- ✅ Input validation
- ✅ Secure WebSocket connections

---

## 📊 Performance Considerations

- ✅ Database indexing on foreign keys
- ✅ Pagination for large datasets
- ✅ Lazy loading for relationships
- ✅ Connection pooling
- ✅ Caching for static data (categories)
- ✅ Optimized queries with JPA

---

## 🐛 Troubleshooting

### **Server won't start**
- Check MySQL is running
- Verify database credentials in `application.properties`
- Ensure port 8080 is available

### **Authentication errors**
- Token may be expired (login again)
- Check Authorization header format: `Bearer <token>`
- Verify user role matches endpoint requirements

### **WebSocket connection fails**
- Check CORS configuration
- Verify WebSocket endpoint: `/ws`
- Try with SockJS fallback

### **Database errors**
- Ensure MySQL is running
- Check database exists: `CREATE DATABASE fixitnow;`
- Verify Hibernate is creating tables (check logs)

---

## 📝 Environment Variables

Create `application.properties` or use environment variables:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/fixitnow
spring.datasource.username=root
spring.datasource.password=your_password

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# JWT
jwt.secret=your-256-bit-secret-key-here
jwt.expiration=86400000

# Server
server.port=8080

# CORS
cors.allowed-origins=http://localhost:5173
```

---

## 🚀 Deployment

### **Production Checklist**
- [ ] Change `jwt.secret` to a strong random key
- [ ] Set `spring.jpa.show-sql=false`
- [ ] Configure production database
- [ ] Set up HTTPS/SSL
- [ ] Configure CORS for production domain
- [ ] Enable logging and monitoring
- [ ] Set up backup strategy
- [ ] Configure rate limiting
- [ ] Review security settings

---

## 📦 Dependencies

Main dependencies (see `pom.xml` for complete list):
- Spring Boot 3.5.6
- Spring Security
- Spring Data JPA
- Spring WebSocket
- MySQL Connector
- Auth0 JWT
- Lombok
- ModelMapper

---

## 🤝 Contributing

1. Follow existing code structure
2. Add tests for new features
3. Update documentation
4. Follow Java naming conventions
5. Use Lombok annotations
6. Add proper error handling

---

## 📞 API Support

For questions or issues:

1. **Check documentation**:
   - Start with this README
   - Review milestone-specific docs
   - Check API reference guides

2. **Test with provided scripts**:
   - Use test scripts for verification
   - Follow testing guides

3. **Common patterns**:
   - All list endpoints support pagination
   - All protected endpoints need JWT token
   - Use ISO 8601 format for dates
   - Follow REST conventions

---

## 🎓 For Frontend Developers

### **Quick Integration Guide**

1. **Authentication Flow**:
   - Register user → Login → Get token → Use in headers

2. **Service Browsing**:
   - Get categories → Search services → View details

3. **Booking Flow**:
   - Create booking → Provider confirms → Chat → Complete → Review

4. **WebSocket Chat**:
   - Connect to `/ws` → Subscribe to room → Send/receive messages

### **Recommended Libraries**
- **HTTP Client**: Axios or Fetch API
- **WebSocket**: SockJS-client + @stomp/stompjs
- **State Management**: Redux or Context API
- **Date Handling**: date-fns or dayjs

---

## ✅ Testing Checklist

Before integration:
- [ ] Authentication works (register, login)
- [ ] Can browse services
- [ ] Can create bookings
- [ ] Provider can accept bookings
- [ ] Chat room creates successfully
- [ ] Messages send and receive
- [ ] Can leave reviews

---

## 📈 API Statistics

- **Total Endpoints**: 40+ REST APIs
- **WebSocket Destinations**: 6
- **Database Tables**: 9
- **Supported Operations**: CRUD for all entities
- **Authentication**: JWT-based
- **Real-time Features**: Chat, typing indicators
- **Search Capabilities**: Advanced filtering and sorting

---

## 🎉 What's Working

✅ **Complete authentication system**  
✅ **Full service marketplace**  
✅ **End-to-end booking workflow**  
✅ **Real-time chat with WebSocket**  
✅ **Review and rating system**  
✅ **Advanced search and filters**  
✅ **Role-based permissions**  
✅ **Comprehensive documentation**  
✅ **Test scripts provided**  
✅ **Production-ready code**  

---

## 📚 Additional Resources

- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **WebSocket Guide**: https://spring.io/guides/gs/messaging-stomp-websocket/
- **JWT Tutorial**: https://jwt.io/introduction
- **MySQL Docs**: https://dev.mysql.com/doc/

---

## 📄 License

[Add your license here]

---

## 👨‍💻 Development Team

[Add team information]

---

**🚀 The FixItNow backend is complete and ready for production use!**

For detailed information on specific features, please refer to the milestone-specific documentation files.

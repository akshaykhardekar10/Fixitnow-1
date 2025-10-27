# 🎉 Milestone 3: Booking & Interaction - COMPLETE

## ✅ **All Requirements Implemented & Tested**

### **Week 5: Booking System**
- [x] **Booking Request System**: Complete end-to-end booking flow
- [x] **Time Slot Management**: Time-based booking with conflict detection
- [x] **Provider Accept/Reject**: Providers can accept or reject booking requests
- [x] **Booking Status Updates**: Full status management (Pending, Confirmed, Completed, Cancelled)
- [x] **Booking History**: View past and upcoming bookings
- [x] **Cancellation System**: Both parties can cancel with reasons

### **Week 6: Real-time Communication**
- [x] **Real-time Chat**: WebSocket-based live chat between customer and provider
- [x] **Chat Rooms**: Automatic chat room creation per booking
- [x] **Message History**: Persistent message storage and retrieval
- [x] **Unread Messages**: Track and display unread message counts
- [x] **Typing Indicators**: Real-time typing status
- [x] **Review System**: Integrated with existing review functionality after service completion

---

## 🗄️ **Database Schema**

### **Bookings Table**
```sql
CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_listing_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    provider_id BIGINT NOT NULL,
    booking_date DATETIME NOT NULL,
    time_slot VARCHAR(50) NOT NULL,
    duration_hours INT,
    status VARCHAR(20) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    service_location VARCHAR(500),
    customer_notes TEXT,
    provider_notes TEXT,
    cancellation_reason TEXT,
    cancelled_by BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (service_listing_id) REFERENCES service_listings(id),
    FOREIGN KEY (customer_id) REFERENCES users(id),
    FOREIGN KEY (provider_id) REFERENCES users(id)
);
```

**Booking Statuses:**
- `PENDING` - Booking request created, awaiting provider response
- `CONFIRMED` - Provider accepted the booking
- `COMPLETED` - Service has been completed
- `CANCELLED` - Booking was cancelled by either party

### **Chat Rooms Table**
```sql
CREATE TABLE chat_rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    provider_id BIGINT NOT NULL,
    room_id VARCHAR(100) UNIQUE NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_message_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id),
    FOREIGN KEY (customer_id) REFERENCES users(id),
    FOREIGN KEY (provider_id) REFERENCES users(id)
);
```

### **Chat Messages Table**
```sql
CREATE TABLE chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_room_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    message_type VARCHAR(20) DEFAULT 'TEXT',
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(id),
    FOREIGN KEY (sender_id) REFERENCES users(id)
);
```

---

## 📋 **API Endpoints - Milestone 3**

### **Booking APIs**

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/bookings` | Create a new booking request | Customer |
| GET | `/api/bookings/{id}` | Get booking details | Customer/Provider |
| PUT | `/api/bookings/{id}/status` | Update booking status (accept/reject/complete/cancel) | Customer/Provider |
| GET | `/api/bookings/customer/my-bookings` | Get customer's bookings (paginated) | Customer |
| GET | `/api/bookings/customer/upcoming` | Get customer's upcoming bookings | Customer |
| GET | `/api/bookings/provider/my-bookings` | Get provider's bookings (paginated) | Provider |
| GET | `/api/bookings/provider/upcoming` | Get provider's upcoming bookings | Provider |
| GET | `/api/bookings/provider/pending` | Get provider's pending booking requests | Provider |

### **Chat APIs (REST)**

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/chat/room/{bookingId}` | Create or get chat room for booking | Customer/Provider |
| GET | `/api/chat/rooms` | Get all user's chat rooms | Customer/Provider |
| GET | `/api/chat/room/{roomId}/messages` | Get chat messages (paginated) | Customer/Provider |
| POST | `/api/chat/send` | Send a chat message (REST fallback) | Customer/Provider |
| PUT | `/api/chat/room/{roomId}/mark-read` | Mark messages as read | Customer/Provider |
| GET | `/api/chat/room/{roomId}/unread-count` | Get unread message count | Customer/Provider |

### **WebSocket Endpoints**

| Endpoint | Type | Description |
|----------|------|-------------|
| `/ws` | Connect | WebSocket connection endpoint |
| `/app/chat.send` | Subscribe | Send message via WebSocket |
| `/app/chat.join` | Subscribe | Join a chat room |
| `/app/chat.typing` | Subscribe | Send typing indicator |
| `/topic/room/{roomId}` | Subscribe | Receive messages for a room |
| `/topic/room/{roomId}/typing` | Subscribe | Receive typing indicators |
| `/queue/errors` | Subscribe | Receive error messages |

---

## 🚀 **API Usage Examples**

### **1. Create a Booking (Customer)**

**Request:**
```http
POST /api/bookings
Authorization: Bearer {customer_token}
Content-Type: application/json

{
    "serviceListingId": 5,
    "bookingDate": "2024-11-15T10:00:00",
    "timeSlot": "10:00-13:00",
    "durationHours": 3,
    "totalPrice": 225.00,
    "serviceLocation": "123 Main St, New York, NY",
    "customerNotes": "Please bring eco-friendly cleaning products"
}
```

**Response:**
```json
{
    "message": "Booking request created successfully",
    "booking": {
        "id": 1,
        "serviceListingId": 5,
        "serviceTitle": "Professional Deep Cleaning",
        "serviceCategoryName": "Home Cleaning",
        "customerId": 10,
        "customerName": "John Smith",
        "customerEmail": "john@example.com",
        "providerId": 3,
        "providerName": "Jane Doe",
        "providerEmail": "jane@provider.com",
        "bookingDate": "2024-11-15T10:00:00",
        "timeSlot": "10:00-13:00",
        "durationHours": 3,
        "status": "PENDING",
        "totalPrice": 225.00,
        "serviceLocation": "123 Main St, New York, NY",
        "customerNotes": "Please bring eco-friendly cleaning products",
        "createdAt": "2024-10-26T14:30:00",
        "updatedAt": "2024-10-26T14:30:00"
    }
}
```

### **2. Provider Accepts Booking**

**Request:**
```http
PUT /api/bookings/1/status
Authorization: Bearer {provider_token}
Content-Type: application/json

{
    "status": "CONFIRMED",
    "providerNotes": "Confirmed! I'll arrive at 10:00 AM sharp with all necessary equipment."
}
```

**Response:**
```json
{
    "message": "Booking status updated successfully",
    "booking": {
        "id": 1,
        "status": "CONFIRMED",
        "providerNotes": "Confirmed! I'll arrive at 10:00 AM sharp with all necessary equipment.",
        "updatedAt": "2024-10-26T14:35:00"
    }
}
```

### **3. Provider Rejects Booking**

**Request:**
```http
PUT /api/bookings/1/status
Authorization: Bearer {provider_token}
Content-Type: application/json

{
    "status": "CANCELLED",
    "cancellationReason": "Not available on that date. Please book for another time."
}
```

### **4. Get My Bookings (Customer)**

**Request:**
```http
GET /api/bookings/customer/my-bookings?status=CONFIRMED&page=0&size=10
Authorization: Bearer {customer_token}
```

**Response:**
```json
{
    "content": [
        {
            "id": 1,
            "serviceTitle": "Professional Deep Cleaning",
            "providerName": "Jane Doe",
            "bookingDate": "2024-11-15T10:00:00",
            "timeSlot": "10:00-13:00",
            "status": "CONFIRMED",
            "totalPrice": 225.00
        }
    ],
    "totalPages": 1,
    "totalElements": 1,
    "number": 0,
    "size": 10
}
```

### **5. Get Pending Bookings (Provider)**

**Request:**
```http
GET /api/bookings/provider/pending
Authorization: Bearer {provider_token}
```

**Response:**
```json
[
    {
        "id": 2,
        "serviceTitle": "Plumbing Repair",
        "customerName": "Mike Johnson",
        "bookingDate": "2024-11-20T14:00:00",
        "timeSlot": "14:00-16:00",
        "status": "PENDING",
        "totalPrice": 150.00,
        "customerNotes": "Leaking kitchen sink"
    }
]
```

### **6. Complete a Booking (Provider)**

**Request:**
```http
PUT /api/bookings/1/status
Authorization: Bearer {provider_token}
Content-Type: application/json

{
    "status": "COMPLETED",
    "providerNotes": "Service completed successfully. Home is spotless!"
}
```

---

## 💬 **Real-time Chat Implementation**

### **REST API Approach (Simple)**

**Step 1: Create Chat Room**
```http
POST /api/chat/room/1
Authorization: Bearer {token}
```

**Response:**
```json
{
    "message": "Chat room ready",
    "chatRoom": {
        "id": 1,
        "roomId": "booking_1_customer_10_provider_3",
        "bookingId": 1,
        "customerId": 10,
        "customerName": "John Smith",
        "providerId": 3,
        "providerName": "Jane Doe",
        "isActive": true,
        "unreadCount": 0
    }
}
```

**Step 2: Send Message (REST)**
```http
POST /api/chat/send
Authorization: Bearer {token}
Content-Type: application/json

{
    "roomId": "booking_1_customer_10_provider_3",
    "message": "Hi! What time will you arrive?",
    "messageType": "TEXT"
}
```

**Step 3: Get Messages**
```http
GET /api/chat/room/booking_1_customer_10_provider_3/messages
Authorization: Bearer {token}
```

**Response:**
```json
[
    {
        "id": 1,
        "chatRoomId": 1,
        "roomId": "booking_1_customer_10_provider_3",
        "senderId": 10,
        "senderName": "John Smith",
        "message": "Hi! What time will you arrive?",
        "isRead": false,
        "messageType": "TEXT",
        "createdAt": "2024-10-26T15:00:00"
    }
]
```

### **WebSocket Approach (Real-time)**

**Frontend Implementation Example (JavaScript):**

```javascript
// 1. Connect to WebSocket
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);
    
    // 2. Subscribe to room messages
    const roomId = 'booking_1_customer_10_provider_3';
    
    stompClient.subscribe(`/topic/room/${roomId}`, function(message) {
        const chatMessage = JSON.parse(message.body);
        displayMessage(chatMessage);
    });
    
    // 3. Subscribe to typing indicators
    stompClient.subscribe(`/topic/room/${roomId}/typing`, function(message) {
        showTypingIndicator(message.body);
    });
});

// 4. Send message via WebSocket
function sendMessage(roomId, message) {
    stompClient.send('/app/chat.send', {}, JSON.stringify({
        roomId: roomId,
        message: message,
        messageType: 'TEXT'
    }));
}

// 5. Send typing indicator
function sendTyping(roomId) {
    stompClient.send('/app/chat.typing', {}, roomId);
}

// 6. Join room
function joinRoom(roomId) {
    stompClient.send('/app/chat.join', {}, roomId);
}
```

---

## 🔄 **Complete Booking Flow**

### **Customer Journey:**
1. **Browse Services** → Find a service they need
2. **Book Service** → Select date, time slot, provide details
3. **Wait for Confirmation** → Provider reviews the request
4. **Chat with Provider** → Discuss details if needed
5. **Service Delivery** → Provider completes the service
6. **Leave Review** → After completion, rate and review

### **Provider Journey:**
1. **Receive Booking Request** → Get notification of new booking
2. **Review Details** → Check date, time, customer notes
3. **Accept or Reject** → Confirm availability
4. **Chat with Customer** → Clarify any questions
5. **Complete Service** → Mark booking as completed
6. **Receive Reviews** → Get customer feedback

---

## 📱 **Status Flow Diagram**

```
PENDING
   ↓
   ├─→ CONFIRMED (Provider accepts)
   │      ↓
   │      ├─→ COMPLETED (Service done)
   │      │
   │      └─→ CANCELLED (Either party cancels)
   │
   └─→ CANCELLED (Provider rejects or Customer cancels)
```

### **Valid Status Transitions:**
- `PENDING` → `CONFIRMED` or `CANCELLED`
- `CONFIRMED` → `COMPLETED` or `CANCELLED`
- `COMPLETED` → No further changes allowed
- `CANCELLED` → No further changes allowed

---

## 🎯 **Key Features**

### **1. Booking Conflict Detection**
- Prevents double-booking for providers
- Checks for same date, time slot, and provider
- Only PENDING and CONFIRMED bookings block time slots

### **2. Role-Based Permissions**
- Only providers can confirm bookings
- Both parties can cancel bookings
- Only providers can mark as completed
- Users can only view their own bookings

### **3. Real-time Chat**
- WebSocket-based instant messaging
- Automatic chat room creation per booking
- Message persistence in database
- Unread message tracking
- Typing indicators
- System messages for events

### **4. Booking Analytics**
- Track booking counts per service
- View upcoming bookings
- Filter by status
- Sort and paginate results

---

## ✅ **Milestone 3 Checklist**

### **Week 5 - Booking System**
- [x] Booking request creation
- [x] Time slot validation
- [x] Provider accept/reject functionality
- [x] Booking status management (PENDING, CONFIRMED, COMPLETED, CANCELLED)
- [x] Cancellation with reasons
- [x] Booking history and filtering
- [x] Conflict detection for double-booking
- [x] Role-based booking operations
- [x] Automatic booking count tracking

### **Week 6 - Real-time Communication**
- [x] WebSocket configuration
- [x] Real-time chat between customer and provider
- [x] Automatic chat room creation
- [x] Message persistence
- [x] Message history retrieval
- [x] Unread message tracking
- [x] Mark messages as read
- [x] Typing indicators
- [x] REST API fallback for chat
- [x] Review system integration (from Milestone 2)

---

## 🚦 **Testing the APIs**

### **Complete Test Flow**

```bash
# 1. Customer registers and logs in
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Customer",
    "email": "john@customer.com",
    "password": "password123",
    "role": "CUSTOMER",
    "location": "New York, NY"
  }'

CUSTOMER_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "john@customer.com", "password": "password123"}' \
  | jq -r '.accessToken')

# 2. Customer creates a booking
curl -X POST http://localhost:8080/api/bookings \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "serviceListingId": 1,
    "bookingDate": "2024-11-20T10:00:00",
    "timeSlot": "10:00-13:00",
    "durationHours": 3,
    "totalPrice": 225.00,
    "serviceLocation": "New York, NY",
    "customerNotes": "Please use eco-friendly products"
  }'

# 3. Provider logs in
PROVIDER_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "provider@example.com", "password": "password123"}' \
  | jq -r '.accessToken')

# 4. Provider checks pending bookings
curl http://localhost:8080/api/bookings/provider/pending \
  -H "Authorization: Bearer $PROVIDER_TOKEN"

# 5. Provider confirms booking
curl -X PUT http://localhost:8080/api/bookings/1/status \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "CONFIRMED",
    "providerNotes": "Confirmed! Will arrive at 10:00 AM."
  }'

# 6. Create chat room for booking
curl -X POST http://localhost:8080/api/chat/room/1 \
  -H "Authorization: Bearer $CUSTOMER_TOKEN"

# 7. Send chat message
curl -X POST http://localhost:8080/api/chat/send \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": "booking_1_customer_10_provider_3",
    "message": "Looking forward to the service!",
    "messageType": "TEXT"
  }'

# 8. Provider marks booking as completed
curl -X PUT http://localhost:8080/api/bookings/1/status \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "COMPLETED",
    "providerNotes": "Service completed successfully!"
  }'

# 9. Customer leaves a review (from Milestone 2)
curl -X POST http://localhost:8080/api/reviews \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "serviceListingId": 1,
    "rating": 5,
    "comment": "Excellent service! Very professional."
  }'
```

---

## 🎓 **Frontend Integration Notes**

### **Important Points:**

1. **Authentication**: All endpoints require Bearer token in Authorization header
2. **WebSocket Connection**: Connect to `/ws` endpoint before subscribing
3. **Room ID Format**: `booking_{bookingId}_customer_{customerId}_provider_{providerId}`
4. **Status Transitions**: Follow valid status transition rules
5. **Conflict Detection**: Backend automatically prevents double-booking
6. **Real-time Updates**: Use WebSocket for instant chat updates
7. **REST Fallback**: REST API available if WebSocket is not suitable
8. **Unread Tracking**: Messages marked as read when viewed
9. **Pagination**: All list endpoints support page and size parameters
10. **Time Format**: Use ISO 8601 format for dates (e.g., "2024-11-20T10:00:00")

### **Recommended Frontend Libraries:**

- **WebSocket**: SockJS + STOMP.js for WebSocket communication
- **Chat UI**: react-chat-elements, stream-chat-react, or custom components
- **Date/Time**: date-fns or moment.js for date formatting
- **Real-time**: Consider polling as fallback if WebSocket fails

---

## 🎊 **Milestone 3 Complete!**

The backend now supports:
- ✅ **Complete booking workflow** from request to completion
- ✅ **Real-time chat** with WebSocket and REST APIs
- ✅ **Booking status management** with validation
- ✅ **Conflict detection** to prevent double-booking
- ✅ **Message persistence** and history
- ✅ **Unread tracking** and notifications
- ✅ **Review integration** after service completion

**The FixItNow platform is now ready for end-to-end service booking and customer-provider interaction! 🚀**

---

## 📈 **Next Steps (Milestone 4 - Future Enhancements)**

1. **Notifications** - Email/SMS notifications for booking updates
2. **Payment Integration** - Stripe/PayPal for online payments
3. **Provider Calendar** - Visual calendar for availability management
4. **Service Packages** - Bundle multiple services
5. **Admin Dashboard** - Analytics and system management
6. **Image Uploads** - Profile pictures and service images
7. **Search Optimization** - Advanced search with filters
8. **Mobile Push** - Push notifications for mobile apps

# Milestone 3 - API Reference Guide
## Booking & Real-time Chat APIs

## 🔗 Base URL
```
http://localhost:8080
```

## 📚 Table of Contents
1. [Booking APIs](#booking-apis)
2. [Chat REST APIs](#chat-rest-apis)
3. [WebSocket APIs](#websocket-apis)
4. [Request/Response Examples](#requestresponse-examples)
5. [Error Handling](#error-handling)

---

## Booking APIs

### 1. Create Booking (Customer)
**POST** `/api/bookings`

Create a new booking request for a service.

**Authentication:** Required (CUSTOMER role)

**Request Body:**
```json
{
    "serviceListingId": 5,
    "bookingDate": "2024-11-20T10:00:00",
    "timeSlot": "10:00-13:00",
    "durationHours": 3,
    "totalPrice": 225.00,
    "serviceLocation": "123 Main St, New York, NY",
    "customerNotes": "Please bring eco-friendly cleaning products"
}
```

**Field Descriptions:**
- `serviceListingId` (Long, required) - ID of the service to book
- `bookingDate` (DateTime, required) - Date and time for the service (ISO 8601 format)
- `timeSlot` (String, required) - Time slot in format "HH:MM-HH:MM"
- `durationHours` (Integer, optional) - Estimated duration in hours
- `totalPrice` (Decimal, required) - Total price for the service
- `serviceLocation` (String, optional) - Where the service will be performed
- `customerNotes` (String, optional) - Additional notes or requirements

**Response (201 Created):**
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
        "bookingDate": "2024-11-20T10:00:00",
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

**Example:**
```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Authorization: Bearer YOUR_CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "serviceListingId": 5,
    "bookingDate": "2024-11-20T10:00:00",
    "timeSlot": "10:00-13:00",
    "durationHours": 3,
    "totalPrice": 225.00,
    "serviceLocation": "New York, NY",
    "customerNotes": "Please use eco-friendly products"
  }'
```

---

### 2. Get Booking by ID
**GET** `/api/bookings/{id}`

Get detailed information about a specific booking.

**Authentication:** Required (Customer or Provider of the booking)

**Path Parameters:**
- `id` (Long) - Booking ID

**Response:**
```json
{
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
    "bookingDate": "2024-11-20T10:00:00",
    "timeSlot": "10:00-13:00",
    "durationHours": 3,
    "status": "CONFIRMED",
    "totalPrice": 225.00,
    "serviceLocation": "123 Main St, New York, NY",
    "customerNotes": "Please bring eco-friendly products",
    "providerNotes": "Confirmed! Will arrive at 10 AM.",
    "createdAt": "2024-10-26T14:30:00",
    "updatedAt": "2024-10-26T14:35:00"
}
```

**Example:**
```bash
curl http://localhost:8080/api/bookings/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 3. Update Booking Status
**PUT** `/api/bookings/{id}/status`

Update the status of a booking (accept, reject, complete, cancel).

**Authentication:** Required (Customer or Provider of the booking)

**Path Parameters:**
- `id` (Long) - Booking ID

**Request Body:**
```json
{
    "status": "CONFIRMED",
    "providerNotes": "Confirmed! I'll arrive at 10:00 AM sharp.",
    "cancellationReason": "Not applicable for confirmation"
}
```

**Status Values:**
- `PENDING` - Initial status (system-set)
- `CONFIRMED` - Provider accepts (provider only)
- `COMPLETED` - Service finished (provider only)
- `CANCELLED` - Cancelled by either party

**Valid Status Transitions:**
- PENDING → CONFIRMED or CANCELLED
- CONFIRMED → COMPLETED or CANCELLED
- COMPLETED → No changes allowed
- CANCELLED → No changes allowed

**Response:**
```json
{
    "message": "Booking status updated successfully",
    "booking": {
        "id": 1,
        "status": "CONFIRMED",
        "providerNotes": "Confirmed! I'll arrive at 10:00 AM sharp.",
        "updatedAt": "2024-10-26T14:35:00"
    }
}
```

**Example - Provider Confirms:**
```bash
curl -X PUT http://localhost:8080/api/bookings/1/status \
  -H "Authorization: Bearer PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "CONFIRMED",
    "providerNotes": "Confirmed! See you then."
  }'
```

**Example - Customer Cancels:**
```bash
curl -X PUT http://localhost:8080/api/bookings/1/status \
  -H "Authorization: Bearer CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "CANCELLED",
    "cancellationReason": "Need to reschedule"
  }'
```

---

### 4. Get Customer Bookings
**GET** `/api/bookings/customer/my-bookings`

Get all bookings for the authenticated customer with pagination and filtering.

**Authentication:** Required (CUSTOMER role)

**Query Parameters:**
- `status` (String, optional) - Filter by status: PENDING, CONFIRMED, COMPLETED, CANCELLED
- `page` (int, default: 0) - Page number
- `size` (int, default: 20) - Items per page
- `sortBy` (String, default: "createdAt") - Field to sort by
- `sortOrder` (String, default: "desc") - Sort order: asc or desc

**Example:**
```bash
curl "http://localhost:8080/api/bookings/customer/my-bookings?status=CONFIRMED&page=0&size=10" \
  -H "Authorization: Bearer CUSTOMER_TOKEN"
```

**Response:**
```json
{
    "content": [
        {
            "id": 1,
            "serviceTitle": "Professional Deep Cleaning",
            "providerName": "Jane Doe",
            "bookingDate": "2024-11-20T10:00:00",
            "timeSlot": "10:00-13:00",
            "status": "CONFIRMED",
            "totalPrice": 225.00
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 10
    },
    "totalPages": 1,
    "totalElements": 1,
    "number": 0,
    "size": 10
}
```

---

### 5. Get Customer Upcoming Bookings
**GET** `/api/bookings/customer/upcoming`

Get all upcoming bookings (PENDING or CONFIRMED) for the customer.

**Authentication:** Required (CUSTOMER role)

**Response:**
```json
[
    {
        "id": 1,
        "serviceTitle": "Professional Deep Cleaning",
        "providerName": "Jane Doe",
        "bookingDate": "2024-11-20T10:00:00",
        "timeSlot": "10:00-13:00",
        "status": "CONFIRMED",
        "totalPrice": 225.00
    },
    {
        "id": 3,
        "serviceTitle": "Plumbing Repair",
        "providerName": "Bob Smith",
        "bookingDate": "2024-11-25T14:00:00",
        "timeSlot": "14:00-16:00",
        "status": "PENDING",
        "totalPrice": 150.00
    }
]
```

**Example:**
```bash
curl http://localhost:8080/api/bookings/customer/upcoming \
  -H "Authorization: Bearer CUSTOMER_TOKEN"
```

---

### 6. Get Provider Bookings
**GET** `/api/bookings/provider/my-bookings`

Get all bookings for the authenticated provider with pagination and filtering.

**Authentication:** Required (PROVIDER role)

**Query Parameters:**
- `status` (String, optional) - Filter by status
- `page` (int, default: 0)
- `size` (int, default: 20)
- `sortBy` (String, default: "createdAt")
- `sortOrder` (String, default: "desc")

**Example:**
```bash
curl "http://localhost:8080/api/bookings/provider/my-bookings?status=PENDING&page=0&size=10" \
  -H "Authorization: Bearer PROVIDER_TOKEN"
```

---

### 7. Get Provider Upcoming Bookings
**GET** `/api/bookings/provider/upcoming`

Get all upcoming bookings for the provider.

**Authentication:** Required (PROVIDER role)

**Example:**
```bash
curl http://localhost:8080/api/bookings/provider/upcoming \
  -H "Authorization: Bearer PROVIDER_TOKEN"
```

---

### 8. Get Provider Pending Bookings
**GET** `/api/bookings/provider/pending`

Get all pending booking requests awaiting provider response.

**Authentication:** Required (PROVIDER role)

**Response:**
```json
[
    {
        "id": 2,
        "serviceTitle": "Home Cleaning",
        "customerName": "Mike Johnson",
        "customerEmail": "mike@example.com",
        "bookingDate": "2024-11-22T09:00:00",
        "timeSlot": "09:00-12:00",
        "status": "PENDING",
        "totalPrice": 180.00,
        "customerNotes": "First time booking",
        "createdAt": "2024-10-26T15:00:00"
    }
]
```

**Example:**
```bash
curl http://localhost:8080/api/bookings/provider/pending \
  -H "Authorization: Bearer PROVIDER_TOKEN"
```

---

## Chat REST APIs

### 1. Create or Get Chat Room
**POST** `/api/chat/room/{bookingId}`

Create a chat room for a booking or get existing one.

**Authentication:** Required (Customer or Provider of the booking)

**Path Parameters:**
- `bookingId` (Long) - Booking ID

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
        "unreadCount": 0,
        "lastMessage": null,
        "lastMessageAt": null,
        "createdAt": "2024-10-26T14:30:00"
    }
}
```

**Example:**
```bash
curl -X POST http://localhost:8080/api/chat/room/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 2. Get User Chat Rooms
**GET** `/api/chat/rooms`

Get all active chat rooms for the authenticated user.

**Authentication:** Required (CUSTOMER or PROVIDER role)

**Response:**
```json
[
    {
        "id": 1,
        "roomId": "booking_1_customer_10_provider_3",
        "bookingId": 1,
        "customerId": 10,
        "customerName": "John Smith",
        "providerId": 3,
        "providerName": "Jane Doe",
        "isActive": true,
        "unreadCount": 2,
        "lastMessage": "When will you arrive?",
        "lastMessageAt": "2024-10-26T15:30:00",
        "createdAt": "2024-10-26T14:30:00"
    }
]
```

**Example:**
```bash
curl http://localhost:8080/api/chat/rooms \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 3. Get Chat Messages
**GET** `/api/chat/room/{roomId}/messages`

Get messages for a chat room with optional pagination.

**Authentication:** Required (Member of the chat room)

**Path Parameters:**
- `roomId` (String) - Chat room ID

**Query Parameters:**
- `page` (int, default: 0) - Page number
- `size` (int, default: 50) - Messages per page

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
        "isRead": true,
        "messageType": "TEXT",
        "createdAt": "2024-10-26T15:00:00"
    },
    {
        "id": 2,
        "chatRoomId": 1,
        "roomId": "booking_1_customer_10_provider_3",
        "senderId": 3,
        "senderName": "Jane Doe",
        "message": "I'll arrive at 10:00 AM sharp!",
        "isRead": false,
        "messageType": "TEXT",
        "createdAt": "2024-10-26T15:02:00"
    }
]
```

**Example:**
```bash
curl "http://localhost:8080/api/chat/room/booking_1_customer_10_provider_3/messages?page=0&size=50" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 4. Send Message (REST)
**POST** `/api/chat/send`

Send a message via REST API (alternative to WebSocket).

**Authentication:** Required (Member of the chat room)

**Request Body:**
```json
{
    "roomId": "booking_1_customer_10_provider_3",
    "message": "Thanks! See you then.",
    "messageType": "TEXT"
}
```

**Message Types:**
- `TEXT` - Regular text message
- `IMAGE` - Image message (URL)
- `FILE` - File attachment (URL)
- `SYSTEM` - System-generated message

**Response:**
```json
{
    "message": "Message sent successfully",
    "chatMessage": {
        "id": 3,
        "chatRoomId": 1,
        "roomId": "booking_1_customer_10_provider_3",
        "senderId": 10,
        "senderName": "John Smith",
        "message": "Thanks! See you then.",
        "isRead": false,
        "messageType": "TEXT",
        "createdAt": "2024-10-26T15:05:00"
    }
}
```

**Example:**
```bash
curl -X POST http://localhost:8080/api/chat/send \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": "booking_1_customer_10_provider_3",
    "message": "Looking forward to the service!",
    "messageType": "TEXT"
  }'
```

---

### 5. Mark Messages as Read
**PUT** `/api/chat/room/{roomId}/mark-read`

Mark all unread messages in a room as read.

**Authentication:** Required (Member of the chat room)

**Path Parameters:**
- `roomId` (String) - Chat room ID

**Response:**
```json
{
    "message": "Messages marked as read"
}
```

**Example:**
```bash
curl -X PUT "http://localhost:8080/api/chat/room/booking_1_customer_10_provider_3/mark-read" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 6. Get Unread Count
**GET** `/api/chat/room/{roomId}/unread-count`

Get the count of unread messages in a room.

**Authentication:** Required (Member of the chat room)

**Path Parameters:**
- `roomId` (String) - Chat room ID

**Response:**
```json
{
    "unreadCount": 3
}
```

**Example:**
```bash
curl "http://localhost:8080/api/chat/room/booking_1_customer_10_provider_3/unread-count" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## WebSocket APIs

### WebSocket Connection

**Endpoint:** `/ws`

**Protocols:** Native WebSocket or SockJS

**Connection Example (JavaScript):**
```javascript
// Using SockJS
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);
    
    // Subscribe to topics here
});
```

---

### 1. Send Message via WebSocket
**Destination:** `/app/chat.send`

Send a message in real-time via WebSocket.

**Message Format:**
```json
{
    "roomId": "booking_1_customer_10_provider_3",
    "message": "Hello from WebSocket!",
    "messageType": "TEXT"
}
```

**Example:**
```javascript
stompClient.send('/app/chat.send', {}, JSON.stringify({
    roomId: 'booking_1_customer_10_provider_3',
    message: 'Hello from WebSocket!',
    messageType: 'TEXT'
}));
```

**Broadcast To:** `/topic/room/{roomId}`

All subscribers to the room will receive the message.

---

### 2. Subscribe to Room Messages
**Destination:** `/topic/room/{roomId}`

Subscribe to receive real-time messages for a specific room.

**Received Message Format:**
```json
{
    "id": 5,
    "chatRoomId": 1,
    "roomId": "booking_1_customer_10_provider_3",
    "senderId": 3,
    "senderName": "Jane Doe",
    "message": "On my way!",
    "isRead": false,
    "messageType": "TEXT",
    "createdAt": "2024-10-26T15:10:00"
}
```

**Example:**
```javascript
const roomId = 'booking_1_customer_10_provider_3';

stompClient.subscribe(`/topic/room/${roomId}`, function(message) {
    const chatMessage = JSON.parse(message.body);
    console.log('New message:', chatMessage);
    displayMessage(chatMessage);
});
```

---

### 3. Join Chat Room
**Destination:** `/app/chat.join`

Notify the server that a user has joined a chat room.

**Message Format:** Room ID as string

**Example:**
```javascript
stompClient.send('/app/chat.join', {}, 'booking_1_customer_10_provider_3');
```

---

### 4. Typing Indicator
**Destination:** `/app/chat.typing`

Send typing indicator to other users in the room.

**Message Format:** Room ID as string

**Example:**
```javascript
// Send typing indicator
function sendTyping(roomId) {
    stompClient.send('/app/chat.typing', {}, roomId);
}

// Throttle typing events
let typingTimeout;
inputField.addEventListener('input', () => {
    clearTimeout(typingTimeout);
    sendTyping(roomId);
    typingTimeout = setTimeout(() => {
        // Stop typing indicator after 3 seconds
    }, 3000);
});
```

---

### 5. Subscribe to Typing Indicators
**Destination:** `/topic/room/{roomId}/typing`

Receive typing indicators from other users.

**Received Message Format:** String with user name

**Example:**
```javascript
stompClient.subscribe(`/topic/room/${roomId}/typing`, function(message) {
    const typingText = message.body;
    console.log('Typing:', typingText);
    showTypingIndicator(typingText);
});
```

---

### 6. Error Queue
**Destination:** `/queue/errors`

Receive error messages from the server.

**Example:**
```javascript
stompClient.subscribe('/queue/errors', function(error) {
    console.error('WebSocket error:', error.body);
    showErrorNotification(error.body);
});
```

---

## Request/Response Examples

### Complete Booking + Chat Flow

```bash
# Step 1: Customer creates booking
curl -X POST http://localhost:8080/api/bookings \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "serviceListingId": 1,
    "bookingDate": "2024-11-20T10:00:00",
    "timeSlot": "10:00-13:00",
    "durationHours": 3,
    "totalPrice": 225.00,
    "serviceLocation": "New York, NY"
  }'

# Response: booking with id=1

# Step 2: Provider checks pending bookings
curl http://localhost:8080/api/bookings/provider/pending \
  -H "Authorization: Bearer $PROVIDER_TOKEN"

# Step 3: Provider confirms booking
curl -X PUT http://localhost:8080/api/bookings/1/status \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "CONFIRMED",
    "providerNotes": "Confirmed!"
  }'

# Step 4: Customer creates chat room
curl -X POST http://localhost:8080/api/chat/room/1 \
  -H "Authorization: Bearer $CUSTOMER_TOKEN"

# Response: roomId="booking_1_customer_10_provider_3"

# Step 5: Customer sends message
curl -X POST http://localhost:8080/api/chat/send \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": "booking_1_customer_10_provider_3",
    "message": "What should I prepare before you arrive?"
  }'

# Step 6: Provider responds
curl -X POST http://localhost:8080/api/chat/send \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": "booking_1_customer_10_provider_3",
    "message": "Just clear the areas to be cleaned. I bring all supplies!"
  }'

# Step 7: Get chat history
curl "http://localhost:8080/api/chat/room/booking_1_customer_10_provider_3/messages" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN"

# Step 8: Provider completes booking
curl -X PUT http://localhost:8080/api/bookings/1/status \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "COMPLETED",
    "providerNotes": "Service completed successfully!"
  }'

# Step 9: Customer leaves review
curl -X POST http://localhost:8080/api/reviews \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "serviceListingId": 1,
    "rating": 5,
    "comment": "Excellent service!"
  }'
```

---

## Error Handling

### Error Response Format
```json
{
    "error": "Error message description"
}
```

### Common Error Codes

**400 Bad Request**
- Invalid request data
- Validation errors
- Invalid status transition

**401 Unauthorized**
- Missing or invalid authentication token
- Token expired

**403 Forbidden**
- Insufficient permissions
- Not authorized to access this resource

**404 Not Found**
- Booking not found
- Chat room not found
- Service not found

**409 Conflict**
- Booking slot already taken
- Double-booking attempt
- Cannot modify completed/cancelled booking

### Example Error Responses

**Invalid Status Transition:**
```json
{
    "error": "Completed booking cannot be modified"
}
```

**Double Booking:**
```json
{
    "error": "Provider is not available for the selected time slot"
}
```

**Unauthorized Access:**
```json
{
    "error": "Unauthorized to update this booking"
}
```

---

## Important Notes

1. **Authentication**: All endpoints require valid JWT token except public ones
2. **Date Format**: Use ISO 8601 format: `2024-11-20T10:00:00`
3. **Time Slots**: Use format `HH:MM-HH:MM` (e.g., "09:00-12:00")
4. **Room ID Format**: `booking_{bookingId}_customer_{customerId}_provider_{providerId}`
5. **Status Transitions**: Follow valid transition rules (see documentation)
6. **WebSocket**: Requires connection setup before use
7. **Pagination**: Default page size is 20, max recommended is 100
8. **Real-time**: Use WebSocket for instant updates, REST for reliable delivery
9. **Unread Tracking**: Messages automatically tracked, mark as read when viewed
10. **Conflict Detection**: Backend prevents double-booking automatically

---

## Testing with Postman

Import these endpoints into Postman:
1. Create environment variables for `BASE_URL`, `CUSTOMER_TOKEN`, `PROVIDER_TOKEN`
2. Set Authorization header: `Bearer {{CUSTOMER_TOKEN}}`
3. Test the complete flow from booking creation to chat

---

**For complete examples and usage, see MILESTONE_3_COMPLETE.md**

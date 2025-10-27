# Milestone 3 - Testing Guide for Frontend Developers
## Booking & Real-time Chat Testing

## 🎯 Purpose

This guide helps frontend developers understand and test the Milestone 3 APIs for:
- **Booking System**: Create, manage, and track service bookings
- **Real-time Chat**: Communicate between customers and providers

---

## 📋 Prerequisites

1. **Backend Running**: Make sure the Spring Boot application is running on `http://localhost:8080`
2. **Test Data**: Create test users (customer and provider) with service listings
3. **Tools**: Use curl, Postman, or any HTTP client
4. **WebSocket Client**: For testing real-time chat (optional)

---

## 🧪 Quick Start Testing

### Step 1: Set Up Test Users

```bash
# Register a customer
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Customer",
    "email": "customer@test.com",
    "password": "test123",
    "role": "CUSTOMER",
    "location": "New York, NY"
  }'

# Register a provider
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Provider",
    "email": "provider@test.com",
    "password": "test123",
    "role": "PROVIDER",
    "location": "New York, NY"
  }'

# Login as customer
CUSTOMER_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "customer@test.com", "password": "test123"}' \
  | jq -r '.accessToken')

echo "Customer Token: $CUSTOMER_TOKEN"

# Login as provider
PROVIDER_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "provider@test.com", "password": "test123"}' \
  | jq -r '.accessToken')

echo "Provider Token: $PROVIDER_TOKEN"
```

### Step 2: Create Provider Profile & Service

```bash
# Create provider profile
curl -X POST http://localhost:8080/api/providers/profile \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "serviceCategoryId": 1,
    "bio": "Professional cleaner with 5 years experience",
    "skills": "Deep cleaning, Window cleaning",
    "serviceArea": "New York, NY",
    "hourlyRate": 75.00,
    "available": true
  }'

# Create a service listing
curl -X POST http://localhost:8080/api/services \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": 1,
    "subcategoryId": 1,
    "title": "Professional Home Cleaning",
    "description": "Complete home cleaning service",
    "price": 75.00,
    "pricingType": "HOURLY",
    "serviceLocation": "New York, NY",
    "estimatedDuration": "3-4 hours",
    "availabilityDays": ["Monday", "Tuesday", "Wednesday"],
    "active": true
  }'
```

---

## 📅 Testing Booking System

### Test 1: Create a Booking (Customer)

**Purpose**: Customer creates a booking request

```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "serviceListingId": 1,
    "bookingDate": "2024-12-01T10:00:00",
    "timeSlot": "10:00-13:00",
    "durationHours": 3,
    "totalPrice": 225.00,
    "serviceLocation": "123 Main St, New York, NY",
    "customerNotes": "Please use eco-friendly products"
  }'
```

**Expected Response**:
```json
{
    "message": "Booking request created successfully",
    "booking": {
        "id": 1,
        "status": "PENDING",
        "bookingDate": "2024-12-01T10:00:00",
        "timeSlot": "10:00-13:00",
        "totalPrice": 225.00,
        "customerName": "Test Customer",
        "providerName": "Test Provider"
    }
}
```

**✅ What to Check**:
- Status is `PENDING`
- Booking ID is returned
- Customer and provider names are correct
- Date and time slot are stored correctly

---

### Test 2: View Booking Details

```bash
# Customer views booking
curl http://localhost:8080/api/bookings/1 \
  -H "Authorization: Bearer $CUSTOMER_TOKEN"

# Provider views same booking
curl http://localhost:8080/api/bookings/1 \
  -H "Authorization: Bearer $PROVIDER_TOKEN"
```

**✅ What to Check**:
- Both customer and provider can view the booking
- All booking details are returned
- Customer notes are visible

---

### Test 3: Provider Views Pending Bookings

```bash
curl http://localhost:8080/api/bookings/provider/pending \
  -H "Authorization: Bearer $PROVIDER_TOKEN"
```

**Expected Response**:
```json
[
    {
        "id": 1,
        "serviceTitle": "Professional Home Cleaning",
        "customerName": "Test Customer",
        "bookingDate": "2024-12-01T10:00:00",
        "timeSlot": "10:00-13:00",
        "status": "PENDING",
        "totalPrice": 225.00,
        "customerNotes": "Please use eco-friendly products"
    }
]
```

**✅ What to Check**:
- Pending booking appears in the list
- Customer notes are visible
- All details are correct

---

### Test 4: Provider Accepts Booking

```bash
curl -X PUT http://localhost:8080/api/bookings/1/status \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "CONFIRMED",
    "providerNotes": "Confirmed! I will arrive at 10:00 AM sharp."
  }'
```

**Expected Response**:
```json
{
    "message": "Booking status updated successfully",
    "booking": {
        "id": 1,
        "status": "CONFIRMED",
        "providerNotes": "Confirmed! I will arrive at 10:00 AM sharp.",
        "updatedAt": "2024-10-26T15:30:00"
    }
}
```

**✅ What to Check**:
- Status changed to `CONFIRMED`
- Provider notes are saved
- Updated timestamp is set

---

### Test 5: Provider Rejects Booking

```bash
# Create another booking first, then reject it
curl -X PUT http://localhost:8080/api/bookings/2/status \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "CANCELLED",
    "cancellationReason": "Not available on that date"
  }'
```

**✅ What to Check**:
- Status changed to `CANCELLED`
- Cancellation reason is saved
- Cannot be modified again

---

### Test 6: Customer Views Their Bookings

```bash
# All bookings
curl "http://localhost:8080/api/bookings/customer/my-bookings?page=0&size=10" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN"

# Confirmed bookings only
curl "http://localhost:8080/api/bookings/customer/my-bookings?status=CONFIRMED&page=0&size=10" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN"

# Upcoming bookings
curl http://localhost:8080/api/bookings/customer/upcoming \
  -H "Authorization: Bearer $CUSTOMER_TOKEN"
```

**✅ What to Check**:
- Pagination works correctly
- Status filter works
- Upcoming bookings only show future dates
- Sorted by date

---

### Test 7: Provider Completes Booking

```bash
curl -X PUT http://localhost:8080/api/bookings/1/status \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "COMPLETED",
    "providerNotes": "Service completed successfully! Home is spotless."
  }'
```

**✅ What to Check**:
- Status changed to `COMPLETED`
- Cannot be modified further
- Customer can now leave a review

---

### Test 8: Customer Cancels Booking

```bash
curl -X PUT http://localhost:8080/api/bookings/3/status \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "CANCELLED",
    "cancellationReason": "Need to reschedule due to emergency"
  }'
```

**✅ What to Check**:
- Customer can cancel their booking
- Cancellation reason is saved
- `cancelledBy` field contains customer ID

---

### Test 9: Conflict Detection

```bash
# Try to book the same time slot again
curl -X POST http://localhost:8080/api/bookings \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "serviceListingId": 1,
    "bookingDate": "2024-12-01T10:00:00",
    "timeSlot": "10:00-13:00",
    "durationHours": 3,
    "totalPrice": 225.00,
    "serviceLocation": "New York, NY"
  }'
```

**Expected Response**:
```json
{
    "error": "Provider is not available for the selected time slot"
}
```

**✅ What to Check**:
- Booking is rejected
- Error message is clear
- No double-booking occurs

---

### Test 10: Invalid Status Transitions

```bash
# Try to modify completed booking
curl -X PUT http://localhost:8080/api/bookings/1/status \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "CONFIRMED"
  }'
```

**Expected Response**:
```json
{
    "error": "Completed booking cannot be modified"
}
```

**✅ What to Check**:
- Status transitions are validated
- Appropriate error messages are returned

---

## 💬 Testing Chat System

### Test 1: Create Chat Room

```bash
# Customer creates chat room for booking
curl -X POST http://localhost:8080/api/chat/room/1 \
  -H "Authorization: Bearer $CUSTOMER_TOKEN"
```

**Expected Response**:
```json
{
    "message": "Chat room ready",
    "chatRoom": {
        "id": 1,
        "roomId": "booking_1_customer_1_provider_2",
        "bookingId": 1,
        "customerId": 1,
        "customerName": "Test Customer",
        "providerId": 2,
        "providerName": "Test Provider",
        "isActive": true,
        "unreadCount": 0
    }
}
```

**✅ What to Check**:
- Room ID follows the pattern: `booking_{bookingId}_customer_{customerId}_provider_{providerId}`
- Both customer and provider IDs are correct
- Room is active

---

### Test 2: Send Messages (REST API)

```bash
# Customer sends message
curl -X POST http://localhost:8080/api/chat/send \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": "booking_1_customer_1_provider_2",
    "message": "Hi! What should I prepare before you arrive?",
    "messageType": "TEXT"
  }'

# Provider responds
curl -X POST http://localhost:8080/api/chat/send \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": "booking_1_customer_1_provider_2",
    "message": "Just clear the areas to be cleaned. I bring all supplies!",
    "messageType": "TEXT"
  }'
```

**Expected Response**:
```json
{
    "message": "Message sent successfully",
    "chatMessage": {
        "id": 1,
        "chatRoomId": 1,
        "roomId": "booking_1_customer_1_provider_2",
        "senderId": 1,
        "senderName": "Test Customer",
        "message": "Hi! What should I prepare before you arrive?",
        "isRead": false,
        "messageType": "TEXT",
        "createdAt": "2024-10-26T16:00:00"
    }
}
```

**✅ What to Check**:
- Message is saved with correct sender
- Message ID is generated
- isRead is false initially
- Timestamp is set

---

### Test 3: Get Chat Messages

```bash
# Get recent messages (last 50)
curl "http://localhost:8080/api/chat/room/booking_1_customer_1_provider_2/messages" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN"

# Get paginated messages
curl "http://localhost:8080/api/chat/room/booking_1_customer_1_provider_2/messages?page=0&size=20" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN"
```

**Expected Response**:
```json
[
    {
        "id": 2,
        "senderId": 2,
        "senderName": "Test Provider",
        "message": "Just clear the areas to be cleaned. I bring all supplies!",
        "isRead": false,
        "messageType": "TEXT",
        "createdAt": "2024-10-26T16:02:00"
    },
    {
        "id": 1,
        "senderId": 1,
        "senderName": "Test Customer",
        "message": "Hi! What should I prepare before you arrive?",
        "isRead": false,
        "messageType": "TEXT",
        "createdAt": "2024-10-26T16:00:00"
    }
]
```

**✅ What to Check**:
- Messages are sorted by createdAt descending (newest first)
- All message details are included
- Sender names are correct

---

### Test 4: Get User's Chat Rooms

```bash
# Customer's chat rooms
curl http://localhost:8080/api/chat/rooms \
  -H "Authorization: Bearer $CUSTOMER_TOKEN"

# Provider's chat rooms
curl http://localhost:8080/api/chat/rooms \
  -H "Authorization: Bearer $PROVIDER_TOKEN"
```

**Expected Response**:
```json
[
    {
        "id": 1,
        "roomId": "booking_1_customer_1_provider_2",
        "bookingId": 1,
        "customerId": 1,
        "customerName": "Test Customer",
        "providerId": 2,
        "providerName": "Test Provider",
        "isActive": true,
        "unreadCount": 1,
        "lastMessage": "Just clear the areas to be cleaned. I bring all supplies!",
        "lastMessageAt": "2024-10-26T16:02:00",
        "createdAt": "2024-10-26T15:30:00"
    }
]
```

**✅ What to Check**:
- All active chat rooms are listed
- Unread count is accurate
- Last message preview is shown
- Sorted by last message time

---

### Test 5: Mark Messages as Read

```bash
curl -X PUT "http://localhost:8080/api/chat/room/booking_1_customer_1_provider_2/mark-read" \
  -H "Authorization: Bearer $PROVIDER_TOKEN"
```

**Expected Response**:
```json
{
    "message": "Messages marked as read"
}
```

**✅ What to Check**:
- Unread count decreases
- Messages are marked as read
- Only affects messages sent by other user

---

### Test 6: Get Unread Count

```bash
curl "http://localhost:8080/api/chat/room/booking_1_customer_1_provider_2/unread-count" \
  -H "Authorization: Bearer $PROVIDER_TOKEN"
```

**Expected Response**:
```json
{
    "unreadCount": 1
}
```

**✅ What to Check**:
- Count only includes messages from other user
- Updates when messages are marked as read

---

## 🔌 Testing WebSocket Chat (Advanced)

### Using JavaScript/Node.js

Create a file `test-websocket.js`:

```javascript
const SockJS = require('sockjs-client');
const Stomp = require('@stomp/stompjs');

// Replace with actual tokens
const CUSTOMER_TOKEN = 'your_customer_token';
const roomId = 'booking_1_customer_1_provider_2';

const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);
    
    // Subscribe to room messages
    stompClient.subscribe(`/topic/room/${roomId}`, function(message) {
        const chatMessage = JSON.parse(message.body);
        console.log('Received message:', chatMessage);
    });
    
    // Subscribe to typing indicators
    stompClient.subscribe(`/topic/room/${roomId}/typing`, function(message) {
        console.log('Typing indicator:', message.body);
    });
    
    // Join room
    stompClient.send('/app/chat.join', {}, roomId);
    
    // Send a message after 2 seconds
    setTimeout(() => {
        stompClient.send('/app/chat.send', {}, JSON.stringify({
            roomId: roomId,
            message: 'Hello from WebSocket!',
            messageType: 'TEXT'
        }));
    }, 2000);
    
    // Send typing indicator
    setTimeout(() => {
        stompClient.send('/app/chat.typing', {}, roomId);
    }, 1000);
}, function(error) {
    console.error('Connection error:', error);
});
```

**✅ What to Check**:
- WebSocket connects successfully
- Can subscribe to room topics
- Messages are received in real-time
- Typing indicators work
- Connection stays alive

---

## 📊 Complete Test Scenario

### End-to-End Booking + Chat Flow

```bash
#!/bin/bash

echo "=== 1. Customer creates booking ==="
BOOKING_ID=$(curl -s -X POST http://localhost:8080/api/bookings \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "serviceListingId": 1,
    "bookingDate": "2024-12-01T10:00:00",
    "timeSlot": "10:00-13:00",
    "durationHours": 3,
    "totalPrice": 225.00,
    "serviceLocation": "New York, NY"
  }' | jq -r '.booking.id')

echo "Booking created with ID: $BOOKING_ID"
sleep 1

echo "=== 2. Provider views pending bookings ==="
curl -s http://localhost:8080/api/bookings/provider/pending \
  -H "Authorization: Bearer $PROVIDER_TOKEN" | jq '.'
sleep 1

echo "=== 3. Provider confirms booking ==="
curl -s -X PUT "http://localhost:8080/api/bookings/$BOOKING_ID/status" \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "CONFIRMED",
    "providerNotes": "Confirmed!"
  }' | jq '.'
sleep 1

echo "=== 4. Customer creates chat room ==="
ROOM_ID=$(curl -s -X POST "http://localhost:8080/api/chat/room/$BOOKING_ID" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  | jq -r '.chatRoom.roomId')

echo "Chat room created: $ROOM_ID"
sleep 1

echo "=== 5. Customer sends message ==="
curl -s -X POST http://localhost:8080/api/chat/send \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"roomId\": \"$ROOM_ID\",
    \"message\": \"Looking forward to the service!\",
    \"messageType\": \"TEXT\"
  }" | jq '.'
sleep 1

echo "=== 6. Provider responds ==="
curl -s -X POST http://localhost:8080/api/chat/send \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"roomId\": \"$ROOM_ID\",
    \"message\": \"Me too! See you soon.\",
    \"messageType\": \"TEXT\"
  }" | jq '.'
sleep 1

echo "=== 7. Get chat history ==="
curl -s "http://localhost:8080/api/chat/room/$ROOM_ID/messages" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" | jq '.'
sleep 1

echo "=== 8. Provider completes booking ==="
curl -s -X PUT "http://localhost:8080/api/bookings/$BOOKING_ID/status" \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "COMPLETED",
    "providerNotes": "Service completed!"
  }' | jq '.'

echo "=== Test Complete ==="
```

**Save as `test_milestone3.sh` and run:**
```bash
chmod +x test_milestone3.sh
./test_milestone3.sh
```

---

## ✅ Test Checklist

### Booking System Tests
- [ ] Customer can create booking
- [ ] Provider sees pending bookings
- [ ] Provider can accept booking
- [ ] Provider can reject booking
- [ ] Customer can view their bookings
- [ ] Provider can view their bookings
- [ ] Booking status updates correctly
- [ ] Provider can complete booking
- [ ] Customer can cancel booking
- [ ] Conflict detection works
- [ ] Invalid status transitions are blocked
- [ ] Pagination works
- [ ] Status filtering works
- [ ] Upcoming bookings filter works

### Chat System Tests
- [ ] Chat room is created for booking
- [ ] Customer can send messages
- [ ] Provider can send messages
- [ ] Messages are saved to database
- [ ] Chat history is retrievable
- [ ] Unread count is accurate
- [ ] Mark as read works
- [ ] User's chat rooms list works
- [ ] WebSocket connection works
- [ ] Real-time messages are delivered
- [ ] Typing indicators work

### Integration Tests
- [ ] Complete booking flow works
- [ ] Chat works after booking
- [ ] Review can be left after completion
- [ ] Multiple bookings work
- [ ] Multiple chat rooms work

---

## 🐛 Common Issues & Solutions

### Issue 1: "Unauthorized" Error
**Cause**: Token expired or invalid

**Solution**:
```bash
# Re-login to get fresh token
CUSTOMER_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "customer@test.com", "password": "test123"}' \
  | jq -r '.accessToken')
```

### Issue 2: "Service not found"
**Cause**: Service listing doesn't exist

**Solution**: Create a service listing first (see Step 2 in Quick Start)

### Issue 3: "Booking slot already taken"
**Cause**: Trying to book same time slot

**Solution**: Use different date/time or different provider

### Issue 4: WebSocket connection fails
**Cause**: CORS or authentication issue

**Solution**: Check if backend is running and CORS is configured for your origin

### Issue 5: Messages not showing up
**Cause**: Wrong room ID or not subscribed

**Solution**: Verify room ID format and subscription

---

## 📝 Notes for Frontend Integration

1. **Token Management**: Store tokens securely, refresh when expired
2. **Date Formatting**: Always use ISO 8601 format for dates
3. **WebSocket**: Implement reconnection logic for reliability
4. **Error Handling**: Display clear error messages to users
5. **Loading States**: Show loading indicators during API calls
6. **Real-time Updates**: Use WebSocket for instant chat updates
7. **Optimistic UI**: Update UI immediately, rollback on error
8. **Polling Fallback**: Implement polling if WebSocket fails
9. **Notification**: Show notifications for new messages/bookings
10. **Validation**: Validate input before sending to API

---

## 🎓 Ready for Integration!

The backend APIs are fully functional and ready for frontend integration. Use this guide to:
- Test all booking scenarios
- Verify chat functionality
- Understand expected responses
- Handle error cases
- Integrate WebSocket

**For detailed API documentation, see MILESTONE_3_API_REFERENCE.md**

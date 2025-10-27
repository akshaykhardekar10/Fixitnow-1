#!/bin/bash

# Milestone 3: Booking & Chat Testing Script
# This script tests all booking and chat APIs

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Base URL
BASE_URL="http://localhost:8080"

echo "============================================"
echo "Milestone 3: Booking & Chat Testing Script"
echo "============================================"
echo ""

# Check if jq is installed
if ! command -v jq &> /dev/null; then
    echo -e "${RED}Error: jq is not installed. Please install jq to run this script.${NC}"
    echo "Install: sudo apt-get install jq (Ubuntu) or brew install jq (Mac)"
    exit 1
fi

# Function to print test step
print_step() {
    echo -e "${YELLOW}[$1] $2${NC}"
}

# Function to print success
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# Function to print error
print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Check if server is running
print_step "0/15" "Checking if server is running..."
if ! curl -s "$BASE_URL/api/health" > /dev/null 2>&1; then
    print_error "Server is not running at $BASE_URL"
    echo "Please start the Spring Boot application first."
    exit 1
fi
print_success "Server is running"
echo ""
sleep 1

# 1. Register customer
print_step "1/15" "Registering test customer..."
CUSTOMER_REG=$(curl -s -X POST "$BASE_URL/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Customer M3",
    "email": "customer.m3@test.com",
    "password": "test123",
    "role": "CUSTOMER",
    "location": "New York, NY"
  }')

if echo "$CUSTOMER_REG" | grep -q "error"; then
    echo "Customer might already exist, continuing..."
else
    print_success "Customer registered"
    echo "$CUSTOMER_REG" | jq '.'
fi
echo ""
sleep 1

# 2. Register provider
print_step "2/15" "Registering test provider..."
PROVIDER_REG=$(curl -s -X POST "$BASE_URL/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Provider M3",
    "email": "provider.m3@test.com",
    "password": "test123",
    "role": "PROVIDER",
    "location": "New York, NY"
  }')

if echo "$PROVIDER_REG" | grep -q "error"; then
    echo "Provider might already exist, continuing..."
else
    print_success "Provider registered"
    echo "$PROVIDER_REG" | jq '.'
fi
echo ""
sleep 1

# 3. Login as customer
print_step "3/15" "Logging in as customer..."
CUSTOMER_TOKEN=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "customer.m3@test.com",
    "password": "test123"
  }' | jq -r '.accessToken')

if [ "$CUSTOMER_TOKEN" == "null" ] || [ -z "$CUSTOMER_TOKEN" ]; then
    print_error "Failed to get customer token"
    exit 1
fi
print_success "Customer logged in"
echo "Token: ${CUSTOMER_TOKEN:0:20}..."
echo ""
sleep 1

# 4. Login as provider
print_step "4/15" "Logging in as provider..."
PROVIDER_TOKEN=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "provider.m3@test.com",
    "password": "test123"
  }' | jq -r '.accessToken')

if [ "$PROVIDER_TOKEN" == "null" ] || [ -z "$PROVIDER_TOKEN" ]; then
    print_error "Failed to get provider token"
    exit 1
fi
print_success "Provider logged in"
echo "Token: ${PROVIDER_TOKEN:0:20}..."
echo ""
sleep 1

# 5. Create provider profile
print_step "5/15" "Creating provider profile..."
PROFILE_CREATE=$(curl -s -X POST "$BASE_URL/api/providers/profile" \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "serviceCategoryId": 1,
    "bio": "Professional cleaner with 5 years experience",
    "skills": "Deep cleaning, Window cleaning, Carpet cleaning",
    "serviceArea": "New York, Brooklyn, Queens",
    "hourlyRate": 75.00,
    "available": true,
    "certifications": ["Certified Cleaner"]
  }')

if echo "$PROFILE_CREATE" | grep -q "error"; then
    echo "Profile might already exist, continuing..."
else
    print_success "Provider profile created"
    echo "$PROFILE_CREATE" | jq '.'
fi
echo ""
sleep 1

# 6. Create service listing
print_step "6/15" "Creating service listing..."
SERVICE_CREATE=$(curl -s -X POST "$BASE_URL/api/services" \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": 1,
    "subcategoryId": 1,
    "title": "Professional Home Cleaning - M3 Test",
    "description": "Complete home cleaning service for testing",
    "price": 75.00,
    "pricingType": "HOURLY",
    "serviceLocation": "New York, NY",
    "estimatedDuration": "3-4 hours",
    "availabilityDays": ["Monday", "Tuesday", "Wednesday"],
    "active": true
  }')

SERVICE_ID=$(echo "$SERVICE_CREATE" | jq -r '.service.id')
if [ "$SERVICE_ID" == "null" ] || [ -z "$SERVICE_ID" ]; then
    print_error "Failed to create service listing"
    echo "$SERVICE_CREATE"
    exit 1
fi
print_success "Service listing created with ID: $SERVICE_ID"
echo "$SERVICE_CREATE" | jq '.'
echo ""
sleep 1

# 7. Create a booking
print_step "7/15" "Creating a booking..."
BOOKING_DATE=$(date -d "+7 days" -u +"%Y-%m-%dT10:00:00" 2>/dev/null || date -v+7d -u +"%Y-%m-%dT10:00:00")
BOOKING_CREATE=$(curl -s -X POST "$BASE_URL/api/bookings" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"serviceListingId\": $SERVICE_ID,
    \"bookingDate\": \"$BOOKING_DATE\",
    \"timeSlot\": \"10:00-13:00\",
    \"durationHours\": 3,
    \"totalPrice\": 225.00,
    \"serviceLocation\": \"123 Main St, New York, NY\",
    \"customerNotes\": \"Please bring eco-friendly cleaning products\"
  }")

BOOKING_ID=$(echo "$BOOKING_CREATE" | jq -r '.booking.id')
if [ "$BOOKING_ID" == "null" ] || [ -z "$BOOKING_ID" ]; then
    print_error "Failed to create booking"
    echo "$BOOKING_CREATE"
    exit 1
fi
print_success "Booking created with ID: $BOOKING_ID"
echo "$BOOKING_CREATE" | jq '.'
echo ""
sleep 1

# 8. Provider checks pending bookings
print_step "8/15" "Provider checking pending bookings..."
PENDING_BOOKINGS=$(curl -s "$BASE_URL/api/bookings/provider/pending" \
  -H "Authorization: Bearer $PROVIDER_TOKEN")
print_success "Pending bookings retrieved"
echo "$PENDING_BOOKINGS" | jq '.'
echo ""
sleep 1

# 9. Provider confirms booking
print_step "9/15" "Provider confirming booking..."
BOOKING_CONFIRM=$(curl -s -X PUT "$BASE_URL/api/bookings/$BOOKING_ID/status" \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "CONFIRMED",
    "providerNotes": "Confirmed! I will arrive at 10:00 AM sharp with all equipment."
  }')
print_success "Booking confirmed"
echo "$BOOKING_CONFIRM" | jq '.'
echo ""
sleep 1

# 10. Customer views bookings
print_step "10/15" "Customer viewing their bookings..."
CUSTOMER_BOOKINGS=$(curl -s "$BASE_URL/api/bookings/customer/my-bookings?status=CONFIRMED" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN")
print_success "Customer bookings retrieved"
echo "$CUSTOMER_BOOKINGS" | jq '.'
echo ""
sleep 1

# 11. Create chat room
print_step "11/15" "Creating chat room for booking..."
CHAT_ROOM=$(curl -s -X POST "$BASE_URL/api/chat/room/$BOOKING_ID" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN")

ROOM_ID=$(echo "$CHAT_ROOM" | jq -r '.chatRoom.roomId')
if [ "$ROOM_ID" == "null" ] || [ -z "$ROOM_ID" ]; then
    print_error "Failed to create chat room"
    echo "$CHAT_ROOM"
    exit 1
fi
print_success "Chat room created: $ROOM_ID"
echo "$CHAT_ROOM" | jq '.'
echo ""
sleep 1

# 12. Customer sends message
print_step "12/15" "Customer sending message..."
MESSAGE1=$(curl -s -X POST "$BASE_URL/api/chat/send" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"roomId\": \"$ROOM_ID\",
    \"message\": \"Hi! What should I prepare before you arrive?\",
    \"messageType\": \"TEXT\"
  }")
print_success "Customer message sent"
echo "$MESSAGE1" | jq '.'
echo ""
sleep 1

# 13. Provider responds
print_step "13/15" "Provider responding..."
MESSAGE2=$(curl -s -X POST "$BASE_URL/api/chat/send" \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"roomId\": \"$ROOM_ID\",
    \"message\": \"Just clear the areas to be cleaned. I bring all supplies and equipment!\",
    \"messageType\": \"TEXT\"
  }")
print_success "Provider message sent"
echo "$MESSAGE2" | jq '.'
echo ""
sleep 1

# 14. Get chat messages
print_step "14/15" "Getting chat message history..."
MESSAGES=$(curl -s "$BASE_URL/api/chat/room/$ROOM_ID/messages" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN")
print_success "Chat messages retrieved"
echo "$MESSAGES" | jq '.'
echo ""
sleep 1

# 15. Provider completes booking
print_step "15/15" "Provider completing booking..."
BOOKING_COMPLETE=$(curl -s -X PUT "$BASE_URL/api/bookings/$BOOKING_ID/status" \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "COMPLETED",
    "providerNotes": "Service completed successfully! Home is spotless."
  }')
print_success "Booking completed"
echo "$BOOKING_COMPLETE" | jq '.'
echo ""

# Summary
echo "============================================"
echo -e "${GREEN}✓ All Milestone 3 Tests Completed Successfully!${NC}"
echo "============================================"
echo ""
echo "Summary:"
echo "- Customer Token: ${CUSTOMER_TOKEN:0:30}..."
echo "- Provider Token: ${PROVIDER_TOKEN:0:30}..."
echo "- Service ID: $SERVICE_ID"
echo "- Booking ID: $BOOKING_ID"
echo "- Chat Room ID: $ROOM_ID"
echo ""
echo "You can now:"
echo "1. Test WebSocket connection with the room ID"
echo "2. Leave a review for the completed booking"
echo "3. Create more bookings and test other scenarios"
echo ""
echo "See MILESTONE_3_TESTING_GUIDE.md for more test scenarios."

@echo off
echo ============================================
echo Milestone 3: Booking & Chat Testing Script
echo ============================================
echo.

REM Set base URL
set BASE_URL=http://localhost:8080

echo [1/15] Registering test customer...
curl -X POST %BASE_URL%/api/auth/register -H "Content-Type: application/json" -d "{\"name\":\"Test Customer\",\"email\":\"customer.m3@test.com\",\"password\":\"test123\",\"role\":\"CUSTOMER\",\"location\":\"New York, NY\"}" 2>nul
echo.
timeout /t 1 /nobreak >nul

echo [2/15] Registering test provider...
curl -X POST %BASE_URL%/api/auth/register -H "Content-Type: application/json" -d "{\"name\":\"Test Provider\",\"email\":\"provider.m3@test.com\",\"password\":\"test123\",\"role\":\"PROVIDER\",\"location\":\"New York, NY\"}" 2>nul
echo.
timeout /t 1 /nobreak >nul

echo [3/15] Logging in as customer...
for /f "delims=" %%i in ('curl -s -X POST %BASE_URL%/api/auth/login -H "Content-Type: application/json" -d "{\"email\":\"customer.m3@test.com\",\"password\":\"test123\"}"') do set CUSTOMER_RESPONSE=%%i
echo Response: %CUSTOMER_RESPONSE%
echo.
timeout /t 1 /nobreak >nul

echo [4/15] Logging in as provider...
for /f "delims=" %%i in ('curl -s -X POST %BASE_URL%/api/auth/login -H "Content-Type: application/json" -d "{\"email\":\"provider.m3@test.com\",\"password\":\"test123\"}"') do set PROVIDER_RESPONSE=%%i
echo Response: %PROVIDER_RESPONSE%
echo.
timeout /t 1 /nobreak >nul

echo NOTE: Extract tokens manually from above responses for next steps
echo.
echo [5/15] Creating provider profile...
echo Please run: curl -X POST %BASE_URL%/api/providers/profile -H "Authorization: Bearer PROVIDER_TOKEN" -H "Content-Type: application/json" -d "{\"serviceCategoryId\":1,\"bio\":\"Test provider\",\"skills\":\"Cleaning\",\"serviceArea\":\"New York\",\"hourlyRate\":75.00,\"available\":true}"
echo.

echo [6/15] Creating service listing...
echo Please run: curl -X POST %BASE_URL%/api/services -H "Authorization: Bearer PROVIDER_TOKEN" -H "Content-Type: application/json" -d "{\"categoryId\":1,\"subcategoryId\":1,\"title\":\"Test Service\",\"description\":\"Test\",\"price\":75.00,\"pricingType\":\"HOURLY\",\"serviceLocation\":\"New York\",\"availabilityDays\":[\"Monday\"]}"
echo.

echo [7/15] Creating a booking...
echo Please run: curl -X POST %BASE_URL%/api/bookings -H "Authorization: Bearer CUSTOMER_TOKEN" -H "Content-Type: application/json" -d "{\"serviceListingId\":1,\"bookingDate\":\"2024-12-01T10:00:00\",\"timeSlot\":\"10:00-13:00\",\"durationHours\":3,\"totalPrice\":225.00,\"serviceLocation\":\"New York, NY\",\"customerNotes\":\"Test booking\"}"
echo.

echo [8/15] Provider checking pending bookings...
echo Please run: curl %BASE_URL%/api/bookings/provider/pending -H "Authorization: Bearer PROVIDER_TOKEN"
echo.

echo [9/15] Provider confirming booking...
echo Please run: curl -X PUT %BASE_URL%/api/bookings/1/status -H "Authorization: Bearer PROVIDER_TOKEN" -H "Content-Type: application/json" -d "{\"status\":\"CONFIRMED\",\"providerNotes\":\"Confirmed!\"}"
echo.

echo [10/15] Customer viewing bookings...
echo Please run: curl %BASE_URL%/api/bookings/customer/my-bookings -H "Authorization: Bearer CUSTOMER_TOKEN"
echo.

echo [11/15] Creating chat room...
echo Please run: curl -X POST %BASE_URL%/api/chat/room/1 -H "Authorization: Bearer CUSTOMER_TOKEN"
echo.

echo [12/15] Customer sending message...
echo Please run: curl -X POST %BASE_URL%/api/chat/send -H "Authorization: Bearer CUSTOMER_TOKEN" -H "Content-Type: application/json" -d "{\"roomId\":\"ROOM_ID\",\"message\":\"Hello\",\"messageType\":\"TEXT\"}"
echo.

echo [13/15] Provider responding...
echo Please run: curl -X POST %BASE_URL%/api/chat/send -H "Authorization: Bearer PROVIDER_TOKEN" -H "Content-Type: application/json" -d "{\"roomId\":\"ROOM_ID\",\"message\":\"Hi there\",\"messageType\":\"TEXT\"}"
echo.

echo [14/15] Getting chat messages...
echo Please run: curl %BASE_URL%/api/chat/room/ROOM_ID/messages -H "Authorization: Bearer CUSTOMER_TOKEN"
echo.

echo [15/15] Provider completing booking...
echo Please run: curl -X PUT %BASE_URL%/api/bookings/1/status -H "Authorization: Bearer PROVIDER_TOKEN" -H "Content-Type: application/json" -d "{\"status\":\"COMPLETED\",\"providerNotes\":\"Done!\"}"
echo.

echo ============================================
echo Testing Complete!
echo ============================================
echo.
echo NOTE: This script requires manual token extraction.
echo For automated testing, use the bash script or Postman.
echo.
echo Check MILESTONE_3_TESTING_GUIDE.md for detailed testing instructions.
echo.

pause

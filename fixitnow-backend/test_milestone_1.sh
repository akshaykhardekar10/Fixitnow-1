#!/bin/bash

# Milestone 1 Complete Testing Script
# This script tests all Week 1 & 2 requirements

BASE_URL="http://localhost:8080"
echo "🚀 Testing FixItNow Backend - Milestone 1"
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test counter
TESTS_PASSED=0
TESTS_FAILED=0

# Function to test API endpoint
test_api() {
    local test_name="$1"
    local method="$2"
    local endpoint="$3"
    local data="$4"
    local headers="$5"
    local expected_status="$6"
    
    echo -n "Testing: $test_name... "
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s -w "%{http_code}" -H "$headers" "$BASE_URL$endpoint")
    else
        response=$(curl -s -w "%{http_code}" -X "$method" -H "Content-Type: application/json" -H "$headers" -d "$data" "$BASE_URL$endpoint")
    fi
    
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" = "$expected_status" ]; then
        echo -e "${GREEN}✅ PASSED${NC} (HTTP $http_code)"
        TESTS_PASSED=$((TESTS_PASSED + 1))
        return 0
    else
        echo -e "${RED}❌ FAILED${NC} (HTTP $http_code, Expected $expected_status)"
        echo "Response: $body"
        TESTS_FAILED=$((TESTS_FAILED + 1))
        return 1
    fi
}

echo -e "${YELLOW}Phase 1: Health Check${NC}"
test_api "Application Health" "GET" "/api/health" "" "" "200"

echo -e "\n${YELLOW}Phase 2: Service Categories${NC}"
test_api "Get Service Categories" "GET" "/api/categories" "" "" "200"
test_api "Get Category by ID" "GET" "/api/categories/1" "" "" "200"

echo -e "\n${YELLOW}Phase 3: User Registration${NC}"
# Test customer registration
customer_data='{
    "name": "Test Customer",
    "email": "customer@test.com",
    "password": "test123",
    "role": "CUSTOMER",
    "location": "New York, NY"
}'
test_api "Register Customer" "POST" "/api/auth/register" "$customer_data" "" "200"

# Test provider registration
provider_data='{
    "name": "Test Provider", 
    "email": "provider@test.com",
    "password": "test123",
    "role": "PROVIDER",
    "location": "Los Angeles, CA"
}'
test_api "Register Provider" "POST" "/api/auth/register" "$provider_data" "" "200"

echo -e "\n${YELLOW}Phase 4: Authentication${NC}"
# Test customer login
customer_login='{
    "email": "customer@test.com",
    "password": "test123"
}'
customer_response=$(curl -s -X POST -H "Content-Type: application/json" -d "$customer_login" "$BASE_URL/api/auth/login")
customer_token=$(echo "$customer_response" | jq -r '.accessToken // empty')

if [ -n "$customer_token" ] && [ "$customer_token" != "null" ]; then
    echo -e "Customer Login: ${GREEN}✅ PASSED${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "Customer Login: ${RED}❌ FAILED${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test provider login
provider_login='{
    "email": "provider@test.com", 
    "password": "test123"
}'
provider_response=$(curl -s -X POST -H "Content-Type: application/json" -d "$provider_login" "$BASE_URL/api/auth/login")
provider_token=$(echo "$provider_response" | jq -r '.accessToken // empty')

if [ -n "$provider_token" ] && [ "$provider_token" != "null" ]; then
    echo -e "Provider Login: ${GREEN}✅ PASSED${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
else
    echo -e "Provider Login: ${RED}❌ FAILED${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

echo -e "\n${YELLOW}Phase 5: Role-based Access${NC}"
if [ -n "$customer_token" ]; then
    test_api "Customer Profile Access" "GET" "/api/users/profile" "" "Authorization: Bearer $customer_token" "200"
fi

if [ -n "$provider_token" ]; then
    test_api "Provider Profile Access" "GET" "/api/users/profile" "" "Authorization: Bearer $provider_token" "200"
fi

echo -e "\n${YELLOW}Phase 6: Provider Profile Management${NC}"
if [ -n "$provider_token" ]; then
    # Create provider profile
    profile_data='{
        "serviceCategoryId": 1,
        "bio": "Professional service provider with 5+ years experience",
        "skills": "Cleaning, Maintenance, Repair",
        "serviceArea": "Los Angeles, Beverly Hills, Santa Monica",
        "hourlyRate": 25.00,
        "available": true,
        "certifications": ["Certified Professional", "Insured"]
    }'
    test_api "Create Provider Profile" "POST" "/api/providers/profile" "$profile_data" "Authorization: Bearer $provider_token" "200"
    
    # Get provider profile
    test_api "Get Provider Profile" "GET" "/api/providers/profile" "" "Authorization: Bearer $provider_token" "200"
    
    # Update availability
    availability_data='{"available": false}'
    test_api "Update Availability" "PUT" "/api/providers/availability" "$availability_data" "Authorization: Bearer $provider_token" "200"
fi

echo -e "\n${YELLOW}Phase 7: Provider Discovery${NC}"
test_api "Get All Providers" "GET" "/api/providers" "" "" "200"
test_api "Get Providers by Category" "GET" "/api/providers/category/1" "" "" "200"
test_api "Get Providers by Location" "GET" "/api/providers/location?location=Los Angeles" "" "" "200"

echo -e "\n${YELLOW}Phase 8: Admin Functions${NC}"
# Test with default admin
admin_login='{
    "email": "admin@fixitnow.com",
    "password": "admin123"
}'
admin_response=$(curl -s -X POST -H "Content-Type: application/json" -d "$admin_login" "$BASE_URL/api/auth/login")
admin_token=$(echo "$admin_response" | jq -r '.accessToken // empty')

if [ -n "$admin_token" ] && [ "$admin_token" != "null" ]; then
    echo -e "Admin Login: ${GREEN}✅ PASSED${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
    
    test_api "Admin Get All Users" "GET" "/api/admin/users" "" "Authorization: Bearer $admin_token" "200"
    test_api "Admin Get User Stats" "GET" "/api/admin/users/stats" "" "Authorization: Bearer $admin_token" "200"
else
    echo -e "Admin Login: ${RED}❌ FAILED${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

echo -e "\n${YELLOW}Phase 9: Validation Tests${NC}"
test_api "Field Mapping Validation" "GET" "/api/validate/field-mapping" "" "" "200"
test_api "Create Test Users" "POST" "/api/validate/test-user-creation" "" "" "200"

# Final Results
echo -e "\n=========================================="
echo -e "${YELLOW}MILESTONE 1 TESTING COMPLETE${NC}"
echo -e "=========================================="
echo -e "Tests Passed: ${GREEN}$TESTS_PASSED${NC}"
echo -e "Tests Failed: ${RED}$TESTS_FAILED${NC}"
echo -e "Total Tests: $((TESTS_PASSED + TESTS_FAILED))"

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "\n${GREEN}🎉 ALL TESTS PASSED! Milestone 1 is complete and ready for frontend integration.${NC}"
    exit 0
else
    echo -e "\n${RED}⚠️  Some tests failed. Please check the application logs and fix issues before proceeding.${NC}"
    exit 1
fi

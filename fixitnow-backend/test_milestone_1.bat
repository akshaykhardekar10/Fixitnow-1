@echo off
setlocal enabledelayedexpansion

REM Milestone 1 Complete Testing Script for Windows
REM This script tests all Week 1 & 2 requirements

set BASE_URL=http://localhost:8080
echo 🚀 Testing FixItNow Backend - Milestone 1
echo ==========================================

set TESTS_PASSED=0
set TESTS_FAILED=0

echo Phase 1: Health Check
echo Testing Application Health...
curl -s -w "%%{http_code}" %BASE_URL%/api/health > temp_response.txt
set /p response=<temp_response.txt
if "!response:~-3!" == "200" (
    echo ✅ Health Check PASSED
    set /a TESTS_PASSED+=1
) else (
    echo ❌ Health Check FAILED
    set /a TESTS_FAILED+=1
)

echo.
echo Phase 2: Service Categories
echo Testing Get Service Categories...
curl -s -w "%%{http_code}" %BASE_URL%/api/categories > temp_response.txt
set /p response=<temp_response.txt
if "!response:~-3!" == "200" (
    echo ✅ Service Categories PASSED
    set /a TESTS_PASSED+=1
) else (
    echo ❌ Service Categories FAILED
    set /a TESTS_FAILED+=1
)

echo.
echo Phase 3: User Registration
echo Testing Customer Registration...
curl -s -w "%%{http_code}" -X POST -H "Content-Type: application/json" -d "{\"name\":\"Test Customer\",\"email\":\"customer@test.com\",\"password\":\"test123\",\"role\":\"CUSTOMER\",\"location\":\"New York, NY\"}" %BASE_URL%/api/auth/register > temp_response.txt
set /p response=<temp_response.txt
if "!response:~-3!" == "200" (
    echo ✅ Customer Registration PASSED
    set /a TESTS_PASSED+=1
) else (
    echo ❌ Customer Registration FAILED
    set /a TESTS_FAILED+=1
)

echo Testing Provider Registration...
curl -s -w "%%{http_code}" -X POST -H "Content-Type: application/json" -d "{\"name\":\"Test Provider\",\"email\":\"provider@test.com\",\"password\":\"test123\",\"role\":\"PROVIDER\",\"location\":\"Los Angeles, CA\"}" %BASE_URL%/api/auth/register > temp_response.txt
set /p response=<temp_response.txt
if "!response:~-3!" == "200" (
    echo ✅ Provider Registration PASSED
    set /a TESTS_PASSED+=1
) else (
    echo ❌ Provider Registration FAILED
    set /a TESTS_FAILED+=1
)

echo.
echo Phase 4: Authentication
echo Testing Customer Login...
curl -s -X POST -H "Content-Type: application/json" -d "{\"email\":\"customer@test.com\",\"password\":\"test123\"}" %BASE_URL%/api/auth/login > customer_login.json
findstr /C:"accessToken" customer_login.json >nul
if !errorlevel! == 0 (
    echo ✅ Customer Login PASSED
    set /a TESTS_PASSED+=1
) else (
    echo ❌ Customer Login FAILED
    set /a TESTS_FAILED+=1
)

echo Testing Provider Login...
curl -s -X POST -H "Content-Type: application/json" -d "{\"email\":\"provider@test.com\",\"password\":\"test123\"}" %BASE_URL%/api/auth/login > provider_login.json
findstr /C:"accessToken" provider_login.json >nul
if !errorlevel! == 0 (
    echo ✅ Provider Login PASSED
    set /a TESTS_PASSED+=1
) else (
    echo ❌ Provider Login FAILED
    set /a TESTS_FAILED+=1
)

echo.
echo Phase 5: Provider Discovery
echo Testing Get All Providers...
curl -s -w "%%{http_code}" %BASE_URL%/api/providers > temp_response.txt
set /p response=<temp_response.txt
if "!response:~-3!" == "200" (
    echo ✅ Get Providers PASSED
    set /a TESTS_PASSED+=1
) else (
    echo ❌ Get Providers FAILED
    set /a TESTS_FAILED+=1
)

echo Testing Get Providers by Category...
curl -s -w "%%{http_code}" %BASE_URL%/api/providers/category/1 > temp_response.txt
set /p response=<temp_response.txt
if "!response:~-3!" == "200" (
    echo ✅ Providers by Category PASSED
    set /a TESTS_PASSED+=1
) else (
    echo ❌ Providers by Category FAILED
    set /a TESTS_FAILED+=1
)

echo.
echo Phase 6: Admin Functions
echo Testing Admin Login...
curl -s -X POST -H "Content-Type: application/json" -d "{\"email\":\"admin@fixitnow.com\",\"password\":\"admin123\"}" %BASE_URL%/api/auth/login > admin_login.json
findstr /C:"accessToken" admin_login.json >nul
if !errorlevel! == 0 (
    echo ✅ Admin Login PASSED
    set /a TESTS_PASSED+=1
) else (
    echo ❌ Admin Login FAILED
    set /a TESTS_FAILED+=1
)

echo.
echo Phase 7: Validation Tests
echo Testing Field Mapping Validation...
curl -s -w "%%{http_code}" %BASE_URL%/api/validate/field-mapping > temp_response.txt
set /p response=<temp_response.txt
if "!response:~-3!" == "200" (
    echo ✅ Field Mapping PASSED
    set /a TESTS_PASSED+=1
) else (
    echo ❌ Field Mapping FAILED
    set /a TESTS_FAILED+=1
)

REM Cleanup temp files
del temp_response.txt customer_login.json provider_login.json admin_login.json 2>nul

echo.
echo ==========================================
echo MILESTONE 1 TESTING COMPLETE
echo ==========================================
echo Tests Passed: %TESTS_PASSED%
echo Tests Failed: %TESTS_FAILED%
set /a TOTAL_TESTS=%TESTS_PASSED%+%TESTS_FAILED%
echo Total Tests: %TOTAL_TESTS%

if %TESTS_FAILED% == 0 (
    echo.
    echo 🎉 ALL TESTS PASSED! Milestone 1 is complete and ready for frontend integration.
    exit /b 0
) else (
    echo.
    echo ⚠️  Some tests failed. Please check the application logs and fix issues before proceeding.
    exit /b 1
)

endlocal

@echo off
REM Quick API Test Script for Review Endpoints

SET BASE_URL=http://localhost:8080/api
SET SHIP_ID=1
SET HOTEL_ID=5

echo ================================
echo  TESTING REVIEW API ENDPOINTS
echo ================================
echo.

echo [TEST 1] GET Ship Reviews (shipId=%SHIP_ID%)
echo ----------------------------------------
curl -X GET "%BASE_URL%/ship/%SHIP_ID%/reviews" -H "Content-Type: application/json"
echo.
echo.

echo [TEST 2] GET Hotel Reviews (hotelId=%HOTEL_ID%)
echo ----------------------------------------
curl -X GET "%BASE_URL%/hotel/%HOTEL_ID%/reviews" -H "Content-Type: application/json"
echo.
echo.

echo ================================
echo To test POST endpoints:
echo 1. First login to get JWT token
echo 2. Replace YOUR_TOKEN in the commands below
echo.
echo POST Ship Review:
echo curl -X POST "%BASE_URL%/ship/%SHIP_ID%/reviews" ^
echo   -H "Content-Type: application/json" ^
echo   -H "Authorization: Bearer YOUR_TOKEN" ^
echo   -d "{\"name\":\"Test User\",\"content\":\"Great ship!\",\"stars\":5}"
echo.
echo POST Hotel Review:
echo curl -X POST "%BASE_URL%/hotel/%HOTEL_ID%/reviews" ^
echo   -H "Content-Type: application/json" ^
echo   -H "Authorization: Bearer YOUR_TOKEN" ^
echo   -d "{\"name\":\"Test User\",\"content\":\"Nice hotel!\",\"stars\":4}"
echo ================================

pause

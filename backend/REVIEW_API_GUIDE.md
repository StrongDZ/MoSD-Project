# Hướng Dẫn Chạy Backend Review API

## 📋 Yêu Cầu
- Java 17+
- PostgreSQL
- Maven
- Spring Boot đang chạy

## 🗄️ Bước 1: Thiết Lập Database

### 1.1. Kết nối vào PostgreSQL
```bash
psql -U postgres
```

### 1.2. Chọn database của project
```sql
\c your_database_name
```

### 1.3. Chạy script tạo bảng reviews
```bash
# Từ thư mục backend
psql -U postgres -d your_database_name -f review_tables.sql
```

Hoặc copy-paste trực tiếp vào psql:
```sql
-- Bảng review cho Ship
CREATE TABLE ship_reviews (
    review_id SERIAL PRIMARY KEY,
    ship_id INTEGER NOT NULL,
    user_id INTEGER,
    name VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    stars INTEGER NOT NULL CHECK (stars >= 1 AND stars <= 5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ship_id) REFERENCES ship(ship_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES "user"(user_id) ON DELETE SET NULL
);

-- Bảng review cho Hotel
CREATE TABLE hotel_reviews (
    review_id SERIAL PRIMARY KEY,
    hotel_id INTEGER NOT NULL,
    user_id INTEGER,
    name VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    stars INTEGER NOT NULL CHECK (stars >= 1 AND stars <= 5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (hotel_id) REFERENCES hotel(hotel_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES "user"(user_id) ON DELETE SET NULL
);

-- Index để tăng tốc độ query
CREATE INDEX idx_ship_reviews_ship_id ON ship_reviews(ship_id);
CREATE INDEX idx_hotel_reviews_hotel_id ON hotel_reviews(hotel_id);
CREATE INDEX idx_ship_reviews_stars ON ship_reviews(stars);
CREATE INDEX idx_hotel_reviews_stars ON hotel_reviews(stars);
```

### 1.4. Kiểm tra bảng đã tạo thành công
```sql
\dt ship_reviews
\dt hotel_reviews
```

## 🚀 Bước 2: Chạy Spring Boot Application

### 2.1. Build project
```bash
cd backend
mvn clean install
```

### 2.2. Chạy application
```bash
mvn spring-boot:run
```

Hoặc nếu dùng IDE (IntelliJ/Eclipse), chạy file:
```
TravelAgentBackendApplication.java
```

### 2.3. Kiểm tra server đã chạy
Server sẽ chạy tại: `http://localhost:8080`

Kiểm tra logs xem có lỗi không:
```
2024-12-16 ... : Started TravelAgentBackendApplication in ... seconds
```

## 🧪 Bước 3: Test API với Postman/Curl

### 3.1. Lấy JWT Token (để tạo review)

**Đăng nhập User:**
```bash
POST http://localhost:8080/api/auth/login/user

Body (JSON):
{
  "username": "your_username",
  "password": "your_password"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

Copy token này để dùng cho các request tiếp theo.

---

## 📝 TEST SHIP REVIEWS

### 3.2. GET - Lấy tất cả reviews của Ship

```bash
GET http://localhost:8080/api/ship/1/reviews
```

**Curl:**
```bash
curl -X GET "http://localhost:8080/api/ship/1/reviews" \
  -H "Content-Type: application/json"
```

**Response mẫu:**
```json
{
  "message": "Reviews retrieved successfully",
  "responseCode": 200,
  "data": [
    {
      "reviewId": 1,
      "name": "Nguyễn Văn A",
      "content": "Du thuyền rất đẹp, dịch vụ tốt!",
      "stars": 5,
      "createdAt": "2024-12-16T10:30:00"
    }
  ]
}
```

### 3.3. POST - Tạo review mới cho Ship

```bash
POST http://localhost:8080/api/ship/1/reviews

Headers:
- Content-Type: application/json
- Authorization: Bearer YOUR_JWT_TOKEN

Body (JSON):
{
  "name": "Nguyễn Văn B",
  "content": "Chuyến đi tuyệt vời, cảnh đẹp, phòng sạch sẽ!",
  "stars": 5
}
```

**Curl:**
```bash
curl -X POST "http://localhost:8080/api/ship/1/reviews" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Nguyễn Văn B",
    "content": "Chuyến đi tuyệt vời!",
    "stars": 5
  }'
```

**Response mẫu:**
```json
{
  "message": "Review created successfully",
  "responseCode": 201,
  "data": {
    "reviewId": 2,
    "name": "Nguyễn Văn B",
    "content": "Chuyến đi tuyệt vời!",
    "stars": 5,
    "createdAt": "2024-12-16T11:00:00"
  }
}
```

---

## 🏨 TEST HOTEL REVIEWS

### 3.4. GET - Lấy tất cả reviews của Hotel

```bash
GET http://localhost:8080/api/hotel/5/reviews
```

**Curl:**
```bash
curl -X GET "http://localhost:8080/api/hotel/5/reviews" \
  -H "Content-Type: application/json"
```

### 3.5. POST - Tạo review mới cho Hotel

```bash
POST http://localhost:8080/api/hotel/5/reviews

Headers:
- Content-Type: application/json
- Authorization: Bearer YOUR_JWT_TOKEN

Body (JSON):
{
  "name": "Trần Thị C",
  "content": "Khách sạn sạch đẹp, nhân viên nhiệt tình!",
  "stars": 4
}
```

**Curl:**
```bash
curl -X POST "http://localhost:8080/api/hotel/5/reviews" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Trần Thị C",
    "content": "Khách sạn sạch đẹp!",
    "stars": 4
  }'
```

---

## 🧩 Test với Postman

### Import vào Postman Collection:

```json
{
  "info": {
    "name": "Review API Tests",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Get Ship Reviews",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/api/ship/1/reviews",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "ship", "1", "reviews"]
        }
      }
    },
    {
      "name": "Create Ship Review",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          },
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"name\": \"Test User\",\n  \"content\": \"Great ship!\",\n  \"stars\": 5\n}"
        },
        "url": {
          "raw": "http://localhost:8080/api/ship/1/reviews",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "ship", "1", "reviews"]
        }
      }
    },
    {
      "name": "Get Hotel Reviews",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/api/hotel/5/reviews",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "hotel", "5", "reviews"]
        }
      }
    },
    {
      "name": "Create Hotel Review",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          },
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"name\": \"Test User\",\n  \"content\": \"Nice hotel!\",\n  \"stars\": 4\n}"
        },
        "url": {
          "raw": "http://localhost:8080/api/hotel/5/reviews",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "hotel", "5", "reviews"]
        }
      }
    }
  ]
}
```

---

## ❌ Xử Lý Lỗi Thường Gặp

### Lỗi 1: Table không tồn tại
```
ERROR: relation "ship_reviews" does not exist
```
**Giải pháp:** Chạy lại script SQL ở Bước 1.3

### Lỗi 2: Foreign key constraint
```
ERROR: insert or update on table "ship_reviews" violates foreign key constraint
```
**Giải pháp:** Kiểm tra ship_id hoặc hotel_id có tồn tại trong database không.

### Lỗi 3: 401 Unauthorized khi POST
```json
{
  "message": "Unauthorized",
  "responseCode": 401
}
```
**Giải pháp:** 
- Đăng nhập lại để lấy token mới
- Kiểm tra header Authorization: Bearer {token}

### Lỗi 4: 400 Bad Request - Validation
```json
{
  "message": "Stars must be at least 1",
  "responseCode": 400
}
```
**Giải pháp:** Kiểm tra:
- `name` không được trống
- `content` không được trống
- `stars` phải từ 1-5

---

## ✅ Kiểm Tra Dữ Liệu Trực Tiếp

### Xem dữ liệu trong database:
```sql
-- Xem tất cả reviews của ship
SELECT * FROM ship_reviews WHERE ship_id = 1;

-- Xem tất cả reviews của hotel
SELECT * FROM hotel_reviews WHERE hotel_id = 5;

-- Đếm số reviews theo số sao
SELECT stars, COUNT(*) 
FROM ship_reviews 
WHERE ship_id = 1 
GROUP BY stars;
```

---

## 🎯 Test Frontend Integration

Sau khi API hoạt động, test với frontend:

1. Mở browser: `http://localhost:5173` (hoặc port frontend của bạn)
2. Vào trang chi tiết khách sạn hoặc du thuyền
3. Click tab "Đánh giá"
4. Thử submit review mới
5. Kiểm tra review hiển thị đúng

---

## 📞 Support

Nếu gặp vấn đề, kiểm tra:
1. Spring Boot logs: `backend/logs/`
2. Database connection: `application.properties`
3. Port conflicts: `8080` phải available
4. JWT token expire: refresh lại token

**Cấu trúc API:**
- Base URL: `http://localhost:8080/api`
- Ship Reviews: `/ship/{id}/reviews`
- Hotel Reviews: `/hotel/{id}/reviews`

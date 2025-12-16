# 🚀 QUICK START - Review API

## ⚡ Chạy nhanh trong 3 bước:

### 1️⃣ Setup Database
```bash
psql -U postgres -d your_db -f backend/review_tables.sql
psql -U postgres -d your_db -f backend/review_sample_data.sql
```

### 2️⃣ Start Backend
```bash
# Windows
start-backend.bat

# Linux/Mac
chmod +x start-backend.sh
./start-backend.sh
```

### 3️⃣ Test API
```bash
# Windows
test-review-api.bat

# Linux/Mac
curl http://localhost:8080/api/ship/1/reviews
curl http://localhost:8080/api/hotel/5/reviews
```

---

## 📋 API Cheat Sheet

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/ship/{id}/reviews` | ❌ | Lấy reviews của ship |
| POST | `/api/ship/{id}/reviews` | ✅ | Tạo review cho ship |
| GET | `/api/hotel/{id}/reviews` | ❌ | Lấy reviews của hotel |
| POST | `/api/hotel/{id}/reviews` | ✅ | Tạo review cho hotel |

---

## 💡 Test Request Examples

### ✅ GET Request (No Auth)
```bash
curl http://localhost:8080/api/ship/1/reviews
```

### ✅ POST Request (Need JWT Token)
```bash
# 1. Login first
curl -X POST http://localhost:8080/api/auth/login/user \
  -H "Content-Type: application/json" \
  -d '{"username":"your_user","password":"your_pass"}'

# 2. Copy token from response, then:
curl -X POST http://localhost:8080/api/ship/1/reviews \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"name":"John","content":"Great!","stars":5}'
```

---

## 🎯 Request Body Format

```json
{
  "name": "Tên người đánh giá",
  "content": "Nội dung đánh giá chi tiết",
  "stars": 5
}
```

**Validation:**
- `name`: Không được trống
- `content`: Không được trống  
- `stars`: Phải từ 1-5

---

## ✅ Response Format

**Success (200/201):**
```json
{
  "message": "Reviews retrieved successfully",
  "responseCode": 200,
  "data": [
    {
      "reviewId": 1,
      "name": "Nguyễn Văn A",
      "content": "Tuyệt vời!",
      "stars": 5,
      "createdAt": "2024-12-16T10:30:00"
    }
  ]
}
```

**Error (400/401/500):**
```json
{
  "message": "Error message",
  "responseCode": 400,
  "data": null
}
```

---

## 🔧 Troubleshooting

| Problem | Solution |
|---------|----------|
| Port 8080 busy | `netstat -ano \| findstr :8080` kill process |
| Database error | Check connection in `application.properties` |
| 401 Unauthorized | Login again, get new JWT token |
| Table not exist | Run `review_tables.sql` |

---

## 📁 Files Created

```
backend/
├── review_tables.sql              # Database schema
├── review_sample_data.sql         # Sample data
├── REVIEW_API_GUIDE.md           # Full documentation
├── src/main/java/com/travel_agent/
│   ├── models/entity/
│   │   ├── ship/ShipReviewEntity.java
│   │   └── hotel/HotelReviewEntity.java
│   ├── repositories/
│   │   ├── ship/ShipReviewRepository.java
│   │   └── hotel/HotelReviewRepository.java
│   ├── dto/
│   │   ├── ReviewDTO.java
│   │   └── ReviewRequestDTO.java
│   ├── services/
│   │   ├── ShipReviewService.java
│   │   └── HotelReviewService.java
│   └── controllers/
│       ├── ShipReviewController.java
│       └── HotelReviewController.java
```

---

## 🎓 Next Steps

1. ✅ Run SQL scripts
2. ✅ Start backend server
3. ✅ Test GET endpoints (no auth needed)
4. ✅ Login to get JWT token
5. ✅ Test POST endpoints with token
6. ✅ Check frontend integration
7. ✅ Deploy to production

**Full Guide:** See `REVIEW_API_GUIDE.md`

-- Insert sample reviews for testing
-- Chạy file này sau khi đã tạo tables với review_tables.sql

-- ============================================================
-- 0. INSERT SAMPLE COMPANIES FIRST (vì có foreign key constraint)
-- ============================================================
INSERT INTO company (company_id, company_name, username, email, password, role)
VALUES
(1, 'Paradise Cruises', 'paradise_admin', 'admin@paradisecruises.com', '$2a$10$dummyHashedPassword1', 'COMPANY'),
(2, 'Coastal Tours', 'coastal_admin', 'admin@coastaltours.com', '$2a$10$dummyHashedPassword2', 'COMPANY'),
(3, 'Vinpearl', 'vinpearl_admin', 'admin@vinpearl.com', '$2a$10$dummyHashedPassword3', 'COMPANY'),
(4, 'Saigon Hotels', 'saigon_admin', 'admin@saigonhotels.com', '$2a$10$dummyHashedPassword4', 'COMPANY')
ON CONFLICT (company_id) DO NOTHING;

-- ============================================================
-- 1. INSERT SAMPLE SHIPS (nếu chưa có)
-- ============================================================
INSERT INTO ship (ship_id, ship_name, launch, cabin, shell, trip, company_name, ship_price, address, map_link, thumbnail, company_id) 
VALUES
(1, 'Du thuyền Hạ Long Pearl', 2020, 20, 'Gỗ cao cấp', 'Vịnh Hạ Long 2N1Đ', 'Paradise Cruises', 2500000, 'Bãi Cháy, Hạ Long, Quảng Ninh', 'https://maps.google.com/halong-pearl', 'https://images.example.com/ship1.jpg', 1),
(2, 'Tàu Sapa Express', 2019, 15, 'Thép hiện đại', 'Đà Nẵng - Hội An 1N1Đ', 'Coastal Tours', 1800000, 'Cầu Rồng, Đà Nẵng', 'https://maps.google.com/sapa-express', 'https://images.example.com/ship2.jpg', 2)
ON CONFLICT (ship_id) DO NOTHING;

-- ============================================================
-- 2. INSERT SAMPLE HOTELS (nếu chưa có)
-- ============================================================
INSERT INTO hotel (hotel_id, hotel_name, total_rooms, company_name, hotel_price, city, address, map_link, thumbnail, company_id)
VALUES
(5, 'Vinpearl Resort Nha Trang', 485, 'Vinpearl', 3200000, 'Nha Trang', 'Hòn Tre, Vĩnh Nguyên, Nha Trang', 'https://maps.google.com/vinpearl-nhatrang', 'https://images.example.com/hotel5.jpg', 3),
(10, 'Khách sạn Sài Gòn', 120, 'Saigon Hotels', 850000, 'Hà Nội', '34 Hàng Bài, Hoàn Kiếm, Hà Nội', 'https://maps.google.com/saigon-hanoi', 'https://images.example.com/hotel10.jpg', 4)
ON CONFLICT (hotel_id) DO NOTHING;

-- ============================================================
-- 3. INSERT SAMPLE REVIEWS
-- ============================================================

-- Sample Ship Reviews
INSERT INTO ship_reviews (ship_id, user_id, name, content, stars) VALUES
(1, NULL, 'Nguyễn Văn A', 'Du thuyền rất đẹp, cảnh quan tuyệt vời! Phòng ốc sạch sẽ, nhân viên nhiệt tình.', 5),
(1, NULL, 'Trần Thị B', 'Chuyến đi đáng nhớ, ăn uống ngon. Tuy nhiên giá hơi cao một chút.', 4),
(1, NULL, 'Lê Văn C', 'Bình thường, không có gì đặc biệt. Cần cải thiện dịch vụ.', 3),
(2, NULL, 'Phạm Thị D', 'Tuyệt vời! Sẽ quay lại lần sau. View biển đẹp lắm!', 5),
(2, NULL, 'Hoàng Văn E', 'Khá ổn, giá cả hợp lý. Nhưng cần cải thiện thêm tiện nghi.', 4);

-- Sample Hotel Reviews
INSERT INTO hotel_reviews (hotel_id, user_id, name, content, stars) VALUES
(5, NULL, 'Nguyễn Thị F', 'Khách sạn sang trọng, phòng rộng rãi, sạch sẽ. Rất hài lòng!', 5),
(5, NULL, 'Trần Văn G', 'Vị trí thuận lợi, gần trung tâm. Nhân viên thân thiện.', 4),
(5, NULL, 'Lê Thị H', 'Tốt nhưng giá hơi cao. Bữa sáng ngon.', 4),
(10, NULL, 'Phạm Văn I', 'Không đáng tiền. Phòng nhỏ và cũ. Sẽ không quay lại.', 2),
(10, NULL, 'Hoàng Thị K', 'View đẹp nhưng dịch vụ kém. Cần cải thiện.', 3);

-- Verify data inserted
SELECT 'Ship Reviews:' as table_name, COUNT(*) as total FROM ship_reviews
UNION ALL
SELECT 'Hotel Reviews:' as table_name, COUNT(*) as total FROM hotel_reviews;

-- Show sample data
SELECT * FROM ship_reviews ORDER BY created_at DESC LIMIT 3;
SELECT * FROM hotel_reviews ORDER BY created_at DESC LIMIT 3;

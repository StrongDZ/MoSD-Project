-- Insert sample reviews for testing
-- Chạy file này sau khi đã tạo tables với review_tables.sql

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

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

package com.travel_agent.repositories.hotel;

import com.travel_agent.models.entity.hotel.HotelReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelReviewRepository extends JpaRepository<HotelReviewEntity, Integer> {
    List<HotelReviewEntity> findByHotelIdOrderByCreatedAtDesc(Integer hotelId);
    List<HotelReviewEntity> findByHotelIdAndStarsOrderByCreatedAtDesc(Integer hotelId, Integer stars);
}

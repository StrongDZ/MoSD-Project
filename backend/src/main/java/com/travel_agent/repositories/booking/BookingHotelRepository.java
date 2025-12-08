package com.travel_agent.repositories.booking;

import com.travel_agent.models.entity.booking.BookingHotelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingHotelRepository extends JpaRepository<BookingHotelEntity, Integer> {
    List<BookingHotelEntity> findByUserId(Integer userId);
    List<BookingHotelEntity> findByHotelHotelId(Integer hotelId);
} 
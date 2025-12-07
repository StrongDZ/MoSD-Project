package com.travel_agent.repositories.hotel;

import com.travel_agent.models.entity.hotel.HotelRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRoomRepository extends JpaRepository<HotelRoomEntity, Integer> {
    List<HotelRoomEntity> findByHotel_HotelId(Integer hotelId);
    HotelRoomEntity findByHotelRoomId(Integer hotelRoomId);
}
package com.travel_agent.repositories.hotel;

import com.travel_agent.models.entity.hotel.HotelImageEntity;
import com.travel_agent.models.id.hotel.HotelImageId;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelImageRepository extends JpaRepository<HotelImageEntity, HotelImageId> {
    List<HotelImageEntity> findByHotelId(Integer hotelId);
}
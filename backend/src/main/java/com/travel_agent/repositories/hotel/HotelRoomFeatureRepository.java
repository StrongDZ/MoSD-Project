package com.travel_agent.repositories.hotel;

import com.travel_agent.models.entity.hotel.HotelRoomFeatureEntity;
import com.travel_agent.models.id.hotel.HotelRoomFeatureId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRoomFeatureRepository extends JpaRepository<HotelRoomFeatureEntity, HotelRoomFeatureId> {
    List<HotelRoomFeatureEntity> findByHotelRoomId(Integer hotelRoomId);

    void deleteByHotelRoomId(Integer hotelRoomId);
}
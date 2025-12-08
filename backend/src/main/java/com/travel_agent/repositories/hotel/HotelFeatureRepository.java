package com.travel_agent.repositories.hotel;

import com.travel_agent.models.entity.hotel.HotelFeatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.travel_agent.models.id.hotel.HotelFeatureId;
import java.util.List;

public interface HotelFeatureRepository extends JpaRepository<HotelFeatureEntity, HotelFeatureId> {
    List<HotelFeatureEntity> findByHotelId(Integer hotelId);
}
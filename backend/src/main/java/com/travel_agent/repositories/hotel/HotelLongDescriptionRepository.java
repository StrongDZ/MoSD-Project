package com.travel_agent.repositories.hotel;

import com.travel_agent.models.entity.hotel.HotelLongDescriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.travel_agent.models.id.hotel.HotelDescriptionId;
import java.util.List;

public interface HotelLongDescriptionRepository extends JpaRepository<HotelLongDescriptionEntity, HotelDescriptionId> {
    List<HotelLongDescriptionEntity> findByHotelId(Integer hotelId);
}
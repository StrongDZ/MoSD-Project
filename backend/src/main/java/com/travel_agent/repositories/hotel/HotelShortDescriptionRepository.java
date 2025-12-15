package com.travel_agent.repositories.hotel;

import com.travel_agent.models.entity.hotel.HotelShortDescriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.travel_agent.models.id.hotel.HotelDescriptionId;
import java.util.List;

public interface HotelShortDescriptionRepository extends JpaRepository<HotelShortDescriptionEntity, HotelDescriptionId> {
    List<HotelShortDescriptionEntity> findByHotelId(Integer hotelId);
}
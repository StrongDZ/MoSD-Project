package com.travel_agent.repositories.ship;

import com.travel_agent.models.entity.ship.ShipRoomFeatureEntity;
import com.travel_agent.models.id.ship.ShipRoomFeatureId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipRoomFeatureRepository extends JpaRepository<ShipRoomFeatureEntity, ShipRoomFeatureId> {
    List<ShipRoomFeatureEntity> findByShipRoomId(Integer hotelRoomId);

    void deleteByShipRoomId(Integer shipRoomId);
}
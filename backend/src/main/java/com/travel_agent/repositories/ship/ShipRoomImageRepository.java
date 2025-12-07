package com.travel_agent.repositories.ship;

import com.travel_agent.models.entity.ship.ShipRoomImageEntity;
import com.travel_agent.models.id.ship.ShipRoomImageId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipRoomImageRepository extends JpaRepository<ShipRoomImageEntity, ShipRoomImageId> {
    List<ShipRoomImageEntity> findByRoomId(Integer roomId);
    void deleteByRoomId(Integer roomId);
}
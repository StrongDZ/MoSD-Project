package com.travel_agent.repositories.ship;

import com.travel_agent.models.entity.ship.ShipRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipRoomRepository extends JpaRepository<ShipRoomEntity, Integer> {
    List<ShipRoomEntity> findByShip_ShipId(Integer shipId);

    ShipRoomEntity findByShipRoomId(Integer shipRoomId);
}
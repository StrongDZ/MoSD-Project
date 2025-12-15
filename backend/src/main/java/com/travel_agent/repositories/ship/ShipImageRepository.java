package com.travel_agent.repositories.ship;

import com.travel_agent.models.entity.ship.ShipImageEntity;
import com.travel_agent.models.id.ship.ShipImageId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipImageRepository extends JpaRepository<ShipImageEntity, ShipImageId> {
    List<ShipImageEntity> findByShipId(Integer shipId);
}
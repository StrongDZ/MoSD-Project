package com.travel_agent.repositories.ship;

import com.travel_agent.models.entity.ship.ShipFeatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.travel_agent.models.id.ship.ShipFeatureId;
import java.util.List;

public interface ShipFeatureRepository extends JpaRepository<ShipFeatureEntity, ShipFeatureId> {
    List<ShipFeatureEntity> findByShipId(Integer hotelId);
}
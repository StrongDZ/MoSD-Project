package com.travel_agent.repositories.ship;

import com.travel_agent.models.entity.ship.ShipLongDescriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.travel_agent.models.id.ship.ShipDescriptionId;
import java.util.List;

public interface ShipLongDescriptionRepository extends JpaRepository<ShipLongDescriptionEntity, ShipDescriptionId> {
    List<ShipLongDescriptionEntity> findByShipId(Integer shipId);
}
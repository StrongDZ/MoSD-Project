package com.travel_agent.repositories.ship;

import com.travel_agent.models.entity.ship.ShipShortDescriptionEntity;
import com.travel_agent.models.id.ship.ShipDescriptionId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShipShortDescriptionRepository extends JpaRepository<ShipShortDescriptionEntity, ShipDescriptionId> {
    List<ShipShortDescriptionEntity> findByShipId(Integer shipId);
}
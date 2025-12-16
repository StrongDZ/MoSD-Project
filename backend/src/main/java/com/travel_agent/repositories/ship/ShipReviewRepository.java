package com.travel_agent.repositories.ship;

import com.travel_agent.models.entity.ship.ShipReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipReviewRepository extends JpaRepository<ShipReviewEntity, Integer> {
    List<ShipReviewEntity> findByShipIdOrderByCreatedAtDesc(Integer shipId);
    List<ShipReviewEntity> findByShipIdAndStarsOrderByCreatedAtDesc(Integer shipId, Integer stars);
}

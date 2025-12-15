package com.travel_agent.repositories.ship;

import com.travel_agent.models.entity.ship.ShipEntity;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShipRepository extends JpaRepository<ShipEntity, Integer> {
    @Query("SELECT s FROM ShipEntity s WHERE " +
            "((:name IS NULL OR :name = '' OR (s.shipName IS NOT NULL AND LOWER(s.shipName) LIKE LOWER(CONCAT('%', :name, '%'))))) AND " +
            "((:minPrice IS NULL OR s.shipPrice >= :minPrice)) AND " +
            "((:maxPrice IS NULL OR s.shipPrice <= :maxPrice)) AND " +
            "((:trip IS NULL OR :trip = '' OR (s.address IS NOT NULL AND LOWER(s.trip) LIKE LOWER(CONCAT('%', :trip, '%')))))")
    Page<ShipEntity> findByShipNamePriceAndAddress(
            @Param("name") String name,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("trip") String trip,
            Pageable pageable);

    List<ShipEntity> findByShipNameContainingIgnoreCase(String keyword);
}
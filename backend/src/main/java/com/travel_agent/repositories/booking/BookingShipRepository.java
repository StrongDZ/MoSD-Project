package com.travel_agent.repositories.booking;

import com.travel_agent.models.entity.booking.BookingShipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingShipRepository extends JpaRepository<BookingShipEntity, Integer> {
    List<BookingShipEntity> findByUserId(Integer userId);
    List<BookingShipEntity> findByShipShipId(Integer shipId);
} 
package com.travel_agent.models.entity.booking;

import com.travel_agent.models.entity.ship.ShipEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "booking_ship_info")
@Data
@EqualsAndHashCode(callSuper = true) 
public class BookingShipEntity extends BookingEntity {
    @ManyToOne
    @JoinColumn(name = "ship_id")
    private ShipEntity ship;
}

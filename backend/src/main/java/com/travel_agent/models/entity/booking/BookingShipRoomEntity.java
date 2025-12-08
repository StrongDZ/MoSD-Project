package com.travel_agent.models.entity.booking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.Id;
import com.travel_agent.models.id.booking.BookingShipRoomId;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "booking_ship_room")
@Data
@EqualsAndHashCode(callSuper = true) 
@IdClass(BookingShipRoomId.class)
public class BookingShipRoomEntity extends BookingRoomEntity {
    @Id
    @Column(name = "ship_id")
    private Integer shipId;
}


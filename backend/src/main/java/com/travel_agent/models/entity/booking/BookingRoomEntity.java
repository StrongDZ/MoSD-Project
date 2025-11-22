package com.travel_agent.models.entity.booking;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class BookingRoomEntity {
    @Id
    @Column(name = "booking_id")
    private Integer bookingId;

    @Id
    @Column(name = "room_id")
    private Integer roomId;

    @Column(name = "quantity")
    private Integer quantity;

}

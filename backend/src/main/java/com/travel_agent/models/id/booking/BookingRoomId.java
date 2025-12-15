package com.travel_agent.models.id.booking;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.MappedSuperclass;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class BookingRoomId implements Serializable{
    private Integer bookingId;
    private Integer roomId;
}

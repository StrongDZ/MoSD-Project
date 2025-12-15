package com.travel_agent.models.id.booking;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookingShipRoomId extends BookingRoomId {
    private Integer shipId;
}

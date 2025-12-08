package com.travel_agent.dto.booking;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingShipRequestDTO extends BookingRequestDTO {
    private Integer shipId;
}

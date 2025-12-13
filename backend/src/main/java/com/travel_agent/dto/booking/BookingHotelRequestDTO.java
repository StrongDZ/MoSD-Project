package com.travel_agent.dto.booking;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingHotelRequestDTO extends BookingRequestDTO {
    private Integer hotelId;
}

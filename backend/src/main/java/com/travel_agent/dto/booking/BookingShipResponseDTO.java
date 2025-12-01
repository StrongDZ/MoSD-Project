package com.travel_agent.dto.booking;

import java.util.List;

import com.travel_agent.dto.ship.ShipDTO;
import com.travel_agent.dto.ship.ShipRoomDTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingShipResponseDTO extends BookingResponseDTO {
    private ShipDTO ship;
    private List<ShipRoomBooking> rooms;

    @Data
    public static class ShipRoomBooking {
        private ShipRoomDTO room;
        private Integer quantity;
    }
}

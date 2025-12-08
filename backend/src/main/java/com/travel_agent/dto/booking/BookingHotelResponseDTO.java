package com.travel_agent.dto.booking;

import java.util.List;

import com.travel_agent.dto.hotel.HotelDTO;
import com.travel_agent.dto.hotel.HotelRoomDTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingHotelResponseDTO extends BookingResponseDTO {
    private HotelDTO hotel;
    private List<HotelRoomBooking> rooms;
    private String status;

    @Data
    public static class HotelRoomBooking {
        private HotelRoomDTO room;
        private Integer quantity;
    }
}

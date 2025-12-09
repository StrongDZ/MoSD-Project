package com.travel_agent.services;

import com.travel_agent.dto.hotel.HotelRoomDTO;

public interface HotelService {
    HotelRoomDTO updateRoom(Integer hotelId, Integer roomId, HotelRoomDTO hotelRoomDTO);
}

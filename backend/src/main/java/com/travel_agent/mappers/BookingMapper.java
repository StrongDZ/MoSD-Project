package com.travel_agent.mappers;

import com.travel_agent.dto.booking.BookingHotelResponseDTO;
import com.travel_agent.dto.booking.BookingShipResponseDTO;
import com.travel_agent.models.entity.booking.BookingHotelEntity;
import com.travel_agent.models.entity.booking.BookingShipEntity;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { HotelMapper.class, ShipMapper.class })
public interface BookingMapper {

    BookingHotelResponseDTO convertToHotelResponseDTO(BookingHotelEntity booking);
    BookingShipResponseDTO convertToShipResponseDTO(BookingShipEntity booking);
}

package com.travel_agent.mappers;

import com.travel_agent.dto.booking.BookingHotelResponseDTO;
import com.travel_agent.dto.booking.BookingShipResponseDTO;
import com.travel_agent.models.entity.booking.BookingHotelEntity;
import com.travel_agent.models.entity.booking.BookingShipEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    
    @Mapping(target = "rooms", ignore = true)
    BookingHotelResponseDTO convertToHotelResponseDTO(BookingHotelEntity booking);
    
    @Mapping(target = "rooms", ignore = true)
    BookingShipResponseDTO convertToShipResponseDTO(BookingShipEntity booking);
}

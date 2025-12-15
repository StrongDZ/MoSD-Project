package com.travel_agent.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.travel_agent.dto.hotel.HotelDTO;
import com.travel_agent.models.entity.hotel.HotelEntity;
import com.travel_agent.models.entity.hotel.HotelRoomEntity;
import com.travel_agent.dto.hotel.HotelRoomDTO;


@Mapper(componentModel = "spring")
public interface HotelMapper {
    @Mapping(source = "company.companyId", target = "companyId") // lấy ID từ Company
    HotelDTO hotelToHotelDTO(HotelEntity hotel);

    List<HotelDTO> hotelsToHotelDTOs(List<HotelEntity> hotels);

    HotelRoomDTO hotelRoomToHotelRoomDTO(HotelRoomEntity room);
}

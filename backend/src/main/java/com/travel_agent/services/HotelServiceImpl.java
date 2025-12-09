package com.travel_agent.services;

import com.travel_agent.dto.hotel.HotelRoomDTO;
import com.travel_agent.models.entity.hotel.HotelEntity;
import com.travel_agent.models.entity.hotel.HotelRoomEntity;
import com.travel_agent.repositories.hotel.HotelRepository;
import com.travel_agent.repositories.hotel.HotelRoomRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HotelServiceImpl implements HotelService{

    @Autowired
    private HotelRoomRepository hotelRoomRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private ModelMapper modelMapper;

    public HotelRoomDTO updateRoom(Integer hotelId, Integer roomId, HotelRoomDTO hotelRoomDTO) {
        HotelEntity hotel = hotelRepository.findById(hotelId).
                orElseThrow(() -> new RuntimeException("Hotel not found with id: " + hotelId));

        HotelRoomEntity hotelRoom = hotelRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));

        hotelRoom.setRoomName(hotelRoomDTO.getRoomName());
        hotelRoom.setRoomPrice(hotelRoomDTO.getRoomPrice());
        hotelRoom.setSize(hotelRoomDTO.getSize());
        hotelRoom.setView(hotelRoomDTO.getView());
        hotelRoom.setMaxPersons(hotelRoomDTO.getMaxPerson());
        hotelRoom.setBedType(hotelRoomDTO.getBedType());

        HotelRoomEntity savedRoom = hotelRoomRepository.save(hotelRoom);
        return modelMapper.map(savedRoom, HotelRoomDTO.class);
    }
}

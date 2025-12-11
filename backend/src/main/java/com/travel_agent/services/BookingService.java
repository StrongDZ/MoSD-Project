package com.travel_agent.services;

import com.travel_agent.dto.booking.BookingHotelResponseDTO;
import com.travel_agent.dto.booking.BookingShipResponseDTO;
import com.travel_agent.mappers.BookingMapper;
import com.travel_agent.models.entity.booking.BookingHotelEntity;
import com.travel_agent.models.entity.booking.BookingHotelRoomEntity;
import com.travel_agent.models.entity.booking.BookingShipEntity;
import com.travel_agent.models.entity.booking.BookingShipRoomEntity;
import com.travel_agent.repositories.booking.BookingHotelRepository;
import com.travel_agent.repositories.booking.BookingHotelRoomRepository;
import com.travel_agent.repositories.booking.BookingShipRepository;
import com.travel_agent.repositories.booking.BookingShipRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingHotelRepository bookingHotelRepository;
    private final BookingShipRepository bookingShipRepository;
    private final BookingHotelRoomRepository bookingHotelRoomRepository;
    private final BookingShipRoomRepository bookingShipRoomRepository;
    private final BookingMapper bookingMapper;

    public void updateBookingStatus(Integer bookingId, String status, String note) {
        BookingHotelEntity hotelBooking = bookingHotelRepository.findById(bookingId).orElse(null);
        BookingShipEntity shipBooking = bookingShipRepository.findById(bookingId).orElse(null);

        if (hotelBooking != null) {
            hotelBooking.setState(status);
            if (note != null) {
                hotelBooking.setSpecialRequest(note);
            }
            bookingHotelRepository.save(hotelBooking);
        } else if (shipBooking != null) {
            shipBooking.setState(status);
            if (note != null) {
                shipBooking.setSpecialRequest(note);
            }
            bookingShipRepository.save(shipBooking);
        } else {
            throw new IllegalArgumentException("Booking not found");
        }
    }

    public List<BookingHotelResponseDTO> getHotelBookingsByHotelId(Integer hotelId) {
        List<BookingHotelEntity> hotelBookings = bookingHotelRepository.findByHotelHotelId(hotelId);
        List<BookingHotelResponseDTO> bookingHotelResponseDTOs = new ArrayList<>();

        for (BookingHotelEntity booking : hotelBookings) {
            List<BookingHotelRoomEntity> bookingRooms = bookingHotelRoomRepository.findByBookingId(booking.getBookingId());
            List<BookingHotelResponseDTO.HotelRoomBooking> hotelRooms = new ArrayList<>();

            for (BookingHotelRoomEntity room : bookingRooms) {
                BookingHotelResponseDTO.HotelRoomBooking hotelRoom = new BookingHotelResponseDTO.HotelRoomBooking();
                // Note: Room details will be null until HotelService is implemented
                hotelRoom.setRoom(null);
                hotelRoom.setQuantity(room.getQuantity());
                hotelRooms.add(hotelRoom);
            }

            BookingHotelResponseDTO bookingHotelResponseDTO = bookingMapper.convertToHotelResponseDTO(booking);
            bookingHotelResponseDTO.setRooms(hotelRooms);
            bookingHotelResponseDTOs.add(bookingHotelResponseDTO);
        }
        return bookingHotelResponseDTOs;
    }

    public List<BookingShipResponseDTO> getShipBookingsByShipId(Integer shipId) {
        List<BookingShipEntity> shipBookings = bookingShipRepository.findByShipShipId(shipId);
        List<BookingShipResponseDTO> bookingShipResponseDTOs = new ArrayList<>();

        for (BookingShipEntity booking : shipBookings) {
            List<BookingShipRoomEntity> bookingRooms = bookingShipRoomRepository.findByBookingId(booking.getBookingId());
            List<BookingShipResponseDTO.ShipRoomBooking> shipRooms = new ArrayList<>();

            for (BookingShipRoomEntity room : bookingRooms) {
                BookingShipResponseDTO.ShipRoomBooking shipRoom = new BookingShipResponseDTO.ShipRoomBooking();
                // Note: Room details will be null until ShipService is implemented
                shipRoom.setRoom(null);
                shipRoom.setQuantity(room.getQuantity());
                shipRooms.add(shipRoom);
            }
        
            BookingShipResponseDTO bookingShipResponseDTO = bookingMapper.convertToShipResponseDTO(booking);
            bookingShipResponseDTO.setRooms(shipRooms);
            bookingShipResponseDTOs.add(bookingShipResponseDTO);
        }
        return bookingShipResponseDTOs;
    }
}
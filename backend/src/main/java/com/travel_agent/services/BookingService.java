package com.travel_agent.services;

import com.travel_agent.dto.booking.BookingHotelRequestDTO;
import com.travel_agent.dto.booking.BookingShipRequestDTO;
import com.travel_agent.dto.booking.BookingHotelResponseDTO;
import com.travel_agent.dto.booking.BookingRequestDTO.RoomBooking;
import com.travel_agent.dto.booking.BookingShipResponseDTO;
import com.travel_agent.dto.ship.ShipRoomDTO;
import com.travel_agent.models.entity.booking.BookingHotelEntity;
import com.travel_agent.models.entity.booking.BookingHotelRoomEntity;
import com.travel_agent.models.entity.booking.BookingShipEntity;
import com.travel_agent.models.entity.booking.BookingShipRoomEntity;
import com.travel_agent.models.entity.hotel.HotelEntity;
import com.travel_agent.models.entity.ship.ShipEntity;
import com.travel_agent.repositories.booking.BookingHotelRepository;
import com.travel_agent.repositories.booking.BookingShipRepository;
import com.travel_agent.repositories.booking.BookingHotelRoomRepository;
import com.travel_agent.repositories.booking.BookingShipRoomRepository;
import com.travel_agent.repositories.hotel.HotelRepository;
import com.travel_agent.repositories.ship.ShipRepository;
import com.travel_agent.repositories.ship.ShipRoomRepository;
import com.travel_agent.models.entity.ship.ShipRoomEntity;  
import com.travel_agent.models.entity.hotel.HotelRoomEntity;
import com.travel_agent.repositories.hotel.HotelRoomRepository;
import com.travel_agent.dto.hotel.HotelRoomDTO;
import com.travel_agent.mappers.BookingMapper;
import com.travel_agent.mappers.ShipMapper;
import com.travel_agent.mappers.HotelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingMapper bookingMapper;
    private final BookingHotelRepository bookingHotelRepository;
    private final BookingShipRepository bookingShipRepository;
    private final HotelRepository hotelRepository;
    private final ShipRepository shipRepository;
    private final BookingHotelRoomRepository bookingHotelRoomRepository;
    private final BookingShipRoomRepository bookingShipRoomRepository;
    private final ShipService shipService;
    private final HotelService hotelService;

    public BookingHotelResponseDTO createHotelBooking(BookingHotelRequestDTO request, Integer userId) {
        BookingHotelEntity booking = new BookingHotelEntity();
        HotelEntity hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found"));

        booking.setHotel(hotel);
        booking.setCustomerName(request.getCustomerName());
        booking.setPhone(request.getPhone());
        booking.setEmail(request.getEmail());
        booking.setUserId(userId);
        booking.setStartDate(request.getStartDate());
        booking.setEndDate(request.getEndDate());
        booking.setAdults(request.getAdults());
        booking.setChildren(request.getChildren());
        booking.setSpecialRequest(request.getSpecialRequest());
        booking.setState("PENDING");
        booking.setTotalAmount(request.getTotalAmount());

        booking = bookingHotelRepository.save(booking);

        BookingHotelRoomEntity bookingRoom = new BookingHotelRoomEntity();

        for (RoomBooking roomBooking : request.getRoomList()) {
            bookingRoom.setBookingId(booking.getBookingId());
            bookingRoom.setRoomId(roomBooking.getRoomId());
            bookingRoom.setQuantity(roomBooking.getQuantity());
            bookingRoom.setHotelId(request.getHotelId());

            bookingHotelRoomRepository.save(bookingRoom);
        }

        return bookingMapper.convertToHotelResponseDTO(booking);
    }

    public BookingShipResponseDTO createShipBooking(BookingShipRequestDTO request, Integer userId) {
        BookingShipEntity booking = new BookingShipEntity();
        ShipEntity ship = shipRepository.findById(request.getShipId())
                .orElseThrow(() -> new IllegalArgumentException("Ship not found"));
                
        booking.setShip(ship);
        booking.setCustomerName(request.getCustomerName());
        booking.setPhone(request.getPhone());
        booking.setEmail(request.getEmail());
        booking.setUserId(userId);
        booking.setStartDate(request.getStartDate());
        booking.setEndDate(request.getEndDate());
        booking.setAdults(request.getAdults());
        booking.setChildren(request.getChildren());
        booking.setSpecialRequest(request.getSpecialRequest());
        booking.setState("PENDING");
        booking.setTotalAmount(request.getTotalAmount());

        booking = bookingShipRepository.save(booking);

        BookingShipRoomEntity bookingRoom = new BookingShipRoomEntity();

        for (RoomBooking roomBooking : request.getRoomList()) {
            bookingRoom.setBookingId(booking.getBookingId());
            bookingRoom.setRoomId(roomBooking.getRoomId());
            bookingRoom.setQuantity(roomBooking.getQuantity());
            bookingRoom.setShipId(request.getShipId());

            bookingShipRoomRepository.save(bookingRoom);
        }
        
        return bookingMapper.convertToShipResponseDTO(booking);
    }

    public List<BookingShipResponseDTO> getUserShipBookings(Integer userId) {
        List<BookingShipEntity> shipBookings = bookingShipRepository.findByUserId(userId);
        List<BookingShipResponseDTO> bookingShipResponseDTOs = new ArrayList<>();

        for (BookingShipEntity booking : shipBookings) {
            List<BookingShipRoomEntity> bookingRooms = bookingShipRoomRepository.findByBookingId(booking.getBookingId());
            List<BookingShipResponseDTO.ShipRoomBooking> shipRooms = new ArrayList<>();
            Integer shipId = booking.getShip().getShipId();

            for (BookingShipRoomEntity room : bookingRooms) {
                BookingShipResponseDTO.ShipRoomBooking shipRoom = new BookingShipResponseDTO.ShipRoomBooking();
                ShipRoomDTO shipRoomDTO = shipService.getShipRoom(shipId, room.getRoomId());

                shipRoom.setRoom(shipRoomDTO);
                shipRoom.setQuantity(room.getQuantity());
                shipRooms.add(shipRoom);
            }
        
            BookingShipResponseDTO bookingShipResponseDTO = bookingMapper.convertToShipResponseDTO(booking);
            bookingShipResponseDTO.setRooms(shipRooms);
            bookingShipResponseDTOs.add(bookingShipResponseDTO);
        }
        return bookingShipResponseDTOs;
    }

    public List<BookingHotelResponseDTO> getUserHotelBookings(Integer userId) {
        List<BookingHotelEntity> hotelBookings = bookingHotelRepository.findByUserId(userId);
        List<BookingHotelResponseDTO> bookingHotelResponseDTOs = new ArrayList<>();

        for (BookingHotelEntity booking : hotelBookings) {
            List<BookingHotelRoomEntity> bookingRooms = bookingHotelRoomRepository.findByBookingId(booking.getBookingId());
            List<BookingHotelResponseDTO.HotelRoomBooking> hotelRooms = new ArrayList<>();
            Integer hotelId = booking.getHotel().getHotelId();
            
            for (BookingHotelRoomEntity room : bookingRooms) {
                BookingHotelResponseDTO.HotelRoomBooking hotelRoom = new BookingHotelResponseDTO.HotelRoomBooking();
                HotelRoomDTO hotelRoomDTO = hotelService.getHotelRoom(hotelId, room.getRoomId());
                hotelRoom.setRoom(hotelRoomDTO);
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
                ShipRoomDTO shipRoomDTO = shipService.getShipRoom(shipId, room.getRoomId());

                shipRoom.setRoom(shipRoomDTO);
                shipRoom.setQuantity(room.getQuantity());
                shipRooms.add(shipRoom);
            }
        
            BookingShipResponseDTO bookingShipResponseDTO = bookingMapper.convertToShipResponseDTO(booking);
            bookingShipResponseDTO.setRooms(shipRooms);
            bookingShipResponseDTOs.add(bookingShipResponseDTO);
        }
        return bookingShipResponseDTOs;
    }

    public List<BookingHotelResponseDTO> getHotelBookingsByHotelId(Integer hotelId) {
        List<BookingHotelEntity> hotelBookings = bookingHotelRepository.findByHotelHotelId(hotelId);
        List<BookingHotelResponseDTO> bookingHotelResponseDTOs = new ArrayList<>();

        for (BookingHotelEntity booking : hotelBookings) {
            List<BookingHotelRoomEntity> bookingRooms = bookingHotelRoomRepository.findByBookingId(booking.getBookingId());
            List<BookingHotelResponseDTO.HotelRoomBooking> hotelRooms = new ArrayList<>();

            for (BookingHotelRoomEntity room : bookingRooms) {
                BookingHotelResponseDTO.HotelRoomBooking hotelRoom = new BookingHotelResponseDTO.HotelRoomBooking();
                HotelRoomDTO hotelRoomDTO = hotelService.getHotelRoom(hotelId, room.getRoomId());
                hotelRoom.setRoom(hotelRoomDTO);
                hotelRoom.setQuantity(room.getQuantity());
                hotelRooms.add(hotelRoom);
            }

            BookingHotelResponseDTO bookingHotelResponseDTO = bookingMapper.convertToHotelResponseDTO(booking);
            bookingHotelResponseDTO.setRooms(hotelRooms);
            bookingHotelResponseDTOs.add(bookingHotelResponseDTO);
        }
        return bookingHotelResponseDTOs;
    }

    public void updateBookingStatus(Integer bookingId, String status, String note) {
        // Tìm đơn đặt chỗ trong cả 2 bảng
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
}
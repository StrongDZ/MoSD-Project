package com.travel_agent.services;

import com.travel_agent.models.entity.booking.BookingHotelEntity;
import com.travel_agent.models.entity.booking.BookingShipEntity;
import com.travel_agent.repositories.booking.BookingHotelRepository;
import com.travel_agent.repositories.booking.BookingShipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingHotelRepository bookingHotelRepository;
    private final BookingShipRepository bookingShipRepository;

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
}
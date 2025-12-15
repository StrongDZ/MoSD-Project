package com.travel_agent.repositories.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import com.travel_agent.models.entity.booking.BookingHotelRoomEntity;
import java.util.List;
import com.travel_agent.models.id.booking.BookingHotelRoomId;

public interface BookingHotelRoomRepository extends JpaRepository<BookingHotelRoomEntity, BookingHotelRoomId> {
    List<BookingHotelRoomEntity> findByBookingId(Integer bookingId);
}


package com.travel_agent.repositories.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import com.travel_agent.models.entity.booking.BookingShipRoomEntity;
import com.travel_agent.models.id.booking.BookingShipRoomId;
import java.util.List;

public interface BookingShipRoomRepository extends JpaRepository<BookingShipRoomEntity, BookingShipRoomId> {
    List<BookingShipRoomEntity> findByBookingId(Integer bookingId);
}
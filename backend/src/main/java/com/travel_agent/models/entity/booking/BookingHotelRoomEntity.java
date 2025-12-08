package com.travel_agent.models.entity.booking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.travel_agent.models.id.booking.BookingHotelRoomId;

@Entity
@Table(name = "booking_hotel_room")
@Data
@EqualsAndHashCode(callSuper = true) 
@IdClass(BookingHotelRoomId.class)
public class BookingHotelRoomEntity extends BookingRoomEntity {
    @Id
    @Column(name = "hotel_id")
    private Integer hotelId;
}

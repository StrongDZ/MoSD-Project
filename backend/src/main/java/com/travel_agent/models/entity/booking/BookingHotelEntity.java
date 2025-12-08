package com.travel_agent.models.entity.booking;

import com.travel_agent.models.entity.hotel.HotelEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "booking_hotel_info")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true) 
public class BookingHotelEntity extends BookingEntity {
    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private HotelEntity hotel;
} 
package com.travel_agent.models.entity.hotel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hotel_room")
public class HotelRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_room_id")
    private Integer hotelRoomId;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private HotelEntity hotel;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "room_price")
    private Integer roomPrice;

    private Integer size;

    @Column(name = "max_persons")
    private Integer maxPersons;

    @Column(name = "bed_type")
    private String bedType;

    private String view;
}

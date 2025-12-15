package com.travel_agent.models.entity.ship;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ship_room")
public class ShipRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ship_room_id")
    private Integer shipRoomId;

    @ManyToOne
    @JoinColumn(name = "ship_id")
    private ShipEntity ship;

    @Column(name = "room_name")
    private String roomName;

    private Integer size;

    @Column(name = "max_persons")
    private Integer maxPersons;

    @Column(name = "room_price")
    private Integer roomPrice;
}

package com.travel_agent.models.entity.hotel;

import com.travel_agent.models.id.hotel.HotelRoomFeatureId;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hotel_room_features")
@IdClass(HotelRoomFeatureId.class)
public class HotelRoomFeatureEntity {
    @Id
    @Column(name = "hotel_room_id")
    private Integer hotelRoomId;

    @Id
    @Column(name = "feature_id")
    private Integer roomFeaturesId;
}

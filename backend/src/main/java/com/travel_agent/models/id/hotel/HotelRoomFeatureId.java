package com.travel_agent.models.id.hotel;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelRoomFeatureId implements Serializable {
    private Integer hotelRoomId;
    private Integer roomFeaturesId;
}
package com.travel_agent.models.entity.hotel;

import com.travel_agent.models.id.hotel.HotelFeatureId;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hotel_features")
@IdClass(HotelFeatureId.class)
public class HotelFeatureEntity {

}

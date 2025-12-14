package com.travel_agent.models.entity.hotel;

import com.travel_agent.models.id.hotel.HotelDescriptionId;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hotel_short_description")
@IdClass(HotelDescriptionId.class)
public class HotelShortDescriptionEntity {
    @Id
    @Column(name = "hotel_id")
    private Integer hotelId;
    
    @Id
    @Column(name = "block_id")
    private Integer blockId;

    private String description;
}

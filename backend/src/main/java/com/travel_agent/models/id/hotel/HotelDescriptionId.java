package com.travel_agent.models.id.hotel;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelDescriptionId implements Serializable {
    private Integer hotelId;
    private Integer blockId;
}

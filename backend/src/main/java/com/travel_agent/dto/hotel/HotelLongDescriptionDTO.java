package com.travel_agent.dto.hotel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelLongDescriptionDTO {
    private Integer blockId;
    private String type;
    private String data;
}

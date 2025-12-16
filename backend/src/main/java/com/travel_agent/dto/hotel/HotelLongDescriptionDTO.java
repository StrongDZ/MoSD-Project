package com.travel_agent.dto.hotel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HotelLongDescriptionDTO {
    private Integer blockId;
    private String type;
    private String data;
}
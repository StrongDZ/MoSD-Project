package com.travel_agent.dto.ship;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShipLongDescriptionDTO {
    private Integer blockId;
    private String type;
    private String data;
}
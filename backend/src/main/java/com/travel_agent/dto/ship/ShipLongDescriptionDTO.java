package com.travel_agent.dto.ship;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipLongDescriptionDTO {
    private Integer blockId;
    private String type;
    private String data;
}

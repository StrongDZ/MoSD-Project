package com.travel_agent.models.id.ship;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipDescriptionId implements Serializable {
    private Integer shipId;
    private Integer blockId;
}

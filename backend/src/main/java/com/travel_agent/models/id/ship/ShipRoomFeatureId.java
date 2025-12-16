package com.travel_agent.models.id.ship;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipRoomFeatureId implements Serializable {
    private Integer shipRoomId;
    private Integer roomFeaturesId;
}

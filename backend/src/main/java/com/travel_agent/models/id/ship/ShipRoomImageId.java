package com.travel_agent.models.id.ship;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class ShipRoomImageId implements Serializable {
    private Integer roomId;
    private String imgUrl;
}
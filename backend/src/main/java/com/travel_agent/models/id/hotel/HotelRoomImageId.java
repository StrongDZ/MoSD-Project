package com.travel_agent.models.id.hotel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class HotelRoomImageId implements Serializable {
    private Integer roomId;
    private String imgUrl;
}
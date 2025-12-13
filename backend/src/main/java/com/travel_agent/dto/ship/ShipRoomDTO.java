package com.travel_agent.dto.ship;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShipRoomDTO {
    private Integer roomId;
    private String roomName;
    private Integer roomPrice;
    private List<Integer> roomFeatureIds;
    private List<String> roomFeatures;
    private Integer size;
    private Integer maxPersons;
    private List<String> images;
}
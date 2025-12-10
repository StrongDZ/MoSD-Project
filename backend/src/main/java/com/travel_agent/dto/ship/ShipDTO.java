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

public class ShipDTO {
    private Integer shipId;
    private String shipName;
    private Integer launch;
    private Integer cabin;
    private String shell;
    private String trip;
    private String companyName;
    private Integer shipPrice;
    private String address;
    private String mapLink;
    private String thumbnail;
    private Integer companyId;

    private List<Integer> featureIds;
    private List<String> features;

    private List<String> shortDescriptions;
    private List<ShipLongDescriptionDTO> longDescriptions;
    private List<ShipRoomDTO> rooms;
    private List<String> images;
}

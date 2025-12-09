package com.travel_agent.dto.hotel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HotelRoomDTO {
    private Integer roomId;
    private String roomName;
    private Integer roomPrice;
    private Integer size;
    private Integer maxPerson;
    private String bedType;
    private String view;
    private List<String> images;
}

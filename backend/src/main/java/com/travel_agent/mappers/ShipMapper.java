package com.travel_agent.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.travel_agent.dto.ship.ShipDTO;
import com.travel_agent.models.entity.ship.ShipEntity;
import com.travel_agent.models.entity.ship.ShipRoomEntity;
import com.travel_agent.dto.ship.ShipRoomDTO;


@Mapper(componentModel = "spring")
public interface ShipMapper {

    @Mapping(source = "company.companyId", target = "companyId")
    ShipDTO shipToShipDTO(ShipEntity ship);

    List<ShipDTO> shipsToShipDTOs(List<ShipEntity> ships);

    ShipRoomDTO shipRoomToShipRoomDTO(ShipRoomEntity room);
}

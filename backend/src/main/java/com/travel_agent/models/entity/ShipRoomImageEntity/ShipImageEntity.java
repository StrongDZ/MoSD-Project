package com.travel_agent.models.entity.ship;

import com.travel_agent.models.id.ship.ShipImageId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ship_img")
@IdClass(ShipImageId.class)
public class ShipImageEntity {
    @Id
    private Integer shipId;

    @Id
    private String imgUrl;
}
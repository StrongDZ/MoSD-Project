package com.travel_agent.models.entity.ship;

import com.travel_agent.models.id.ship.ShipDescriptionId;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ship_short_description")
@IdClass(ShipDescriptionId.class)
public class ShipShortDescriptionEntity {
    @Id
    @Column(name = "ship_id")
    private Integer shipId;

    @Id
    @Column(name = "block_id")
    private Integer blockId;

    private String description;
}

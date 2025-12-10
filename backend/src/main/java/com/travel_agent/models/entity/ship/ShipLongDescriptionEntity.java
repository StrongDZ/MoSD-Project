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
@Table(name = "ship_long_description")
@IdClass(ShipDescriptionId.class)
public class ShipLongDescriptionEntity {
    @Id
    @Column(name = "ship_id")
    private Integer shipId;

    @Id
    @Column(name = "block_id")
    private Integer blockId;

    private String type;
    private String data;
}


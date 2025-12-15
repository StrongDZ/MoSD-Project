package com.travel_agent.models.entity.ship;

import com.travel_agent.models.id.ship.ShipFeatureId;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ship_features")
@IdClass(ShipFeatureId.class)
public class ShipFeatureEntity {
    @Id
    @Column(name = "ship_id")
    private Integer shipId;

    @Id
    @Column(name = "feature_id")
    private Integer featureId;
}

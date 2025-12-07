package com.travel_agent.models.entity.ship;

import com.travel_agent.models.id.ship.ShipRoomFeatureId;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ship_room_features")
@IdClass(ShipRoomFeatureId.class)
public class ShipRoomFeatureEntity {
    @Id
    @Column(name = "ship_room_id")
    private Integer shipRoomId;

    @Id
    @Column(name = "feature_id")
    private Integer roomFeaturesId;
}
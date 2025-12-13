package com.travel_agent.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "features")
public class FeatureEntity {
    @Id
    @Column(name = "feature_id")
    private Integer featureId;

    @Column(name = "feature_description")
    private String featureDescription;
}

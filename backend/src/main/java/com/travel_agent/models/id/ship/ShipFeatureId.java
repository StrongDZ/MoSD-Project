package com.travel_agent.models.id.ship;

import java.io.Serializable;
import java.util.Objects;

public class ShipFeatureId implements Serializable {
    private Long shipId;
    private String featureName;

    public ShipFeatureId() {}

    public ShipFeatureId(Long shipId, String featureName) {
        this.shipId = shipId;
        this.featureName = featureName;
    }

    public Long getShipId() {
        return shipId;
    }

    public void setShipId(Long shipId) {
        this.shipId = shipId;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShipFeatureId that = (ShipFeatureId) o;
        return Objects.equals(shipId, that.shipId) && Objects.equals(featureName, that.featureName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shipId, featureName);
    }
}

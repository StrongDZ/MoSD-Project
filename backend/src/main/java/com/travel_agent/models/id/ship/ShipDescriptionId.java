package com.travel_agent.models.id.ship;

import java.io.Serializable;
import java.util.Objects;

public class ShipDescriptionId implements Serializable {
    private Long shipId;
    private String language;

    public ShipDescriptionId() {}

    public ShipDescriptionId(Long shipId, String language) {
        this.shipId = shipId;
        this.language = language;
    }

    public Long getShipId() {
        return shipId;
    }

    public void setShipId(Long shipId) {
        this.shipId = shipId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShipDescriptionId that = (ShipDescriptionId) o;
        return Objects.equals(shipId, that.shipId) && Objects.equals(language, that.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shipId, language);
    }
}

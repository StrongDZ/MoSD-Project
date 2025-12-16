package com.travel_agent.models.id.ship;

import java.io.Serializable;
import java.util.Objects;

public class ShipImageId implements Serializable {
    private Long shipId;
    private String imageUrl;

    public ShipImageId() {}

    public ShipImageId(Long shipId, String imageUrl) {
        this.shipId = shipId;
        this.imageUrl = imageUrl;
    }

    public Long getShipId() {
        return shipId;
    }

    public void setShipId(Long shipId) {
        this.shipId = shipId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShipImageId that = (ShipImageId) o;
        return Objects.equals(shipId, that.shipId) && Objects.equals(imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shipId, imageUrl);
    }
}

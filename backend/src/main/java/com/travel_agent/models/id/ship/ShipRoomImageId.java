package com.travel_agent.models.id.ship;

import java.io.Serializable;
import java.util.Objects;

public class ShipRoomImageId implements Serializable {
    private Long roomId;
    private String imageUrl;

    public ShipRoomImageId() {}

    public ShipRoomImageId(Long roomId, String imageUrl) {
        this.roomId = roomId;
        this.imageUrl = imageUrl;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
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
        ShipRoomImageId that = (ShipRoomImageId) o;
        return Objects.equals(roomId, that.roomId) && Objects.equals(imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId, imageUrl);
    }
}

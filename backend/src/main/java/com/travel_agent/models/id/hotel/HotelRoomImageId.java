package com.travel_agent.models.id.hotel;

import java.io.Serializable;
import java.util.Objects;

public class HotelRoomImageId implements Serializable {
    private Long roomId;
    private String imageUrl;

    public HotelRoomImageId() {}

    public HotelRoomImageId(Long roomId, String imageUrl) {
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
        HotelRoomImageId that = (HotelRoomImageId) o;
        return Objects.equals(roomId, that.roomId) && Objects.equals(imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId, imageUrl);
    }
}

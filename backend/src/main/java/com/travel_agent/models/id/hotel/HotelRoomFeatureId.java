package com.travel_agent.models.id.hotel;

import java.io.Serializable;
import java.util.Objects;

public class HotelRoomFeatureId implements Serializable {
    private Long roomId;
    private String featureName;

    public HotelRoomFeatureId() {}

    public HotelRoomFeatureId(Long roomId, String featureName) {
        this.roomId = roomId;
        this.featureName = featureName;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
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
        HotelRoomFeatureId that = (HotelRoomFeatureId) o;
        return Objects.equals(roomId, that.roomId) && Objects.equals(featureName, that.featureName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId, featureName);
    }
}

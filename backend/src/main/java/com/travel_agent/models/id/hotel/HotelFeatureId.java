package com.travel_agent.models.id.hotel;

import java.io.Serializable;
import java.util.Objects;

public class HotelFeatureId implements Serializable {
    private Long hotelId;
    private String featureName;

    public HotelFeatureId() {}

    public HotelFeatureId(Long hotelId, String featureName) {
        this.hotelId = hotelId;
        this.featureName = featureName;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
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
        HotelFeatureId that = (HotelFeatureId) o;
        return Objects.equals(hotelId, that.hotelId) && Objects.equals(featureName, that.featureName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hotelId, featureName);
    }
}

package com.travel_agent.models.id.hotel;

import java.io.Serializable;
import java.util.Objects;

public class HotelImageId implements Serializable {
    private Long hotelId;
    private String imageUrl;

    public HotelImageId() {}

    public HotelImageId(Long hotelId, String imageUrl) {
        this.hotelId = hotelId;
        this.imageUrl = imageUrl;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
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
        HotelImageId that = (HotelImageId) o;
        return Objects.equals(hotelId, that.hotelId) && Objects.equals(imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hotelId, imageUrl);
    }
}

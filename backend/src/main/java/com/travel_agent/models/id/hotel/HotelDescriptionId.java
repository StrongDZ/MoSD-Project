package com.travel_agent.models.id.hotel;

import java.io.Serializable;
import java.util.Objects;

public class HotelDescriptionId implements Serializable {
    private Long hotelId;
    private String language;

    public HotelDescriptionId() {}

    public HotelDescriptionId(Long hotelId, String language) {
        this.hotelId = hotelId;
        this.language = language;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
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
        HotelDescriptionId that = (HotelDescriptionId) o;
        return Objects.equals(hotelId, that.hotelId) && Objects.equals(language, that.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hotelId, language);
    }
}

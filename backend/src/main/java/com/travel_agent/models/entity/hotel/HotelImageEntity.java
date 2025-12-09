package com.travel_agent.models.entity.hotel;

import com.travel_agent.models.id.hotel.HotelImageId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "hotel_img")
@IdClass(HotelImageId.class)
public class HotelImageEntity {
    @Id
    private Integer hotelId;

    @Id
    private String imgUrl;
}
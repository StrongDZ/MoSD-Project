package com.travel_agent.models.entity.hotel;

import com.travel_agent.models.id.hotel.HotelRoomImageId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "hotel_room_img")
@IdClass(HotelRoomImageId.class)
public class HotelRoomImageEntity {
    @Id
    private Integer roomId;

    @Id
    private String imgUrl;
}
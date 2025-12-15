package com.travel_agent.models.entity.hotel;

import com.travel_agent.models.entity.CompanyEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "hotel")
public class HotelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_id")
    private Integer hotelId;

    @Column(name = "hotel_name")
    private String hotelName;

    @Column(name = "total_rooms")
    private Integer totalRooms;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "hotel_price")
    private Integer hotelPrice;

    private String city;
    private String address;

    @Column(name = "map_link")
    private String mapLink;

    private String thumbnail;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private CompanyEntity company;
}


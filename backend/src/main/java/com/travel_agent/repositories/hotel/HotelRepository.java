package com.travel_agent.repositories.hotel;

import com.travel_agent.models.entity.hotel.HotelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.stream.Collectors;

public interface HotelRepository extends JpaRepository<HotelEntity, Integer> {
    @Query("SELECT h FROM HotelEntity h WHERE " +
            "((:name IS NULL OR :name = '' OR (h.hotelName IS NOT NULL AND LOWER(h.hotelName) LIKE LOWER(CONCAT('%', :name, '%'))))) AND " +
            "((:minPrice IS NULL OR h.hotelPrice >= :minPrice)) AND " +
            "((:maxPrice IS NULL OR h.hotelPrice <= :maxPrice)) AND " +
            "((:city IS NULL OR :city = '' OR (h.city IS NOT NULL AND LOWER(h.city) LIKE LOWER(CONCAT('%', :city, '%')))))")
    Page<HotelEntity> findByHotelNamePriceAndCity(
            @Param("name") String name,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("city") String city,
            Pageable pageable);

    List<HotelEntity> findByHotelNameContainingIgnoreCase(String keyword);
}
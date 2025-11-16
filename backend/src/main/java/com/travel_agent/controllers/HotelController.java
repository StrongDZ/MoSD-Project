package com.travel_agent.controllers;

import com.travel_agent.dto.ResultPaginationDTO;
import com.travel_agent.services.HotelService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotel")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    // Search hotel by name, price and city
    @GetMapping("/search")
    public ResultPaginationDTO searchHotels(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "minPrice", required = false) Integer minPrice,
            @RequestParam(value = "maxPrice", required = false) Integer maxPrice,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
            @RequestParam(value = "pageSize", defaultValue = "6") int pageSize) {
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        return hotelService.searchHotelsByNamePriceAndCity(name, minPrice, maxPrice, city, pageable);
    }
}


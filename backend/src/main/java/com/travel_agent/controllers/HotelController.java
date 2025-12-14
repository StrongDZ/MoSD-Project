package com.travel_agent.controllers;

import com.travel_agent.services.HotelService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotel")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    // Search hotel by name
    @GetMapping("/search")
    public String searchHotels(@RequestParam(value = "name", required = false) String name) {
        return "Search functionality coming soon";
    }
}


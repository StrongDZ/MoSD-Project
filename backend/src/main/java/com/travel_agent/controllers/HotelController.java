package com.travel_agent.controllers;

import com.travel_agent.dto.ResponseObject;
import com.travel_agent.dto.hotel.HotelRoomDTO;
import com.travel_agent.services.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotel")
public class HotelController {

    private final HotelService hotelService;

    @Autowired
    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    // Update room in hotel
    @PutMapping("/{hotelId}/{roomId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ResponseObject> updateHotelRoom(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomId") Integer roomId,
            @RequestBody HotelRoomDTO roomDto) {
        HotelRoomDTO updatedRoom = hotelService.updateHotelRoom(hotelId, roomId, roomDto);

        if (updatedRoom != null) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Room updated successfully")
                    .data(updatedRoom)
                    .responseCode(HttpStatus.OK.value())
                    .build());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

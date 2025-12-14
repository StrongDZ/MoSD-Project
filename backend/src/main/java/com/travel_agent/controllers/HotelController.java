package com.travel_agent.controllers;

import com.travel_agent.dto.hotel.HotelDTO;
import com.travel_agent.dto.hotel.HotelRoomDTO;
import com.travel_agent.dto.ResponseObject;
import com.travel_agent.dto.ResultPaginationDTO;
import com.travel_agent.services.HotelService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/hotel")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public ResultPaginationDTO getAllHotels(
            @RequestParam("currentPage") Optional<Integer> currentPageOptional,
            @RequestParam("pageSize") Optional<Integer> pageSizeOptional
    ) {
        int currentPage = currentPageOptional.orElse(1);
        int pageSize = pageSizeOptional.orElse(10);

        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

        return hotelService.getAllHotels(pageable);
    }

    // Search hotel
    @GetMapping("/search")
    public ResponseEntity<ResponseObject> searchHotelsByNamePriceAndCity(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "minPrice", required = false) Integer minPrice,
            @RequestParam(value = "maxPrice", required = false) Integer maxPrice,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "features", required = false) String features,
            @RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
            @RequestParam(value = "pageSize", defaultValue = "6") int pageSize) {
        
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        ResultPaginationDTO result = hotelService.searchHotelsByNamePriceAndCity(name, minPrice, maxPrice, city, features, pageable);

        return ResponseEntity.ok(ResponseObject.builder()
                .message("Hotels retrieved successfully")
                .data(result)
                .responseCode(HttpStatus.OK.value())
                .build());
    }

    // View hotel details
    @GetMapping("/{hotelId}")
    public ResponseEntity<ResponseObject> getHotelDetails(@PathVariable("hotelId") Integer hotelId) {
        HotelDTO hotelDto = hotelService.getHotelDetails(hotelId);

        if (hotelDto != null) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Hotel details retrieved successfully")
                    .data(hotelDto)
                    .responseCode(HttpStatus.OK.value())
                    .build());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // View all rooms
    @GetMapping("/{hotelId}/rooms")
    public ResponseEntity<ResponseObject> getAllRoomsByHotelId(@PathVariable("hotelId") Integer hotelId) {
        List<HotelRoomDTO> rooms = hotelService.getAllRoomsByHotelId(hotelId);

        if (rooms != null && !rooms.isEmpty()) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Rooms retrieved successfully")
                    .data(rooms)
                    .responseCode(HttpStatus.OK.value())
                    .build());
        } else {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("No rooms found for this hotel")
                    .data(rooms)
                    .responseCode(HttpStatus.OK.value())
                    .build());
        }
    }

    // Add hotel
    @PostMapping("/add")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ResponseObject> addHotel(@RequestBody HotelDTO hotelDto) {
        HotelDTO addedHotel = hotelService.addHotel(hotelDto);

        if (addedHotel != null) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Hotel added successfully")
                    .data(addedHotel)
                    .responseCode(HttpStatus.OK.value())
                    .build());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update hotel
    @PutMapping("/{hotelId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ResponseObject> updateHotel(@PathVariable("hotelId") Integer hotelId, @RequestBody HotelDTO hotelDto) {
        HotelDTO updatedHotel = hotelService.updateHotel(hotelId, hotelDto);

        if (updatedHotel != null) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Hotel updated successfully")
                    .data(updatedHotel)
                    .responseCode(HttpStatus.OK.value())
                    .build());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete hotel
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ResponseObject> deleteHotels(@RequestBody List<Integer> hotelIds) {
        if (hotelIds.size() > 10) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Cannot delete more than 10 hotels at once")
                    .responseCode(HttpStatus.BAD_REQUEST.value())
                    .build());
        }
        hotelService.deleteHotels(hotelIds);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Hotels deleted successfully")
                .responseCode(HttpStatus.OK.value())
                .build());
    }

    // View room
    @GetMapping("/{hotelId}/{roomId}")
    public ResponseEntity<ResponseObject> getHotelRoom(
            @PathVariable("hotelId") Integer hotelId,
            @PathVariable("roomId") Integer roomId) {
        HotelRoomDTO roomDto = hotelService.getHotelRoom(hotelId, roomId);

        if (roomDto != null) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Room details retrieved successfully")
                    .data(roomDto)
                    .responseCode(HttpStatus.OK.value())
                    .build());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Add room to hotel
    @PostMapping("/{hotelId}/add-room")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ResponseObject> addHotelRoom(@PathVariable("hotelId") Integer hotelId, @RequestBody HotelRoomDTO roomDto) {
        HotelRoomDTO addedRoom = hotelService.addHotelRoom(hotelId, roomDto);

        if (addedRoom != null) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Room added successfully")
                    .data(addedRoom)
                    .responseCode(HttpStatus.OK.value())
                    .build());
        } else {
            return ResponseEntity.badRequest().build();
        }
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

    // Delete room from hotel
    @DeleteMapping("/{hotelId}/delete-room")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ResponseObject> deleteHotelRooms(
            @PathVariable("hotelId") Integer hotelId,
            @RequestBody List<Integer> roomIds) {
        if (roomIds.size() > 10) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Cannot delete more than 10 rooms at once")
                    .responseCode(HttpStatus.BAD_REQUEST.value())
                    .build());
        }
        hotelService.deleteHotelRooms(hotelId, roomIds);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Rooms deleted successfully")
                .responseCode(HttpStatus.OK.value())
                .build());
    }

    @GetMapping("/cities")
    public ResponseEntity<ResponseObject> getAllCities() {
        List<String> cities = hotelService.getAllCities();
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Cities retrieved successfully")
                .data(cities)
                .responseCode(HttpStatus.OK.value())
                .build());
    }

    // HotelController.java
    @GetMapping("/suggest")
    public ResponseEntity<ResponseObject> suggestHotelNames(@RequestParam("q") String keyword) {
        List<String> names = hotelService.suggestHotelNames(keyword);
        return ResponseEntity.ok(ResponseObject.builder()
            .message("Hotel names suggestion")
            .data(names)
            .responseCode(HttpStatus.OK.value())
            .build());
    }
}

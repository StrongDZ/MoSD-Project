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

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hotel")
public class HotelController {

    private static final int MAX_BATCH_DELETE_SIZE = 10;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_SEARCH_PAGE_SIZE = 6;

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    private ResponseEntity<ResponseObject> successResponse(String message, Object data) {
        return ResponseEntity.ok(ResponseObject.builder()
                .message(message)
                .data(data)
                .responseCode(HttpStatus.OK.value())
                .build());
    }

    private ResponseEntity<ResponseObject> errorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(ResponseObject.builder()
                .message(message)
                .responseCode(status.value())
                .build());
    }

    @GetMapping
    public ResultPaginationDTO getAllHotels(
            @RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
    ) {
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        return hotelService.getAllHotels(pageable);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseObject> searchHotelsByNamePriceAndCity(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String features,
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "6") int pageSize) {
        
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        ResultPaginationDTO result = hotelService.searchHotelsByNamePriceAndCity(
                name, minPrice, maxPrice, city, features, pageable);
        return successResponse("Hotels retrieved successfully", result);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<ResponseObject> getHotelDetails(@PathVariable Integer hotelId) {
        HotelDTO hotelDto = hotelService.getHotelDetails(hotelId);
        return hotelDto != null 
                ? successResponse("Hotel details retrieved successfully", hotelDto)
                : errorResponse("Hotel not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{hotelId}/rooms")
    public ResponseEntity<ResponseObject> getAllRoomsByHotelId(@PathVariable Integer hotelId) {
        List<HotelRoomDTO> rooms = hotelService.getAllRoomsByHotelId(hotelId);
        String message = (rooms != null && !rooms.isEmpty()) 
                ? "Rooms retrieved successfully" 
                : "No rooms found for this hotel";
        return successResponse(message, rooms);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ResponseObject> addHotel(@Valid @RequestBody HotelDTO hotelDto) {
        HotelDTO addedHotel = hotelService.addHotel(hotelDto);
        return addedHotel != null 
                ? successResponse("Hotel added successfully", addedHotel)
                : errorResponse("Failed to add hotel", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{hotelId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ResponseObject> updateHotel(
            @PathVariable Integer hotelId, 
            @Valid @RequestBody HotelDTO hotelDto) {
        HotelDTO updatedHotel = hotelService.updateHotel(hotelId, hotelDto);
        return updatedHotel != null 
                ? successResponse("Hotel updated successfully", updatedHotel)
                : errorResponse("Hotel not found", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ResponseObject> deleteHotels(@RequestBody List<Integer> hotelIds) {
        if (hotelIds.size() > MAX_BATCH_DELETE_SIZE) {
            return errorResponse(
                    "Cannot delete more than " + MAX_BATCH_DELETE_SIZE + " hotels at once",
                    HttpStatus.BAD_REQUEST);
        }
        hotelService.deleteHotels(hotelIds);
        return successResponse("Hotels deleted successfully", null);
    }

    @GetMapping("/{hotelId}/{roomId}")
    public ResponseEntity<ResponseObject> getHotelRoom(
            @PathVariable Integer hotelId,
            @PathVariable Integer roomId) {
        HotelRoomDTO roomDto = hotelService.getHotelRoom(hotelId, roomId);
        return roomDto != null 
                ? successResponse("Room details retrieved successfully", roomDto)
                : errorResponse("Room not found", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{hotelId}/add-room")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ResponseObject> addHotelRoom(
            @PathVariable Integer hotelId, 
            @Valid @RequestBody HotelRoomDTO roomDto) {
        HotelRoomDTO addedRoom = hotelService.addHotelRoom(hotelId, roomDto);
        return addedRoom != null 
                ? successResponse("Room added successfully", addedRoom)
                : errorResponse("Failed to add room", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{hotelId}/{roomId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ResponseObject> updateHotelRoom(
            @PathVariable Integer hotelId,
            @PathVariable Integer roomId,
            @Valid @RequestBody HotelRoomDTO roomDto) {
        HotelRoomDTO updatedRoom = hotelService.updateHotelRoom(hotelId, roomId, roomDto);
        return updatedRoom != null 
                ? successResponse("Room updated successfully", updatedRoom)
                : errorResponse("Room not found", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{hotelId}/delete-room")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ResponseObject> deleteHotelRooms(
            @PathVariable Integer hotelId,
            @RequestBody List<Integer> roomIds) {
        if (roomIds.size() > MAX_BATCH_DELETE_SIZE) {
            return errorResponse(
                    "Cannot delete more than " + MAX_BATCH_DELETE_SIZE + " rooms at once",
                    HttpStatus.BAD_REQUEST);
        }
        hotelService.deleteHotelRooms(hotelId, roomIds);
        return successResponse("Rooms deleted successfully", null);
    }

    @GetMapping("/cities")
    public ResponseEntity<ResponseObject> getAllCities() {
        return successResponse("Cities retrieved successfully", hotelService.getAllCities());
    }

    @GetMapping("/suggest")
    public ResponseEntity<ResponseObject> suggestHotelNames(@RequestParam("q") String keyword) {
        return successResponse("Hotel names suggestion", hotelService.suggestHotelNames(keyword));
    }
}

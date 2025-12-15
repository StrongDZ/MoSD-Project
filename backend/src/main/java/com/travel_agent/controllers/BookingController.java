package com.travel_agent.controllers;

import com.travel_agent.annotation.CurrentUserId;
import com.travel_agent.dto.ResponseObject;
import com.travel_agent.dto.booking.BookingRequestDTO;
import com.travel_agent.dto.booking.BookingResponseDTO;
import com.travel_agent.dto.booking.BookingHotelRequestDTO;
import com.travel_agent.dto.booking.BookingShipRequestDTO;
import com.travel_agent.dto.booking.BookingHotelResponseDTO;
import com.travel_agent.dto.booking.BookingShipResponseDTO;
import com.travel_agent.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseObject> createBooking(@CurrentUserId Integer userId,
            @RequestBody BookingRequestDTO request) {
        try {
            BookingResponseDTO booking = null;
            if (request instanceof BookingHotelRequestDTO hotelRequest) {   
                // handle hotel booking
                booking = bookingService.createHotelBooking(hotelRequest, userId);
            } else if (request instanceof BookingShipRequestDTO shipRequest) {
                // handle ship booking
                 booking = bookingService.createShipBooking(shipRequest, userId);
            } else {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .message("Invalid booking type")
                        .responseCode(HttpStatus.BAD_REQUEST.value())
                        .build());
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseObject.builder()
                            .message("Booking created successfully")
                            .data(booking)  
                            .responseCode(HttpStatus.CREATED.value())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .message("Failed to create booking: " + e.getMessage())
                            .responseCode(HttpStatus.BAD_REQUEST.value())
                            .build());
        }
    }

    @GetMapping("/my-bookings/ship")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseObject> getMyShipBookings(
            @CurrentUserId Integer userId) {
        try {
            List<BookingShipResponseDTO> bookings = bookingService.getUserShipBookings(userId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Bookings retrieved successfully")
                    .data(bookings)
                    .responseCode(HttpStatus.OK.value())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .message("Failed to retrieve bookings: " + e.getMessage())
                            .responseCode(HttpStatus.BAD_REQUEST.value())
                            .build());
        }
    }

    @GetMapping("/my-bookings/hotel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseObject> getMyHotelBookings(
            @CurrentUserId Integer userId) {
        try {
            List<BookingHotelResponseDTO> bookings = bookingService.getUserHotelBookings(userId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Bookings retrieved successfully")
                    .data(bookings)
                    .responseCode(HttpStatus.OK.value())
                    .build());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .message("Failed to retrieve bookings: " + e.getMessage())
                            .responseCode(HttpStatus.BAD_REQUEST.value())
                            .build());
        }
    }


    @GetMapping("/company")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ResponseObject> getCompanyBookings(
            @CurrentUserId Integer companyId) {

        System.out.println("Current company ID: " + companyId);
        String type = companyId <= 217 ? "HOTEL" : "SHIP";
        System.out.println("Company type: " + type);
        if (type == "HOTEL") {
            try {
                System.out.println("Calling getHotelBookingsByHotelId with companyId: " + companyId);
                List<BookingHotelResponseDTO> bookings = bookingService.getHotelBookingsByHotelId(companyId);
                System.out.println("Number of bookings found: " + (bookings != null ? bookings.size() : 0));
                return ResponseEntity.ok(ResponseObject.builder()
                        .message("Hotel bookings retrieved successfully")
                        .data(bookings)
                        .responseCode(HttpStatus.OK.value())
                        .build());
            } catch (Exception e) {
                System.err.println("Error getting hotel bookings: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseObject.builder()
                                .message("Failed to retrieve hotel bookings: " + e.getMessage())
                                .responseCode(HttpStatus.BAD_REQUEST.value())
                                .build());
            }
        } else {
            List<BookingShipResponseDTO> bookings = bookingService.getShipBookingsByShipId(companyId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Ship bookings retrieved successfully")
                    .data(bookings)
                    .responseCode(HttpStatus.OK.value())
                    .build());
        }
        
    }

    @GetMapping("/company/ship")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ResponseObject> getCompanyShipBookings(
            @CurrentUserId Integer companyId) {
        try {
            List<BookingShipResponseDTO> bookings = bookingService.getShipBookingsByShipId(companyId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Ship bookings retrieved successfully")
                    .data(bookings)
                    .responseCode(HttpStatus.OK.value())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .message("Failed to retrieve ship bookings: " + e.getMessage())
                            .responseCode(HttpStatus.BAD_REQUEST.value())
                            .build());
        }
    }

    @GetMapping("/hotel/{hotelId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ResponseObject> getHotelBookingsByHotelId(
            @PathVariable Integer hotelId) {
        try {
            List<BookingHotelResponseDTO> bookings = bookingService.getHotelBookingsByHotelId(hotelId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Hotel bookings retrieved successfully")
                    .data(bookings)
                    .responseCode(HttpStatus.OK.value())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .message("Failed to retrieve hotel bookings: " + e.getMessage())
                            .responseCode(HttpStatus.BAD_REQUEST.value())
                            .build());
        }
    }

    @PutMapping("/{bookingId}/status")
    public ResponseEntity<ResponseObject> updateBookingStatus(
            @PathVariable Integer bookingId,
            @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            String note = request.get("note");
            
            if (status == null) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .message("Status is required")
                        .responseCode(HttpStatus.BAD_REQUEST.value())
                        .build());
            }

            bookingService.updateBookingStatus(bookingId, status, note);
            
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Booking status updated successfully")
                    .responseCode(HttpStatus.OK.value())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .message("Failed to update booking status: " + e.getMessage())
                            .responseCode(HttpStatus.BAD_REQUEST.value())
                            .build());
        }
    }
}

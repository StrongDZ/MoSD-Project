package com.travel_agent.controllers;

import com.travel_agent.dto.ResponseObject;
import com.travel_agent.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

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
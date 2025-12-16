package com.travel_agent.controllers;

import com.travel_agent.annotation.CurrentUserId;
import com.travel_agent.dto.ResponseObject;
import com.travel_agent.dto.ReviewDTO;
import com.travel_agent.dto.ReviewRequestDTO;
import com.travel_agent.services.HotelReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotel")
@RequiredArgsConstructor
public class HotelReviewController {

    private final HotelReviewService hotelReviewService;

    /**
     * GET /api/hotel/{hotelId}/reviews
     * Lấy tất cả đánh giá của một khách sạn
     */
    @GetMapping("/{hotelId}/reviews")
    public ResponseEntity<ResponseObject> getHotelReviews(@PathVariable Integer hotelId) {
        try {
            List<ReviewDTO> reviews = hotelReviewService.getAllReviewsByHotelId(hotelId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Reviews retrieved successfully")
                    .data(reviews)
                    .responseCode(HttpStatus.OK.value())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .message("Failed to retrieve reviews: " + e.getMessage())
                            .responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }

    /**
     * POST /api/hotel/{hotelId}/reviews
     * Tạo đánh giá mới cho khách sạn
     */
    @PostMapping("/{hotelId}/reviews")
    @PreAuthorize("hasAnyRole('USER', 'GUEST')")
    public ResponseEntity<ResponseObject> createHotelReview(
            @PathVariable Integer hotelId,
            @Valid @RequestBody ReviewRequestDTO reviewRequest,
            @CurrentUserId Integer userId) {
        try {
            ReviewDTO createdReview = hotelReviewService.createReview(hotelId, reviewRequest, userId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseObject.builder()
                            .message("Review created successfully")
                            .data(createdReview)
                            .responseCode(HttpStatus.CREATED.value())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .message("Failed to create review: " + e.getMessage())
                            .responseCode(HttpStatus.BAD_REQUEST.value())
                            .build());
        }
    }
}

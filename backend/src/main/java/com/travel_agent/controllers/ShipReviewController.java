package com.travel_agent.controllers;

import com.travel_agent.annotation.CurrentUserId;
import com.travel_agent.dto.ResponseObject;
import com.travel_agent.dto.ReviewDTO;
import com.travel_agent.dto.ReviewRequestDTO;
import com.travel_agent.services.ShipReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ship")
@RequiredArgsConstructor
public class ShipReviewController {

    private final ShipReviewService shipReviewService;

    /**
     * GET /api/ship/{shipId}/reviews
     * Lấy tất cả đánh giá của một du thuyền
     */
    @GetMapping("/{shipId}/reviews")
    public ResponseEntity<ResponseObject> getShipReviews(@PathVariable Integer shipId) {
        try {
            List<ReviewDTO> reviews = shipReviewService.getAllReviewsByShipId(shipId);
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
     * POST /api/ship/{shipId}/reviews
     * Tạo đánh giá mới cho du thuyền
     */
    @PostMapping("/{shipId}/reviews")
    @PreAuthorize("hasAnyRole('USER', 'GUEST')")
    public ResponseEntity<ResponseObject> createShipReview(
            @PathVariable Integer shipId,
            @Valid @RequestBody ReviewRequestDTO reviewRequest,
            @CurrentUserId Integer userId) {
        try {
            ReviewDTO createdReview = shipReviewService.createReview(shipId, reviewRequest, userId);
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

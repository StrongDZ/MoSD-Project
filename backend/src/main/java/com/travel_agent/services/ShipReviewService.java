package com.travel_agent.services;

import com.travel_agent.dto.ReviewDTO;
import com.travel_agent.dto.ReviewRequestDTO;
import com.travel_agent.models.entity.ship.ShipReviewEntity;
import com.travel_agent.repositories.ship.ShipReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipReviewService {
    private final ShipReviewRepository shipReviewRepository;

    public List<ReviewDTO> getAllReviewsByShipId(Integer shipId) {
        List<ShipReviewEntity> reviews = shipReviewRepository.findByShipIdOrderByCreatedAtDesc(shipId);
        return reviews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ReviewDTO createReview(Integer shipId, ReviewRequestDTO requestDTO, Integer userId) {
        ShipReviewEntity review = ShipReviewEntity.builder()
                .shipId(shipId)
                .userId(userId)
                .name(requestDTO.getName())
                .content(requestDTO.getContent())
                .stars(requestDTO.getStars())
                .build();

        ShipReviewEntity savedReview = shipReviewRepository.save(review);
        return convertToDTO(savedReview);
    }

    private ReviewDTO convertToDTO(ShipReviewEntity entity) {
        return ReviewDTO.builder()
                .reviewId(entity.getReviewId())
                .name(entity.getName())
                .content(entity.getContent())
                .stars(entity.getStars())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

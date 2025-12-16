package com.travel_agent.services;

import com.travel_agent.dto.ReviewDTO;
import com.travel_agent.dto.ReviewRequestDTO;
import com.travel_agent.models.entity.hotel.HotelReviewEntity;
import com.travel_agent.repositories.hotel.HotelReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelReviewService {
    private final HotelReviewRepository hotelReviewRepository;

    public List<ReviewDTO> getAllReviewsByHotelId(Integer hotelId) {
        List<HotelReviewEntity> reviews = hotelReviewRepository.findByHotelIdOrderByCreatedAtDesc(hotelId);
        return reviews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ReviewDTO createReview(Integer hotelId, ReviewRequestDTO requestDTO, Integer userId) {
        HotelReviewEntity review = HotelReviewEntity.builder()
                .hotelId(hotelId)
                .userId(userId)
                .name(requestDTO.getName())
                .content(requestDTO.getContent())
                .stars(requestDTO.getStars())
                .build();

        HotelReviewEntity savedReview = hotelReviewRepository.save(review);
        return convertToDTO(savedReview);
    }

    private ReviewDTO convertToDTO(HotelReviewEntity entity) {
        return ReviewDTO.builder()
                .reviewId(entity.getReviewId())
                .name(entity.getName())
                .content(entity.getContent())
                .stars(entity.getStars())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

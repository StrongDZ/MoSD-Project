package com.travel_agent.repositories;

import com.travel_agent.models.entity.FeatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeatureRepository extends JpaRepository<FeatureEntity, Integer> {
    List<FeatureEntity> findByFeatureDescription(String featureDescription);
}

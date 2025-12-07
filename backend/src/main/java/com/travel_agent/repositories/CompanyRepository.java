package com.travel_agent.repositories;

import com.travel_agent.models.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Integer> {
    Optional<CompanyEntity> findByUsername(String username);
    Optional<CompanyEntity> findByUsernameAndPassword(String username, String password);
    @Query("SELECT c FROM CompanyEntity c WHERE c.username = :input OR c.email = :input")
    Optional<CompanyEntity> findByUsernameOrEmail(@Param("input") String input);
}
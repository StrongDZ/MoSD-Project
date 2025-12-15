package com.travel_agent.repositories;

import com.travel_agent.models.entity.TransactionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionInfoRepository extends JpaRepository<TransactionInfo, Integer> {
    List<TransactionInfo> findByUser_UserId(Integer userId);
}

package com.traffic.repository;

import com.traffic.model.RouteHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteHistoryRepository extends JpaRepository<RouteHistory, Long> {
    List<RouteHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<RouteHistory> findAllByOrderByCreatedAtDesc();
    void deleteByUserId(Long userId);
}

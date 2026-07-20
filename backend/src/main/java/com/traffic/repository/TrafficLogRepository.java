package com.traffic.repository;

import com.traffic.model.TrafficLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrafficLogRepository extends JpaRepository<TrafficLog, Long> {
    List<TrafficLog> findTop20ByOrderByUpdatedTimeDesc();
    List<TrafficLog> findByRoadIdOrderByUpdatedTimeDesc(Long roadId);
}

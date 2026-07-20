package com.traffic.repository;

import com.traffic.model.Road;
import com.traffic.model.RoadStatus;
import com.traffic.model.TrafficLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadRepository extends JpaRepository<Road, Long> {
    List<Road> findByRoadStatus(RoadStatus roadStatus);
    List<Road> findByTrafficLevel(TrafficLevel trafficLevel);
    List<Road> findBySourceOrDestination(String source, String destination);
}

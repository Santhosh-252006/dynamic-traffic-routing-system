package com.traffic.service;

import com.traffic.dto.RoadDto;
import com.traffic.dto.TrafficUpdateDto;
import com.traffic.exception.ResourceNotFoundException;
import com.traffic.model.*;
import com.traffic.repository.RoadRepository;
import com.traffic.repository.TrafficLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class TrafficService {

    private static final Logger logger = LoggerFactory.getLogger(TrafficService.class);

    @Autowired
    private RoadRepository roadRepository;

    @Autowired
    private TrafficLogRepository trafficLogRepository;

    @Autowired
    private RoadService roadService;

    @Autowired
    private AuditLogService auditLogService;

    private final Random random = new Random();

    public List<RoadDto> getCurrentTrafficStatus() {
        return roadRepository.findAll().stream()
                .map(roadService::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoadDto updateTraffic(Long roadId, TrafficUpdateDto updateDto, String currentUserEmail) {
        Road road = roadRepository.findById(roadId)
                .orElseThrow(() -> new ResourceNotFoundException("Road", "id", roadId));

        if (updateDto.getTrafficLevel() != null) {
            road.setTrafficLevel(updateDto.getTrafficLevel());
        }

        if (updateDto.getRoadStatus() != null) {
            road.setRoadStatus(updateDto.getRoadStatus());
        }

        Road updated = roadRepository.save(road);
        trafficLogRepository.save(new TrafficLog(updated.getId(), updated.getName(), updated.getTrafficLevel(), updated.getRoadStatus()));
        auditLogService.logAction(currentUserEmail, "TRAFFIC_UPDATED", "Updated traffic for road " + updated.getName() + ": Level=" + updated.getTrafficLevel() + ", Status=" + updated.getRoadStatus());

        return roadService.convertToDto(updated);
    }

    @Transactional
    public List<RoadDto> triggerSimulation(String currentUserEmail) {
        List<Road> roads = roadRepository.findAll();
        TrafficLevel[] levels = TrafficLevel.values();

        // Randomly alter traffic levels across 3-5 roads to simulate real-time traffic flux
        int countToModify = Math.min(roads.size(), 3 + random.nextInt(3));
        for (int i = 0; i < countToModify; i++) {
            Road road = roads.get(random.nextInt(roads.size()));
            TrafficLevel newLevel = levels[random.nextInt(levels.length)];
            road.setTrafficLevel(newLevel);
            roadRepository.save(road);
            trafficLogRepository.save(new TrafficLog(road.getId(), road.getName(), road.getTrafficLevel(), road.getRoadStatus()));
        }

        auditLogService.logAction(currentUserEmail, "SIMULATION_TRIGGERED", "Automated traffic simulation flux triggered across " + countToModify + " roads.");
        logger.info("Real-time traffic simulation flux triggered across {} roads.", countToModify);

        return roads.stream().map(roadService::convertToDto).collect(Collectors.toList());
    }
}

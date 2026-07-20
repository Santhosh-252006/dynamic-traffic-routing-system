package com.traffic.service;

import com.traffic.dto.IntersectionDto;
import com.traffic.dto.RoadDto;
import com.traffic.exception.ResourceNotFoundException;
import com.traffic.model.*;
import com.traffic.repository.IntersectionRepository;
import com.traffic.repository.RoadRepository;
import com.traffic.repository.TrafficLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoadService {

    @Autowired
    private RoadRepository roadRepository;

    @Autowired
    private IntersectionRepository intersectionRepository;

    @Autowired
    private TrafficLogRepository trafficLogRepository;

    @Autowired
    private AuditLogService auditLogService;

    public List<RoadDto> getAllRoads() {
        return roadRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<IntersectionDto> getAllIntersections() {
        return intersectionRepository.findAll().stream()
                .map(node -> new IntersectionDto(node.getId(), node.getCode(), node.getName(), node.getxCoord(), node.getyCoord(), node.getLatitude(), node.getLongitude()))
                .collect(Collectors.toList());
    }

    public RoadDto getRoadById(Long id) {
        Road road = roadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Road", "id", id));
        return convertToDto(road);
    }

    @Transactional
    public RoadDto createRoad(RoadDto roadDto, String currentUserEmail) {
        Road road = new Road(
                roadDto.getName(),
                roadDto.getSource(),
                roadDto.getDestination(),
                roadDto.getDistance(),
                roadDto.getSpeedLimit(),
                roadDto.getTrafficLevel() != null ? roadDto.getTrafficLevel() : TrafficLevel.LOW,
                roadDto.getRoadStatus() != null ? roadDto.getRoadStatus() : RoadStatus.OPEN,
                roadDto.isTwoWay()
        );

        Road saved = roadRepository.save(road);
        trafficLogRepository.save(new TrafficLog(saved.getId(), saved.getName(), saved.getTrafficLevel(), saved.getRoadStatus()));
        auditLogService.logAction(currentUserEmail, "ROAD_CREATED", "Created road: " + saved.getName() + " (" + saved.getSource() + " -> " + saved.getDestination() + ")");
        return convertToDto(saved);
    }

    @Transactional
    public RoadDto updateRoad(Long id, RoadDto roadDto, String currentUserEmail) {
        Road road = roadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Road", "id", id));

        road.setName(roadDto.getName());
        road.setSource(roadDto.getSource());
        road.setDestination(roadDto.getDestination());
        road.setDistance(roadDto.getDistance());
        road.setSpeedLimit(roadDto.getSpeedLimit());
        road.setTwoWay(roadDto.isTwoWay());

        if (roadDto.getTrafficLevel() != null) {
            road.setTrafficLevel(roadDto.getTrafficLevel());
        }

        if (roadDto.getRoadStatus() != null) {
            road.setRoadStatus(roadDto.getRoadStatus());
        }

        Road updated = roadRepository.save(road);
        trafficLogRepository.save(new TrafficLog(updated.getId(), updated.getName(), updated.getTrafficLevel(), updated.getRoadStatus()));
        auditLogService.logAction(currentUserEmail, "ROAD_UPDATED", "Updated road ID: " + id + " (" + updated.getName() + ")");
        return convertToDto(updated);
    }

    @Transactional
    public void deleteRoad(Long id, String currentUserEmail) {
        Road road = roadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Road", "id", id));
        roadRepository.delete(road);
        auditLogService.logAction(currentUserEmail, "ROAD_DELETED", "Deleted road ID: " + id + " (" + road.getName() + ")");
    }

    public RoadDto convertToDto(Road road) {
        return new RoadDto(
                road.getId(),
                road.getName(),
                road.getSource(),
                road.getDestination(),
                road.getDistance(),
                road.getSpeedLimit(),
                road.getTrafficLevel(),
                road.getRoadStatus(),
                road.isTwoWay(),
                road.getEstimatedTravelTimeMinutes()
        );
    }
}

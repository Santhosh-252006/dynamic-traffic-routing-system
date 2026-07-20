package com.traffic.service;

import com.traffic.algorithm.DijkstraGraph;
import com.traffic.dto.*;
import com.traffic.exception.BadRequestException;
import com.traffic.exception.ResourceNotFoundException;
import com.traffic.model.*;
import com.traffic.repository.IntersectionRepository;
import com.traffic.repository.RoadRepository;
import com.traffic.repository.RouteHistoryRepository;
import com.traffic.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RouteOptimizationService {

    @Autowired
    private RoadRepository roadRepository;

    @Autowired
    private IntersectionRepository intersectionRepository;

    @Autowired
    private RouteHistoryRepository routeHistoryRepository;

    @Transactional
    public RouteResponse findOptimalRoute(RouteSearchRequest request, UserPrincipal currentUser) {
        String source = resolveNodeCode(request.getSource());
        String destination = resolveNodeCode(request.getDestination());

        if (source.equalsIgnoreCase(destination)) {
            throw new BadRequestException("Source and Destination cannot be the same location.");
        }

        List<Road> allRoads = roadRepository.findAll();
        DijkstraGraph graph = new DijkstraGraph();
        for (Road road : allRoads) {
            graph.addRoad(road);
        }

        boolean useFastest = !"SHORTEST".equalsIgnoreCase(request.getOptimizationMode());

        // Primary Route Execution via Dijkstra
        DijkstraGraph.PathResult primaryPath = graph.findOptimalPath(source, destination, useFastest);

        if (!primaryPath.isReachable()) {
            RouteResponse unreachableResponse = new RouteResponse();
            unreachableResponse.setSource(source);
            unreachableResponse.setDestination(destination);
            unreachableResponse.setOptimizationMode(useFastest ? "FASTEST" : "SHORTEST");
            unreachableResponse.setReachable(false);
            unreachableResponse.setNodePath(Collections.emptyList());
            unreachableResponse.setSegments(Collections.emptyList());
            return unreachableResponse;
        }

        RouteResponse primaryResponse = buildRouteResponse(primaryPath, useFastest ? "FASTEST" : "SHORTEST");

        // Alternate Route Execution (by penalizing primary route edges)
        Set<Long> primaryRoadIds = primaryPath.getEdgePath().stream().map(Road::getId).collect(Collectors.toSet());
        DijkstraGraph.PathResult alternatePath = graph.findOptimalPathWithPenalties(source, destination, useFastest, primaryRoadIds);

        if (alternatePath.isReachable() && !alternatePath.getNodePath().equals(primaryPath.getNodePath())) {
            RouteResponse altResponse = buildRouteResponse(alternatePath, useFastest ? "FASTEST (ALT)" : "SHORTEST (ALT)");
            primaryResponse.setAlternateRoute(altResponse);
        }

        // Save Route History
        if (currentUser != null) {
            String pathString = String.join(" -> ", primaryResponse.getNodePath());
            RouteHistory history = new RouteHistory(
                    currentUser.getId(),
                    currentUser.getEmail(),
                    source,
                    destination,
                    primaryResponse.getOptimizationMode(),
                    pathString,
                    primaryResponse.getTotalDistanceKm(),
                    primaryResponse.getEstimatedTimeMinutes(),
                    primaryResponse.getOverallTrafficCondition()
            );
            routeHistoryRepository.save(history);
        }

        return primaryResponse;
    }

    private RouteResponse buildRouteResponse(DijkstraGraph.PathResult pathResult, String mode) {
        RouteResponse response = new RouteResponse();
        response.setSource(pathResult.getSource());
        response.setDestination(pathResult.getDestination());
        response.setOptimizationMode(mode);
        response.setReachable(true);

        List<String> nodeCodes = pathResult.getNodePath();
        Map<String, String> codeToNameMap = intersectionRepository.findAll().stream()
                .collect(Collectors.toMap(Intersection::getCode, Intersection::getName, (a, b) -> a));

        List<String> nodeNames = nodeCodes.stream()
                .map(code -> codeToNameMap.getOrDefault(code, code))
                .collect(Collectors.toList());

        response.setNodePath(nodeNames);
        response.setTotalIntersections(nodeCodes.size());
        response.setTotalDistanceKm(pathResult.getTotalDistanceKm());
        response.setEstimatedTimeMinutes(pathResult.getTotalTimeMinutes());

        List<RouteSegmentDto> segments = new ArrayList<>();
        double totalWeightedMultiplier = 0.0;

        for (Road road : pathResult.getEdgePath()) {
            RouteSegmentDto seg = new RouteSegmentDto(
                    road.getName(),
                    codeToNameMap.getOrDefault(road.getSource(), road.getSource()),
                    codeToNameMap.getOrDefault(road.getDestination(), road.getDestination()),
                    road.getDistance(),
                    road.getSpeedLimit(),
                    road.getEstimatedTravelTimeMinutes(),
                    road.getTrafficLevel()
            );
            segments.add(seg);
            totalWeightedMultiplier += road.getTrafficLevel().getMultiplier() * road.getDistance();
        }

        response.setSegments(segments);

        double avgMultiplier = pathResult.getTotalDistanceKm() > 0 ? (totalWeightedMultiplier / pathResult.getTotalDistanceKm()) : 1.0;
        response.setTrafficScore(Math.round(avgMultiplier * 100.0) / 100.0);

        if (avgMultiplier < 1.15) {
            response.setOverallTrafficCondition("Clear Traffic (Smooth)");
        } else if (avgMultiplier < 1.5) {
            response.setOverallTrafficCondition("Moderate Traffic");
        } else if (avgMultiplier < 2.0) {
            response.setOverallTrafficCondition("Heavy Congestion");
        } else {
            response.setOverallTrafficCondition("Severe Gridlock");
        }

        return response;
    }

    private String resolveNodeCode(String sourceOrName) {
        Optional<Intersection> node = intersectionRepository.findByCodeOrName(sourceOrName, sourceOrName);
        return node.map(Intersection::getCode).orElse(sourceOrName);
    }

    public List<RouteHistoryDto> getUserRouteHistory(Long userId) {
        return routeHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertHistoryToDto)
                .collect(Collectors.toList());
    }

    public List<RouteHistoryDto> getAllRouteHistory() {
        return routeHistoryRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::convertHistoryToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteRouteHistoryItem(Long id, Long userId, boolean isAdmin) {
        RouteHistory history = routeHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RouteHistory", "id", id));
        if (!isAdmin && !history.getUserId().equals(userId)) {
            throw new BadRequestException("You are not authorized to delete this history item.");
        }
        routeHistoryRepository.delete(history);
    }

    @Transactional
    public void clearUserHistory(Long userId) {
        routeHistoryRepository.deleteByUserId(userId);
    }

    private RouteHistoryDto convertHistoryToDto(RouteHistory h) {
        return new RouteHistoryDto(
                h.getId(),
                h.getUserId(),
                h.getUserEmail(),
                h.getSource(),
                h.getDestination(),
                h.getOptimizationMode(),
                h.getRoutePath(),
                h.getTotalDistance(),
                h.getEstimatedTimeMinutes(),
                h.getTrafficCondition(),
                h.getCreatedAt()
        );
    }
}

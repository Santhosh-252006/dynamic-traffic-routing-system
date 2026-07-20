package com.traffic.service;

import com.traffic.dto.RouteResponse;
import com.traffic.dto.RouteSearchRequest;
import com.traffic.model.Intersection;
import com.traffic.model.Road;
import com.traffic.model.RoadStatus;
import com.traffic.model.TrafficLevel;
import com.traffic.repository.IntersectionRepository;
import com.traffic.repository.RoadRepository;
import com.traffic.repository.RouteHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RouteOptimizationServiceTest {

    @Mock
    private RoadRepository roadRepository;

    @Mock
    private IntersectionRepository intersectionRepository;

    @Mock
    private RouteHistoryRepository routeHistoryRepository;

    @InjectMocks
    private RouteOptimizationService routeOptimizationService;

    private List<Road> sampleRoads;
    private List<Intersection> sampleNodes;

    @BeforeEach
    void setUp() {
        Intersection nodeA = new Intersection("INT-01", "Downtown", 100, 100, 40.0, -74.0);
        Intersection nodeB = new Intersection("INT-02", "Central", 200, 100, 40.1, -74.1);
        Intersection nodeC = new Intersection("INT-03", "Tech Park", 300, 100, 40.2, -74.2);

        sampleNodes = Arrays.asList(nodeA, nodeB, nodeC);

        Road road1 = new Road("Road A-B", "INT-01", "INT-02", 5.0, 50.0, TrafficLevel.LOW, RoadStatus.OPEN, true);
        road1.setId(1L);
        Road road2 = new Road("Road B-C", "INT-02", "INT-03", 5.0, 50.0, TrafficLevel.HEAVY, RoadStatus.OPEN, true);
        road2.setId(2L);
        Road road3 = new Road("Direct Bypass A-C", "INT-01", "INT-03", 12.0, 60.0, TrafficLevel.LOW, RoadStatus.OPEN, true);
        road3.setId(3L);

        sampleRoads = Arrays.asList(road1, road2, road3);
    }

    @Test
    void testFindOptimalRoute_ShortestPath_ReturnsMinDistance() {
        when(roadRepository.findAll()).thenReturn(sampleRoads);
        when(intersectionRepository.findAll()).thenReturn(sampleNodes);
        when(intersectionRepository.findByCodeOrName(anyString(), anyString())).thenAnswer(invocation -> {
            String code = invocation.getArgument(0);
            return sampleNodes.stream().filter(n -> n.getCode().equalsIgnoreCase(code) || n.getName().equalsIgnoreCase(code)).findFirst();
        });

        RouteSearchRequest request = new RouteSearchRequest("INT-01", "INT-03", "SHORTEST");
        RouteResponse response = routeOptimizationService.findOptimalRoute(request, null);

        assertTrue(response.isReachable());
        assertEquals(10.0, response.getTotalDistanceKm()); // INT-01 -> INT-02 -> INT-03 = 5 + 5 = 10km (shorter than 12km)
    }

    @Test
    void testFindOptimalRoute_FastestPath_FactorsInTrafficCongestion() {
        when(roadRepository.findAll()).thenReturn(sampleRoads);
        when(intersectionRepository.findAll()).thenReturn(sampleNodes);
        when(intersectionRepository.findByCodeOrName(anyString(), anyString())).thenAnswer(invocation -> {
            String code = invocation.getArgument(0);
            return sampleNodes.stream().filter(n -> n.getCode().equalsIgnoreCase(code) || n.getName().equalsIgnoreCase(code)).findFirst();
        });

        RouteSearchRequest request = new RouteSearchRequest("INT-01", "INT-03", "FASTEST");
        RouteResponse response = routeOptimizationService.findOptimalRoute(request, null);

        assertTrue(response.isReachable());
        // Direct Bypass A-C (12km @ 60km/h LOW traffic = 12 mins) vs A-B-C (5km LOW + 5km HEAVY @ 50km/h = 6 mins + 15 mins = 21 mins)
        // Dijkstra FASTEST will choose Direct Bypass A-C!
        assertEquals("FASTEST", response.getOptimizationMode());
        assertEquals(12.0, response.getTotalDistanceKm());
    }

    @Test
    void testFindOptimalRoute_ClosedRoadIgnored() {
        // Close Direct Bypass A-C
        sampleRoads.get(2).setRoadStatus(RoadStatus.CLOSED);

        when(roadRepository.findAll()).thenReturn(sampleRoads);
        when(intersectionRepository.findAll()).thenReturn(sampleNodes);
        when(intersectionRepository.findByCodeOrName(anyString(), anyString())).thenAnswer(invocation -> {
            String code = invocation.getArgument(0);
            return sampleNodes.stream().filter(n -> n.getCode().equalsIgnoreCase(code) || n.getName().equalsIgnoreCase(code)).findFirst();
        });

        RouteSearchRequest request = new RouteSearchRequest("INT-01", "INT-03", "FASTEST");
        RouteResponse response = routeOptimizationService.findOptimalRoute(request, null);

        assertTrue(response.isReachable());
        // Since Direct Bypass is CLOSED, Dijkstra routes via A-B-C
        assertEquals(10.0, response.getTotalDistanceKm());
    }
}

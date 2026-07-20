package com.traffic.controller;

import com.traffic.dto.RoadDto;
import com.traffic.dto.TrafficUpdateDto;
import com.traffic.security.UserPrincipal;
import com.traffic.service.TrafficService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/traffic")
@Tag(name = "Traffic Management API", description = "Endpoints for viewing real-time traffic levels, updating road status, and triggering live simulation")
public class TrafficController {

    @Autowired
    private TrafficService trafficService;

    @GetMapping
    @Operation(summary = "Get current traffic status for all roads")
    public ResponseEntity<List<RoadDto>> getCurrentTrafficStatus() {
        return ResponseEntity.ok(trafficService.getCurrentTrafficStatus());
    }

    @PutMapping("/{roadId}")
    @Operation(summary = "Update traffic level or road status for a specific road (Admin only)")
    public ResponseEntity<RoadDto> updateTraffic(@PathVariable Long roadId,
                                                 @RequestBody TrafficUpdateDto updateDto,
                                                 @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String email = userPrincipal != null ? userPrincipal.getEmail() : "ADMIN";
        return ResponseEntity.ok(trafficService.updateTraffic(roadId, updateDto, email));
    }

    @PostMapping("/simulate")
    @Operation(summary = "Trigger random real-time traffic flux simulation across the city network (Admin only)")
    public ResponseEntity<List<RoadDto>> triggerSimulation(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String email = userPrincipal != null ? userPrincipal.getEmail() : "ADMIN";
        return ResponseEntity.ok(trafficService.triggerSimulation(email));
    }
}

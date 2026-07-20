package com.traffic.controller;

import com.traffic.dto.IntersectionDto;
import com.traffic.dto.RoadDto;
import com.traffic.security.UserPrincipal;
import com.traffic.service.RoadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Road Network API", description = "Endpoints for managing road edges and map intersection nodes")
public class RoadController {

    @Autowired
    private RoadService roadService;

    @GetMapping("/roads")
    @Operation(summary = "Get all roads")
    public ResponseEntity<List<RoadDto>> getAllRoads() {
        return ResponseEntity.ok(roadService.getAllRoads());
    }

    @GetMapping("/intersections")
    @Operation(summary = "Get all map intersections / graph nodes")
    public ResponseEntity<List<IntersectionDto>> getAllIntersections() {
        return ResponseEntity.ok(roadService.getAllIntersections());
    }

    @GetMapping("/roads/{id}")
    @Operation(summary = "Get road details by ID")
    public ResponseEntity<RoadDto> getRoadById(@PathVariable Long id) {
        return ResponseEntity.ok(roadService.getRoadById(id));
    }

    @PostMapping("/roads")
    @Operation(summary = "Create a new road (Admin only)")
    public ResponseEntity<RoadDto> createRoad(@Valid @RequestBody RoadDto roadDto,
                                               @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String email = userPrincipal != null ? userPrincipal.getEmail() : "ADMIN";
        return ResponseEntity.ok(roadService.createRoad(roadDto, email));
    }

    @PutMapping("/roads/{id}")
    @Operation(summary = "Update an existing road (Admin only)")
    public ResponseEntity<RoadDto> updateRoad(@PathVariable Long id,
                                               @Valid @RequestBody RoadDto roadDto,
                                               @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String email = userPrincipal != null ? userPrincipal.getEmail() : "ADMIN";
        return ResponseEntity.ok(roadService.updateRoad(id, roadDto, email));
    }

    @DeleteMapping("/roads/{id}")
    @Operation(summary = "Delete a road (Admin only)")
    public ResponseEntity<Void> deleteRoad(@PathVariable Long id,
                                           @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String email = userPrincipal != null ? userPrincipal.getEmail() : "ADMIN";
        roadService.deleteRoad(id, email);
        return ResponseEntity.noContent().build();
    }
}

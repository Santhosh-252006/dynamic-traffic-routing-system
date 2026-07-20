package com.traffic.controller;

import com.traffic.dto.*;
import com.traffic.security.UserPrincipal;
import com.traffic.service.RouteOptimizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@Tag(name = "Route Optimization API", description = "Endpoints for finding optimal routes using Dijkstra's algorithm and managing route search history")
public class RouteController {

    @Autowired
    private RouteOptimizationService routeOptimizationService;

    @PostMapping("/find")
    @Operation(summary = "Calculate optimal (Shortest or Fastest) route using Dijkstra's Algorithm")
    public ResponseEntity<RouteResponse> findOptimalRoute(@Valid @RequestBody RouteSearchRequest request,
                                                           @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(routeOptimizationService.findOptimalRoute(request, userPrincipal));
    }

    @GetMapping("/history")
    @Operation(summary = "Get route search history for current user or all (Admin)")
    public ResponseEntity<List<RouteHistoryDto>> getRouteHistory(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).build();
        }
        boolean isAdmin = userPrincipal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return ResponseEntity.ok(routeOptimizationService.getAllRouteHistory());
        } else {
            return ResponseEntity.ok(routeOptimizationService.getUserRouteHistory(userPrincipal.getId()));
        }
    }

    @DeleteMapping("/history/{id}")
    @Operation(summary = "Delete a specific route history item")
    public ResponseEntity<Void> deleteHistoryItem(@PathVariable Long id,
                                                   @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).build();
        }
        boolean isAdmin = userPrincipal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        routeOptimizationService.deleteRouteHistoryItem(id, userPrincipal.getId(), isAdmin);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/history/clear")
    @Operation(summary = "Clear all route history for the authenticated user")
    public ResponseEntity<Void> clearUserHistory(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).build();
        }
        routeOptimizationService.clearUserHistory(userPrincipal.getId());
        return ResponseEntity.noContent().build();
    }
}

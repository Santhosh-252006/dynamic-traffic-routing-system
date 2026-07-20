package com.traffic.dto;

import java.time.LocalDateTime;

public class RouteHistoryDto {
    private Long id;
    private Long userId;
    private String userEmail;
    private String source;
    private String destination;
    private String optimizationMode;
    private String routePath;
    private double totalDistance;
    private double estimatedTimeMinutes;
    private String trafficCondition;
    private LocalDateTime createdAt;

    public RouteHistoryDto() {
    }

    public RouteHistoryDto(Long id, Long userId, String userEmail, String source, String destination, String optimizationMode, String routePath, double totalDistance, double estimatedTimeMinutes, String trafficCondition, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.source = source;
        this.destination = destination;
        this.optimizationMode = optimizationMode;
        this.routePath = routePath;
        this.totalDistance = totalDistance;
        this.estimatedTimeMinutes = estimatedTimeMinutes;
        this.trafficCondition = trafficCondition;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getOptimizationMode() {
        return optimizationMode;
    }

    public void setOptimizationMode(String optimizationMode) {
        this.optimizationMode = optimizationMode;
    }

    public String getRoutePath() {
        return routePath;
    }

    public void setRoutePath(String routePath) {
        this.routePath = routePath;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public void setEstimatedTimeMinutes(double estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }

    public String getTrafficCondition() {
        return trafficCondition;
    }

    public void setTrafficCondition(String trafficCondition) {
        this.trafficCondition = trafficCondition;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

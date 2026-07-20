package com.traffic.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "routes_history")
public class RouteHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private String userEmail;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private String optimizationMode; // SHORTEST or FASTEST

    @Column(nullable = false, length = 2000)
    private String routePath; // Comma separated list of intersection codes/names

    @Column(nullable = false)
    private double totalDistance; // km

    @Column(nullable = false)
    private double estimatedTimeMinutes; // mins

    private String trafficCondition;

    private LocalDateTime createdAt;

    public RouteHistory() {
    }

    public RouteHistory(Long userId, String userEmail, String source, String destination, String optimizationMode, String routePath, double totalDistance, double estimatedTimeMinutes, String trafficCondition) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.source = source;
        this.destination = destination;
        this.optimizationMode = optimizationMode;
        this.routePath = routePath;
        this.totalDistance = totalDistance;
        this.estimatedTimeMinutes = estimatedTimeMinutes;
        this.trafficCondition = trafficCondition;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
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

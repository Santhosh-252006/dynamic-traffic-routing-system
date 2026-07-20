package com.traffic.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "roads")
public class Road {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String source; // Intersection code or name

    @Column(nullable = false)
    private String destination; // Intersection code or name

    @Column(nullable = false)
    private double distance; // Distance in kilometers

    @Column(nullable = false)
    private double speedLimit; // Speed limit in km/h

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrafficLevel trafficLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoadStatus roadStatus;

    private boolean twoWay = true;

    private LocalDateTime updatedTime;

    public Road() {
    }

    public Road(String name, String source, String destination, double distance, double speedLimit, TrafficLevel trafficLevel, RoadStatus roadStatus, boolean twoWay) {
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.distance = distance;
        this.speedLimit = speedLimit;
        this.trafficLevel = trafficLevel;
        this.roadStatus = roadStatus;
        this.twoWay = twoWay;
        this.updatedTime = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * Calculates the estimated travel time in minutes based on distance, speed limit, and traffic level.
     * Formula: Travel Time (hrs) = (Distance / SpeedLimit) * TrafficMultiplier
     * Travel Time (mins) = Travel Time (hrs) * 60
     */
    public double getEstimatedTravelTimeMinutes() {
        if (roadStatus == RoadStatus.CLOSED || speedLimit <= 0) {
            return Double.POSITIVE_INFINITY;
        }
        double baseTimeHours = distance / speedLimit;
        double adjustedTimeHours = baseTimeHours * trafficLevel.getMultiplier();
        return Math.round(adjustedTimeHours * 60.0 * 100.0) / 100.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(double speedLimit) {
        this.speedLimit = speedLimit;
    }

    public TrafficLevel getTrafficLevel() {
        return trafficLevel;
    }

    public void setTrafficLevel(TrafficLevel trafficLevel) {
        this.trafficLevel = trafficLevel;
    }

    public RoadStatus getRoadStatus() {
        return roadStatus;
    }

    public void setRoadStatus(RoadStatus roadStatus) {
        this.roadStatus = roadStatus;
    }

    public boolean isTwoWay() {
        return twoWay;
    }

    public void setTwoWay(boolean twoWay) {
        this.twoWay = twoWay;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}

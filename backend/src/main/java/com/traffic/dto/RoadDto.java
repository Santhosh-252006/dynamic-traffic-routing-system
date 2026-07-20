package com.traffic.dto;

import com.traffic.model.RoadStatus;
import com.traffic.model.TrafficLevel;

public class RoadDto {
    private Long id;
    private String name;
    private String source;
    private String destination;
    private double distance;
    private double speedLimit;
    private TrafficLevel trafficLevel;
    private RoadStatus roadStatus;
    private boolean twoWay;
    private double estimatedTravelTimeMinutes;

    public RoadDto() {
    }

    public RoadDto(Long id, String name, String source, String destination, double distance, double speedLimit, TrafficLevel trafficLevel, RoadStatus roadStatus, boolean twoWay, double estimatedTravelTimeMinutes) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.distance = distance;
        this.speedLimit = speedLimit;
        this.trafficLevel = trafficLevel;
        this.roadStatus = roadStatus;
        this.twoWay = twoWay;
        this.estimatedTravelTimeMinutes = estimatedTravelTimeMinutes;
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

    public double getEstimatedTravelTimeMinutes() {
        return estimatedTravelTimeMinutes;
    }

    public void setEstimatedTravelTimeMinutes(double estimatedTravelTimeMinutes) {
        this.estimatedTravelTimeMinutes = estimatedTravelTimeMinutes;
    }
}

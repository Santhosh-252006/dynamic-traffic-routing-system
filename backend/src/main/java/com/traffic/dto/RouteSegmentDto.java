package com.traffic.dto;

import com.traffic.model.TrafficLevel;

public class RouteSegmentDto {
    private String roadName;
    private String fromNode;
    private String toNode;
    private double distanceKm;
    private double speedLimitKmh;
    private double travelTimeMinutes;
    private TrafficLevel trafficLevel;

    public RouteSegmentDto() {
    }

    public RouteSegmentDto(String roadName, String fromNode, String toNode, double distanceKm, double speedLimitKmh, double travelTimeMinutes, TrafficLevel trafficLevel) {
        this.roadName = roadName;
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.distanceKm = distanceKm;
        this.speedLimitKmh = speedLimitKmh;
        this.travelTimeMinutes = travelTimeMinutes;
        this.trafficLevel = trafficLevel;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public String getFromNode() {
        return fromNode;
    }

    public void setFromNode(String fromNode) {
        this.fromNode = fromNode;
    }

    public String getToNode() {
        return toNode;
    }

    public void setToNode(String toNode) {
        this.toNode = toNode;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public double getSpeedLimitKmh() {
        return speedLimitKmh;
    }

    public void setSpeedLimitKmh(double speedLimitKmh) {
        this.speedLimitKmh = speedLimitKmh;
    }

    public double getTravelTimeMinutes() {
        return travelTimeMinutes;
    }

    public void setTravelTimeMinutes(double travelTimeMinutes) {
        this.travelTimeMinutes = travelTimeMinutes;
    }

    public TrafficLevel getTrafficLevel() {
        return trafficLevel;
    }

    public void setTrafficLevel(TrafficLevel trafficLevel) {
        this.trafficLevel = trafficLevel;
    }
}

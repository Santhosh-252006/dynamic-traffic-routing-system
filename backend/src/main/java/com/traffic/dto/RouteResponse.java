package com.traffic.dto;

import java.util.List;

public class RouteResponse {
    private String source;
    private String destination;
    private String optimizationMode; // SHORTEST or FASTEST
    private List<String> nodePath;
    private List<RouteSegmentDto> segments;
    private double totalDistanceKm;
    private double estimatedTimeMinutes;
    private int totalIntersections;
    private String overallTrafficCondition;
    private double trafficScore; // 1.0 (best) to 5.0 (worst)
    private boolean reachable;
    private RouteResponse alternateRoute;

    public RouteResponse() {
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

    public List<String> getNodePath() {
        return nodePath;
    }

    public void setNodePath(List<String> nodePath) {
        this.nodePath = nodePath;
    }

    public List<RouteSegmentDto> getSegments() {
        return segments;
    }

    public void setSegments(List<RouteSegmentDto> segments) {
        this.segments = segments;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
    }

    public double getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public void setEstimatedTimeMinutes(double estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }

    public int getTotalIntersections() {
        return totalIntersections;
    }

    public void setTotalIntersections(int totalIntersections) {
        this.totalIntersections = totalIntersections;
    }

    public String getOverallTrafficCondition() {
        return overallTrafficCondition;
    }

    public void setOverallTrafficCondition(String overallTrafficCondition) {
        this.overallTrafficCondition = overallTrafficCondition;
    }

    public double getTrafficScore() {
        return trafficScore;
    }

    public void setTrafficScore(double trafficScore) {
        this.trafficScore = trafficScore;
    }

    public boolean isReachable() {
        return reachable;
    }

    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }

    public RouteResponse getAlternateRoute() {
        return alternateRoute;
    }

    public void setAlternateRoute(RouteResponse alternateRoute) {
        this.alternateRoute = alternateRoute;
    }
}

package com.traffic.dto;

import java.util.Map;

public class DashboardMetricsDto {
    private long totalRoads;
    private long totalIntersections;
    private long openRoads;
    private long closedRoads;
    private long totalSearchedRoutes;
    private double averageTravelTimeMinutes;
    private Map<String, Long> trafficLevelDistribution;

    public DashboardMetricsDto() {
    }

    public DashboardMetricsDto(long totalRoads, long totalIntersections, long openRoads, long closedRoads, long totalSearchedRoutes, double averageTravelTimeMinutes, Map<String, Long> trafficLevelDistribution) {
        this.totalRoads = totalRoads;
        this.totalIntersections = totalIntersections;
        this.openRoads = openRoads;
        this.closedRoads = closedRoads;
        this.totalSearchedRoutes = totalSearchedRoutes;
        this.averageTravelTimeMinutes = averageTravelTimeMinutes;
        this.trafficLevelDistribution = trafficLevelDistribution;
    }

    public long getTotalRoads() {
        return totalRoads;
    }

    public void setTotalRoads(long totalRoads) {
        this.totalRoads = totalRoads;
    }

    public long getTotalIntersections() {
        return totalIntersections;
    }

    public void setTotalIntersections(long totalIntersections) {
        this.totalIntersections = totalIntersections;
    }

    public long getOpenRoads() {
        return openRoads;
    }

    public void setOpenRoads(long openRoads) {
        this.openRoads = openRoads;
    }

    public long getClosedRoads() {
        return closedRoads;
    }

    public void setClosedRoads(long closedRoads) {
        this.closedRoads = closedRoads;
    }

    public long getTotalSearchedRoutes() {
        return totalSearchedRoutes;
    }

    public void setTotalSearchedRoutes(long totalSearchedRoutes) {
        this.totalSearchedRoutes = totalSearchedRoutes;
    }

    public double getAverageTravelTimeMinutes() {
        return averageTravelTimeMinutes;
    }

    public void setAverageTravelTimeMinutes(double averageTravelTimeMinutes) {
        this.averageTravelTimeMinutes = averageTravelTimeMinutes;
    }

    public Map<String, Long> getTrafficLevelDistribution() {
        return trafficLevelDistribution;
    }

    public void setTrafficLevelDistribution(Map<String, Long> trafficLevelDistribution) {
        this.trafficLevelDistribution = trafficLevelDistribution;
    }
}

package com.traffic.dto;

import java.util.List;
import java.util.Map;

public class ReportDto {
    private List<RoadDto> mostCongestedRoads;
    private List<String> frequentlyUsedRoutes;
    private double averageSystemTravelTime;
    private Map<String, Long> trafficLevelCounts;
    private Map<String, Integer> hourlySearchVolume;

    public ReportDto() {
    }

    public List<RoadDto> getMostCongestedRoads() {
        return mostCongestedRoads;
    }

    public void setMostCongestedRoads(List<RoadDto> mostCongestedRoads) {
        this.mostCongestedRoads = mostCongestedRoads;
    }

    public List<String> getFrequentlyUsedRoutes() {
        return frequentlyUsedRoutes;
    }

    public void setFrequentlyUsedRoutes(List<String> frequentlyUsedRoutes) {
        this.frequentlyUsedRoutes = frequentlyUsedRoutes;
    }

    public double getAverageSystemTravelTime() {
        return averageSystemTravelTime;
    }

    public void setAverageSystemTravelTime(double averageSystemTravelTime) {
        this.averageSystemTravelTime = averageSystemTravelTime;
    }

    public Map<String, Long> getTrafficLevelCounts() {
        return trafficLevelCounts;
    }

    public void setTrafficLevelCounts(Map<String, Long> trafficLevelCounts) {
        this.trafficLevelCounts = trafficLevelCounts;
    }

    public Map<String, Integer> getHourlySearchVolume() {
        return hourlySearchVolume;
    }

    public void setHourlySearchVolume(Map<String, Integer> hourlySearchVolume) {
        this.hourlySearchVolume = hourlySearchVolume;
    }
}

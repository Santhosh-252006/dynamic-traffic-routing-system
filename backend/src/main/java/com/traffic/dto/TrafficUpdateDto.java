package com.traffic.dto;

import com.traffic.model.RoadStatus;
import com.traffic.model.TrafficLevel;

public class TrafficUpdateDto {
    private TrafficLevel trafficLevel;
    private RoadStatus roadStatus;

    public TrafficUpdateDto() {
    }

    public TrafficUpdateDto(TrafficLevel trafficLevel, RoadStatus roadStatus) {
        this.trafficLevel = trafficLevel;
        this.roadStatus = roadStatus;
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
}

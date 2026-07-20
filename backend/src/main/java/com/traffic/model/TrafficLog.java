package com.traffic.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "traffic_logs")
public class TrafficLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long roadId;

    private String roadName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrafficLevel trafficLevel;

    @Enumerated(EnumType.STRING)
    private RoadStatus roadStatus;

    private LocalDateTime updatedTime;

    public TrafficLog() {
    }

    public TrafficLog(Long roadId, String roadName, TrafficLevel trafficLevel, RoadStatus roadStatus) {
        this.roadId = roadId;
        this.roadName = roadName;
        this.trafficLevel = trafficLevel;
        this.roadStatus = roadStatus;
        this.updatedTime = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (updatedTime == null) {
            updatedTime = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoadId() {
        return roadId;
    }

    public void setRoadId(Long roadId) {
        this.roadId = roadId;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
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

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}

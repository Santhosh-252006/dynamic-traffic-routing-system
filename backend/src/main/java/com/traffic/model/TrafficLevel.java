package com.traffic.model;

public enum TrafficLevel {
    LOW(1.0, "Smooth Traffic", "#10B981"),       // Green
    MEDIUM(1.3, "Moderate Congestion", "#F59E0B"), // Amber
    HIGH(1.8, "Heavy Traffic", "#F97316"),         // Orange
    HEAVY(2.5, "Severe Gridlock", "#EF4444");       // Red

    private final double multiplier;
    private final String description;
    private final String colorCode;

    TrafficLevel(double multiplier, String description, String colorCode) {
        this.multiplier = multiplier;
        this.description = description;
        this.colorCode = colorCode;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public String getDescription() {
        return description;
    }

    public String getColorCode() {
        return colorCode;
    }
}

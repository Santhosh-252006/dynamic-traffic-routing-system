package com.traffic.dto;

import jakarta.validation.constraints.NotBlank;

public class RouteSearchRequest {

    @NotBlank(message = "Source is required")
    private String source;

    @NotBlank(message = "Destination is required")
    private String destination;

    private String optimizationMode = "FASTEST"; // FASTEST or SHORTEST

    public RouteSearchRequest() {
    }

    public RouteSearchRequest(String source, String destination, String optimizationMode) {
        this.source = source;
        this.destination = destination;
        this.optimizationMode = optimizationMode;
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
}

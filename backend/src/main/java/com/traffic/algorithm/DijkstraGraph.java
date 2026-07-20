package com.traffic.algorithm;

import com.traffic.model.Road;
import com.traffic.model.RoadStatus;

import java.util.*;

public class DijkstraGraph {

    public static class Edge {
        private final String source;
        private final String target;
        private final double distanceKm;
        private final double travelTimeMinutes;
        private final Road road;

        public Edge(String source, String target, double distanceKm, double travelTimeMinutes, Road road) {
            this.source = source;
            this.target = target;
            this.distanceKm = distanceKm;
            this.travelTimeMinutes = travelTimeMinutes;
            this.road = road;
        }

        public String getSource() {
            return source;
        }

        public String getTarget() {
            return target;
        }

        public double getDistanceKm() {
            return distanceKm;
        }

        public double getTravelTimeMinutes() {
            return travelTimeMinutes;
        }

        public Road getRoad() {
            return road;
        }
    }

    public static class PathResult {
        private final String source;
        private final String destination;
        private final List<String> nodePath;
        private final List<Road> edgePath;
        private final double totalDistanceKm;
        private final double totalTimeMinutes;
        private final boolean reachable;

        public PathResult(String source, String destination, List<String> nodePath, List<Road> edgePath, double totalDistanceKm, double totalTimeMinutes, boolean reachable) {
            this.source = source;
            this.destination = destination;
            this.nodePath = nodePath;
            this.edgePath = edgePath;
            this.totalDistanceKm = totalDistanceKm;
            this.totalTimeMinutes = totalTimeMinutes;
            this.reachable = reachable;
        }

        public String getSource() {
            return source;
        }

        public String getDestination() {
            return destination;
        }

        public List<String> getNodePath() {
            return nodePath;
        }

        public List<Road> getEdgePath() {
            return edgePath;
        }

        public double getTotalDistanceKm() {
            return totalDistanceKm;
        }

        public double getTotalTimeMinutes() {
            return totalTimeMinutes;
        }

        public boolean isReachable() {
            return reachable;
        }
    }

    private final Map<String, List<Edge>> adjacencyList = new HashMap<>();

    public void addRoad(Road road) {
        if (road.getRoadStatus() == RoadStatus.CLOSED) {
            return; // Ignore closed roads so Dijkstra never traverses them
        }
        String u = road.getSource();
        String v = road.getDestination();

        double dist = road.getDistance();
        double time = road.getEstimatedTravelTimeMinutes();

        adjacencyList.computeIfAbsent(u, k -> new ArrayList<>()).add(new Edge(u, v, dist, time, road));

        if (road.isTwoWay()) {
            adjacencyList.computeIfAbsent(v, k -> new ArrayList<>()).add(new Edge(v, u, dist, time, road));
        }
    }

    /**
     * Executes Dijkstra's algorithm to find the optimal route.
     * @param startNode Source intersection code/name
     * @param endNode Destination intersection code/name
     * @param useFastest If true, optimizes for travel time (mins); if false, optimizes for distance (km)
     */
    public PathResult findOptimalPath(String startNode, String endNode, boolean useFastest) {
        return findOptimalPathWithPenalties(startNode, endNode, useFastest, Collections.emptySet());
    }

    /**
     * Executes Dijkstra with optional edge penalties (used for computing alternative routes).
     */
    public PathResult findOptimalPathWithPenalties(String startNode, String endNode, boolean useFastest, Set<Long> penalizedRoadIds) {
        if (!adjacencyList.containsKey(startNode) && !startNode.equalsIgnoreCase(endNode)) {
            return new PathResult(startNode, endNode, Collections.emptyList(), Collections.emptyList(), 0, 0, false);
        }

        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previousNode = new HashMap<>();
        Map<String, Edge> previousEdge = new HashMap<>();

        PriorityQueue<NodeCost> pq = new PriorityQueue<>(Comparator.comparingDouble(NodeCost::getCost));

        distances.put(startNode, 0.0);
        pq.add(new NodeCost(startNode, 0.0));

        while (!pq.isEmpty()) {
            NodeCost current = pq.poll();
            String u = current.getNode();

            if (u.equalsIgnoreCase(endNode)) {
                break; // Target reached
            }

            if (current.getCost() > distances.getOrDefault(u, Double.POSITIVE_INFINITY)) {
                continue;
            }

            List<Edge> neighbors = adjacencyList.getOrDefault(u, Collections.emptyList());
            for (Edge edge : neighbors) {
                String v = edge.getTarget();

                double weight = useFastest ? edge.getTravelTimeMinutes() : edge.getDistanceKm();
                if (penalizedRoadIds.contains(edge.getRoad().getId())) {
                    weight *= 3.0; // Apply penalty to force alternate routes
                }

                double newCost = distances.get(u) + weight;
                if (newCost < distances.getOrDefault(v, Double.POSITIVE_INFINITY)) {
                    distances.put(v, newCost);
                    previousNode.put(v, u);
                    previousEdge.put(v, edge);
                    pq.add(new NodeCost(v, newCost));
                }
            }
        }

        if (!distances.containsKey(endNode) || Double.isInfinite(distances.get(endNode))) {
            return new PathResult(startNode, endNode, Collections.emptyList(), Collections.emptyList(), 0, 0, false);
        }

        // Reconstruct path
        LinkedList<String> nodePath = new LinkedList<>();
        LinkedList<Road> edgePath = new LinkedList<>();
        String curr = endNode;
        nodePath.addFirst(curr);

        double totalDistance = 0.0;
        double totalTime = 0.0;

        while (previousNode.containsKey(curr)) {
            Edge edge = previousEdge.get(curr);
            edgePath.addFirst(edge.getRoad());
            totalDistance += edge.getDistanceKm();
            totalTime += edge.getTravelTimeMinutes();

            curr = previousNode.get(curr);
            nodePath.addFirst(curr);
        }

        return new PathResult(
            startNode,
            endNode,
            nodePath,
            edgePath,
            Math.round(totalDistance * 100.0) / 100.0,
            Math.round(totalTime * 100.0) / 100.0,
            true
        );
    }

    private static class NodeCost {
        private final String node;
        private final double cost;

        public NodeCost(String node, double cost) {
            this.node = node;
            this.cost = cost;
        }

        public String getNode() {
            return node;
        }

        public double getCost() {
            return cost;
        }
    }
}

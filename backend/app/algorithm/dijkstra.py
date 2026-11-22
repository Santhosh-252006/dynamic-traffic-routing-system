import heapq
from typing import Dict, List, Set, Optional, Any
from dataclasses import dataclass, field

@dataclass
class Edge:
    source: str
    target: str
    distance_km: float
    travel_time_minutes: float
    road_id: int
    road_name: str
    speed_limit: float
    traffic_level: str
    road_obj: Any = None

@dataclass
class PathResult:
    source: str
    destination: str
    node_path: List[str] = field(default_factory=list)
    edge_path: List[Edge] = field(default_factory=list)
    total_distance_km: float = 0.0
    total_time_minutes: float = 0.0
    reachable: bool = False

class DijkstraGraph:
    def __init__(self):
        self.adjacency_list: Dict[str, List[Edge]] = {}

    def add_road(self, road):
        """Add road to graph unless it's marked CLOSED."""
        if getattr(road, "road_status", "OPEN") == "CLOSED":
            return

        u = road.source
        v = road.destination
        dist = float(road.distance)
        time_mins = float(road.estimated_travel_time_minutes)

        edge_fwd = Edge(
            source=u,
            target=v,
            distance_km=dist,
            travel_time_minutes=time_mins,
            road_id=road.id,
            road_name=road.name,
            speed_limit=road.speed_limit,
            traffic_level=road.traffic_level,
            road_obj=road
        )
        self.adjacency_list.setdefault(u, []).append(edge_fwd)

        if getattr(road, "two_way", True):
            edge_rev = Edge(
                source=v,
                target=u,
                distance_km=dist,
                travel_time_minutes=time_mins,
                road_id=road.id,
                road_name=road.name,
                speed_limit=road.speed_limit,
                traffic_level=road.traffic_level,
                road_obj=road
            )
            self.adjacency_list.setdefault(v, []).append(edge_rev)

    def find_optimal_path(
        self, 
        start_node: str, 
        end_node: str, 
        use_fastest: bool = True,
        penalized_road_ids: Optional[Set[int]] = None
    ) -> PathResult:
        if penalized_road_ids is None:
            penalized_road_ids = set()

        if start_node not in self.adjacency_list and start_node.lower() != end_node.lower():
            return PathResult(source=start_node, destination=end_node, reachable=False)

        # Min-Heap Priority Queue: entries are (cost, current_node)
        pq: List[tuple[float, str]] = []
        heapq.heappush(pq, (0.0, start_node))

        distances: Dict[str, float] = {start_node: 0.0}
        previous_node: Dict[str, str] = {}
        previous_edge: Dict[str, Edge] = {}

        while pq:
            current_cost, u = heapq.heappop(pq)

            if u.lower() == end_node.lower():
                break

            if current_cost > distances.get(u, float("inf")):
                continue

            for edge in self.adjacency_list.get(u, []):
                v = edge.target
                base_weight = edge.travel_time_minutes if use_fastest else edge.distance_km
                
                # Apply penalty for alternate route calculation
                if edge.road_id in penalized_road_ids:
                    base_weight *= 3.0

                new_cost = current_cost + base_weight
                if new_cost < distances.get(v, float("inf")):
                    distances[v] = new_cost
                    previous_node[v] = u
                    previous_edge[v] = edge
                    heapq.heappush(pq, (new_cost, v))

        if end_node not in distances or distances[end_node] == float("inf"):
            return PathResult(source=start_node, destination=end_node, reachable=False)

        # Reconstruct path backwards
        node_path: List[str] = []
        edge_path: List[Edge] = []
        curr = end_node
        node_path.append(curr)

        total_dist = 0.0
        total_time = 0.0

        while curr in previous_node:
            edge = previous_edge[curr]
            edge_path.append(edge)
            total_dist += edge.distance_km
            total_time += edge.travel_time_minutes
            curr = previous_node[curr]
            node_path.append(curr)

        node_path.reverse()
        edge_path.reverse()

        return PathResult(
            source=start_node,
            destination=end_node,
            node_path=node_path,
            edge_path=edge_path,
            total_distance_km=round(total_dist, 2),
            total_time_minutes=round(total_time, 2),
            reachable=True
        )

from dataclasses import dataclass
from app.algorithm.dijkstra import DijkstraGraph

@dataclass
class MockRoad:
    id: int
    name: str
    source: str
    destination: str
    distance: float
    speed_limit: float
    traffic_level: str
    road_status: str
    two_way: bool = True

    @property
    def estimated_travel_time_minutes(self) -> float:
        if self.road_status == "CLOSED" or self.speed_limit <= 0:
            return float("inf")
        mults = {"LOW": 1.0, "MEDIUM": 1.3, "HIGH": 1.8, "HEAVY": 2.5}
        mult = mults.get(self.traffic_level, 1.0)
        return round((self.distance / self.speed_limit) * mult * 60.0, 2)

def test_dijkstra_shortest_path():
    graph = DijkstraGraph()
    # Path 1: A -> B -> C (5 + 5 = 10km)
    road1 = MockRoad(1, "A-B", "A", "B", 5.0, 50.0, "LOW", "OPEN")
    road2 = MockRoad(2, "B-C", "B", "C", 5.0, 50.0, "HEAVY", "OPEN")
    # Path 2: A -> C direct (12km)
    road3 = MockRoad(3, "A-C", "A", "C", 12.0, 60.0, "LOW", "OPEN")

    graph.add_road(road1)
    graph.add_road(road2)
    graph.add_road(road3)

    result = graph.find_optimal_path("A", "C", use_fastest=False)
    assert result.reachable is True
    assert result.total_distance_km == 10.0
    assert result.node_path == ["A", "B", "C"]

def test_dijkstra_fastest_path_with_congestion():
    graph = DijkstraGraph()
    # Path 1: A -> B -> C
    # A->B: 5km @ 50km/h (LOW 1.0x) = 6 mins
    # B->C: 5km @ 50km/h (HEAVY 2.5x) = 15 mins
    # Total time = 21 mins (distance = 10km)
    road1 = MockRoad(1, "A-B", "A", "B", 5.0, 50.0, "LOW", "OPEN")
    road2 = MockRoad(2, "B-C", "B", "C", 5.0, 50.0, "HEAVY", "OPEN")

    # Path 2: A -> C direct
    # A->C: 12km @ 60km/h (LOW 1.0x) = 12 mins (distance = 12km)
    road3 = MockRoad(3, "A-C", "A", "C", 12.0, 60.0, "LOW", "OPEN")

    graph.add_road(road1)
    graph.add_road(road2)
    graph.add_road(road3)

    result = graph.find_optimal_path("A", "C", use_fastest=True)
    assert result.reachable is True
    # Fastest route chooses A -> C direct (12 mins vs 21 mins)!
    assert result.total_time_minutes == 12.0
    assert result.node_path == ["A", "C"]

def test_dijkstra_closed_road_bypass():
    graph = DijkstraGraph()
    road1 = MockRoad(1, "A-B", "A", "B", 5.0, 50.0, "LOW", "OPEN")
    road2 = MockRoad(2, "B-C", "B", "C", 5.0, 50.0, "LOW", "OPEN")
    # Direct road is CLOSED
    road3 = MockRoad(3, "A-C", "A", "C", 4.0, 60.0, "LOW", "CLOSED")

    graph.add_road(road1)
    graph.add_road(road2)
    graph.add_road(road3)

    result = graph.find_optimal_path("A", "C", use_fastest=False)
    assert result.reachable is True
    # Bypasses closed road 3, routes via A -> B -> C
    assert result.node_path == ["A", "B", "C"]
    assert result.total_distance_km == 10.0

def test_dijkstra_alternate_route_with_penalties():
    graph = DijkstraGraph()
    road1 = MockRoad(1, "A-B", "A", "B", 5.0, 50.0, "LOW", "OPEN")
    road2 = MockRoad(2, "B-C", "B", "C", 5.0, 50.0, "LOW", "OPEN")
    road3 = MockRoad(3, "A-D", "A", "D", 6.0, 50.0, "LOW", "OPEN")
    road4 = MockRoad(4, "D-C", "D", "C", 6.0, 50.0, "LOW", "OPEN")

    graph.add_road(road1)
    graph.add_road(road2)
    graph.add_road(road3)
    graph.add_road(road4)

    # Primary
    primary = graph.find_optimal_path("A", "C", use_fastest=False)
    assert primary.node_path == ["A", "B", "C"]

    # Alternate penalizing primary road IDs (1, 2)
    penalized = {1, 2}
    alternate = graph.find_optimal_path("A", "C", use_fastest=False, penalized_road_ids=penalized)
    assert alternate.reachable is True
    assert alternate.node_path == ["A", "D", "C"]

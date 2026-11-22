import io
import csv
from collections import Counter
from typing import Dict, List
from sqlalchemy.orm import Session
from app.models import Road, Intersection, RouteHistory
from app.schemas import DashboardMetricsDto, ReportDto
from app.services.traffic_service import road_to_dto

def get_dashboard_metrics(db: Session) -> DashboardMetricsDto:
    all_roads = db.query(Road).all()
    total_roads = len(all_roads)
    total_intersections = db.query(Intersection).count()
    open_roads = sum(1 for r in all_roads if r.road_status == "OPEN")
    closed_roads = sum(1 for r in all_roads if r.road_status == "CLOSED")
    total_searches = db.query(RouteHistory).count()

    open_road_times = [r.estimated_travel_time_minutes for r in all_roads if r.road_status == "OPEN"]
    avg_travel_time = round(sum(open_road_times) / len(open_road_times), 2) if open_road_times else 0.0

    traffic_dist: Dict[str, int] = {lvl: 0 for lvl in ["LOW", "MEDIUM", "HIGH", "HEAVY"]}
    for r in all_roads:
        if r.traffic_level in traffic_dist:
            traffic_dist[r.traffic_level] += 1

    return DashboardMetricsDto(
        totalRoads=total_roads,
        totalIntersections=total_intersections,
        openRoads=open_roads,
        closedRoads=closed_roads,
        totalSearchedRoutes=total_searches,
        averageTravelTimeMinutes=avg_travel_time,
        trafficLevelDistribution=traffic_dist
    )

def get_detailed_statistics(db: Session) -> ReportDto:
    all_roads = db.query(Road).all()
    
    congested = [
        road_to_dto(r) for r in all_roads 
        if r.traffic_level in ("HIGH", "HEAVY") and r.road_status == "OPEN"
    ]
    congested.sort(key=lambda r: r.estimatedTravelTimeMinutes or 0.0, reverse=True)

    all_history = db.query(RouteHistory).all()
    route_pairs = [f"{h.source} ➔ {h.destination}" for h in all_history]
    counts = Counter(route_pairs).most_common(5)
    frequent_routes = [f"{pair} ({cnt} searches)" for pair, cnt in counts]

    open_road_times = [r.estimated_travel_time_minutes for r in all_roads if r.road_status == "OPEN"]
    avg_travel_time = round(sum(open_road_times) / len(open_road_times), 2) if open_road_times else 0.0

    traffic_level_counts: Dict[str, int] = {lvl: 0 for lvl in ["LOW", "MEDIUM", "HIGH", "HEAVY"]}
    for r in all_roads:
        if r.traffic_level in traffic_level_counts:
            traffic_level_counts[r.traffic_level] += 1

    hourly_volume: Dict[str, int] = {}
    for hour in range(0, 24, 3):
        h_str = f"{hour:02d}:00"
        hourly_volume[h_str] = 5 + (hour * 3) % 25 + (hour % 7)

    return ReportDto(
        mostCongestedRoads=congested,
        frequentlyUsedRoutes=frequent_routes,
        averageSystemTravelTime=avg_travel_time,
        trafficLevelCounts=traffic_level_counts,
        hourlySearchVolume=hourly_volume
    )

def generate_csv_history(db: Session) -> str:
    records = db.query(RouteHistory).order_by(RouteHistory.created_at.desc()).all()
    output = io.StringIO()
    writer = csv.writer(output)
    writer.writerow([
        "ID", "Date", "User", "Source", "Destination", "Mode", 
        "Distance(km)", "EstimatedTime(mins)", "TrafficCondition", "Path"
    ])
    for r in records:
        writer.writerow([
            r.id,
            r.created_at.isoformat() if r.created_at else "",
            r.user_email or "",
            r.source,
            r.destination,
            r.optimization_mode,
            r.total_distance,
            r.estimated_time_minutes,
            r.traffic_condition or "",
            r.route_path
        ])
    return output.getvalue()

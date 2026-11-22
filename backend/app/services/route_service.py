from typing import Optional, List, Dict
from sqlalchemy.orm import Session
from app.algorithm.dijkstra import DijkstraGraph, PathResult
from app.models import Road, Intersection, RouteHistory, User
from app.schemas import RouteSearchRequest, RouteResponse, RouteSegmentDto, RouteHistoryDto

def resolve_node_code(db: Session, identifier: str) -> str:
    node = db.query(Intersection).filter(
        (Intersection.code == identifier) | (Intersection.name == identifier)
    ).first()
    return node.code if node else identifier

def build_route_response(db: Session, path_result: PathResult, mode: str) -> RouteResponse:
    intersections = db.query(Intersection).all()
    code_to_name: Dict[str, str] = {i.code: i.name for i in intersections}
    
    node_names = [code_to_name.get(code, code) for code in path_result.node_path]
    segments: List[RouteSegmentDto] = []
    total_weighted_mult = 0.0

    traffic_multipliers = {"LOW": 1.0, "MEDIUM": 1.3, "HIGH": 1.8, "HEAVY": 2.5}

    for edge in path_result.edge_path:
        from_name = code_to_name.get(edge.source, edge.source)
        to_name = code_to_name.get(edge.target, edge.target)
        seg = RouteSegmentDto(
            roadName=edge.road_name,
            fromNode=from_name,
            toNode=to_name,
            distanceKm=edge.distance_km,
            speedLimitKmh=edge.speed_limit,
            travelTimeMinutes=edge.travel_time_minutes,
            trafficLevel=edge.traffic_level
        )
        segments.append(seg)
        mult = traffic_multipliers.get(edge.traffic_level, 1.0)
        total_weighted_mult += mult * edge.distance_km

    avg_mult = (total_weighted_mult / path_result.total_distance_km) if path_result.total_distance_km > 0 else 1.0
    traffic_score = round(avg_mult, 2)

    if avg_mult < 1.15:
        overall_condition = "Clear Traffic (Smooth)"
    elif avg_mult < 1.5:
        overall_condition = "Moderate Traffic"
    elif avg_mult < 2.0:
        overall_condition = "Heavy Congestion"
    else:
        overall_condition = "Severe Gridlock"

    return RouteResponse(
        source=path_result.source,
        destination=path_result.destination,
        optimizationMode=mode,
        nodePath=node_names,
        segments=segments,
        totalDistanceKm=path_result.total_distance_km,
        estimatedTimeMinutes=path_result.total_time_minutes,
        totalIntersections=len(path_result.node_path),
        overallTrafficCondition=overall_condition,
        trafficScore=traffic_score,
        reachable=True,
        alternateRoute=None
    )

def find_optimal_route(db: Session, request: RouteSearchRequest, current_user: Optional[User] = None) -> RouteResponse:
    source_code = resolve_node_code(db, request.source)
    dest_code = resolve_node_code(db, request.destination)

    if source_code.lower() == dest_code.lower():
        return RouteResponse(
            source=source_code,
            destination=dest_code,
            optimizationMode=request.optimizationMode or "FASTEST",
            reachable=False
        )

    all_roads = db.query(Road).all()
    graph = DijkstraGraph()
    for road in all_roads:
        graph.add_road(road)

    use_fastest = (request.optimizationMode or "FASTEST").upper() != "SHORTEST"

    # Primary Dijkstra Execution
    primary_result = graph.find_optimal_path(source_code, dest_code, use_fastest=use_fastest)

    if not primary_result.reachable:
        return RouteResponse(
            source=source_code,
            destination=dest_code,
            optimizationMode="FASTEST" if use_fastest else "SHORTEST",
            reachable=False
        )

    primary_response = build_route_response(
        db, primary_result, "FASTEST" if use_fastest else "SHORTEST"
    )

    # Alternate Route Execution by penalizing primary route edges
    primary_road_ids = {edge.road_id for edge in primary_result.edge_path}
    alternate_result = graph.find_optimal_path(
        source_code, dest_code, use_fastest=use_fastest, penalized_road_ids=primary_road_ids
    )

    if alternate_result.reachable and alternate_result.node_path != primary_result.node_path:
        alt_response = build_route_response(
            db, alternate_result, "FASTEST (ALT)" if use_fastest else "SHORTEST (ALT)"
        )
        primary_response.alternateRoute = alt_response

    # Save to history if user is authenticated
    if current_user:
        history_record = RouteHistory(
            user_id=current_user.id,
            user_email=current_user.email,
            source=source_code,
            destination=dest_code,
            optimization_mode=primary_response.optimizationMode,
            route_path=" -> ".join(primary_response.nodePath),
            total_distance=primary_response.totalDistanceKm,
            estimated_time_minutes=primary_response.estimatedTimeMinutes,
            traffic_condition=primary_response.overallTrafficCondition
        )
        db.add(history_record)
        db.commit()

    return primary_response

def get_user_history(db: Session, user_id: int) -> List[RouteHistory]:
    return db.query(RouteHistory).filter(RouteHistory.user_id == user_id).order_by(RouteHistory.created_at.desc()).all()

def get_all_history(db: Session) -> List[RouteHistory]:
    return db.query(RouteHistory).order_by(RouteHistory.created_at.desc()).all()

def delete_history_item(db: Session, history_id: int, user_id: int, is_admin: bool) -> bool:
    record = db.query(RouteHistory).filter(RouteHistory.id == history_id).first()
    if not record:
        return False
    if not is_admin and record.user_id != user_id:
        return False
    db.delete(record)
    db.commit()
    return True

def clear_user_history(db: Session, user_id: int):
    db.query(RouteHistory).filter(RouteHistory.user_id == user_id).delete()
    db.commit()

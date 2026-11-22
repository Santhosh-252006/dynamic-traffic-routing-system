import random
from typing import List, Optional
from sqlalchemy.orm import Session
from app.models import Road, TrafficLog, AuditLog
from app.schemas import RoadDto, TrafficUpdateDto

TRAFFIC_LEVELS = ["LOW", "MEDIUM", "HIGH", "HEAVY"]

def road_to_dto(road: Road) -> RoadDto:
    return RoadDto(
        id=road.id,
        name=road.name,
        source=road.source,
        destination=road.destination,
        distance=road.distance,
        speedLimit=road.speed_limit,
        trafficLevel=road.traffic_level,
        roadStatus=road.road_status,
        twoWay=road.two_way,
        estimatedTravelTimeMinutes=road.estimated_travel_time_minutes
    )

def update_traffic(db: Session, road_id: int, update_dto: TrafficUpdateDto, user_email: Optional[str] = "ADMIN") -> Optional[RoadDto]:
    road = db.query(Road).filter(Road.id == road_id).first()
    if not road:
        return None

    if update_dto.trafficLevel:
        road.traffic_level = update_dto.trafficLevel
    if update_dto.roadStatus:
        road.road_status = update_dto.roadStatus

    db.commit()
    db.refresh(road)

    # Log to TrafficLog and AuditLog
    t_log = TrafficLog(
        road_id=road.id,
        road_name=road.name,
        traffic_level=road.traffic_level,
        road_status=road.road_status
    )
    a_log = AuditLog(
        user_email=user_email or "SYSTEM",
        action="TRAFFIC_UPDATED",
        details=f"Updated road {road.name} (ID: {road.id}) to Traffic: {road.traffic_level}, Status: {road.road_status}"
    )
    db.add(t_log)
    db.add(a_log)
    db.commit()

    return road_to_dto(road)

def trigger_simulation(db: Session, user_email: Optional[str] = "SYSTEM") -> List[RoadDto]:
    roads = db.query(Road).all()
    if not roads:
        return []

    count_to_modify = min(len(roads), random.randint(3, 5))
    selected_roads = random.sample(roads, count_to_modify)

    for road in selected_roads:
        new_level = random.choice(TRAFFIC_LEVELS)
        road.traffic_level = new_level
        db.add(TrafficLog(
            road_id=road.id,
            road_name=road.name,
            traffic_level=road.traffic_level,
            road_status=road.road_status
        ))

    db.add(AuditLog(
        user_email=user_email or "SYSTEM",
        action="SIMULATION_TRIGGERED",
        details=f"Automated traffic simulation flux triggered across {count_to_modify} corridors."
    ))
    db.commit()

    return [road_to_dto(r) for r in roads]

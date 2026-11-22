from typing import List
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.database import get_db
from app.models import Road, Intersection, TrafficLog, AuditLog, User
from app.schemas import RoadDto, IntersectionDto
from app.security import require_admin, get_current_user_optional
from app.services.traffic_service import road_to_dto

router = APIRouter(tags=["Road Network API"])

@router.get("/roads", response_model=List[RoadDto])
def get_all_roads(db: Session = Depends(get_db)):
    roads = db.query(Road).all()
    return [road_to_dto(r) for r in roads]

@router.get("/intersections", response_model=List[IntersectionDto])
def get_all_intersections(db: Session = Depends(get_db)):
    nodes = db.query(Intersection).all()
    return [
        IntersectionDto(
            id=n.id,
            code=n.code,
            name=n.name,
            xCoord=n.x_coord,
            yCoord=n.y_coord,
            latitude=n.latitude,
            longitude=n.longitude
        ) for n in nodes
    ]

@router.get("/roads/{road_id}", response_model=RoadDto)
def get_road(road_id: int, db: Session = Depends(get_db)):
    road = db.query(Road).filter(Road.id == road_id).first()
    if not road:
        raise HTTPException(status_code=404, detail=f"Road not found with id: {road_id}")
    return road_to_dto(road)

@router.post("/roads", response_model=RoadDto)
def create_road(
    road_dto: RoadDto, 
    db: Session = Depends(get_db), 
    admin: User = Depends(require_admin)
):
    road = Road(
        name=road_dto.name,
        source=road_dto.source,
        destination=road_dto.destination,
        distance=road_dto.distance,
        speed_limit=road_dto.speedLimit,
        traffic_level=road_dto.trafficLevel or "LOW",
        road_status=road_dto.roadStatus or "OPEN",
        two_way=road_dto.twoWay if road_dto.twoWay is not None else True
    )
    db.add(road)
    db.commit()
    db.refresh(road)

    db.add(TrafficLog(
        road_id=road.id,
        road_name=road.name,
        traffic_level=road.traffic_level,
        road_status=road.road_status
    ))
    db.add(AuditLog(
        user_email=admin.email,
        action="ROAD_CREATED",
        details=f"Created road: {road.name} ({road.source} -> {road.destination})"
    ))
    db.commit()

    return road_to_dto(road)

@router.put("/roads/{road_id}", response_model=RoadDto)
def update_road(
    road_id: int, 
    road_dto: RoadDto, 
    db: Session = Depends(get_db), 
    admin: User = Depends(require_admin)
):
    road = db.query(Road).filter(Road.id == road_id).first()
    if not road:
        raise HTTPException(status_code=404, detail=f"Road not found with id: {road_id}")

    road.name = road_dto.name
    road.source = road_dto.source
    road.destination = road_dto.destination
    road.distance = road_dto.distance
    road.speed_limit = road_dto.speedLimit
    if road_dto.trafficLevel:
        road.traffic_level = road_dto.trafficLevel
    if road_dto.roadStatus:
        road.road_status = road_dto.roadStatus
    if road_dto.twoWay is not None:
        road.two_way = road_dto.twoWay

    db.commit()
    db.refresh(road)

    db.add(TrafficLog(
        road_id=road.id,
        road_name=road.name,
        traffic_level=road.traffic_level,
        road_status=road.road_status
    ))
    db.add(AuditLog(
        user_email=admin.email,
        action="ROAD_UPDATED",
        details=f"Updated road ID: {road_id} ({road.name})"
    ))
    db.commit()

    return road_to_dto(road)

@router.delete("/roads/{road_id}", status_code=204)
def delete_road(
    road_id: int, 
    db: Session = Depends(get_db), 
    admin: User = Depends(require_admin)
):
    road = db.query(Road).filter(Road.id == road_id).first()
    if not road:
        raise HTTPException(status_code=404, detail=f"Road not found with id: {road_id}")

    db.delete(road)
    db.add(AuditLog(
        user_email=admin.email,
        action="ROAD_DELETED",
        details=f"Deleted road ID: {road_id} ({road.name})"
    ))
    db.commit()
    return None

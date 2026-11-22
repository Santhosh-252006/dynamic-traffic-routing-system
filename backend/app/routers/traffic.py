from typing import List
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.database import get_db
from app.models import Road, User
from app.schemas import RoadDto, TrafficUpdateDto
from app.security import require_admin
from app.services.traffic_service import road_to_dto, update_traffic as svc_update_traffic, trigger_simulation as svc_simulate

router = APIRouter(prefix="/traffic", tags=["Traffic Management API"])

@router.get("", response_model=List[RoadDto])
def get_traffic_status(db: Session = Depends(get_db)):
    roads = db.query(Road).all()
    return [road_to_dto(r) for r in roads]

@router.put("/{road_id}", response_model=RoadDto)
def update_traffic(
    road_id: int, 
    dto: TrafficUpdateDto, 
    db: Session = Depends(get_db), 
    admin: User = Depends(require_admin)
):
    updated = svc_update_traffic(db, road_id, dto, admin.email)
    if not updated:
        raise HTTPException(status_code=404, detail=f"Road not found with id: {road_id}")
    return updated

@router.post("/simulate", response_model=List[RoadDto])
def simulate_traffic(
    db: Session = Depends(get_db), 
    admin: User = Depends(require_admin)
):
    return svc_simulate(db, admin.email)

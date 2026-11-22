from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.database import get_db
from app.models import User
from app.schemas import RouteSearchRequest, RouteResponse, RouteHistoryDto
from app.security import get_current_user, get_current_user_optional
from app.services.route_service import (
    find_optimal_route as svc_find_route,
    get_user_history,
    get_all_history,
    delete_history_item as svc_delete_history,
    clear_user_history as svc_clear_history
)

router = APIRouter(prefix="/routes", tags=["Route Optimization API"])

@router.post("/find", response_model=RouteResponse)
def find_route(
    request: RouteSearchRequest,
    db: Session = Depends(get_db),
    current_user: Optional[User] = Depends(get_current_user_optional)
):
    if not request.source or not request.destination:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Source and Destination are required."
        )
    return svc_find_route(db, request, current_user)

@router.get("/history", response_model=List[RouteHistoryDto])
def get_history(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    is_admin = current_user.role == "ROLE_ADMIN"
    records = get_all_history(db) if is_admin else get_user_history(db, current_user.id)
    return [
        RouteHistoryDto(
            id=r.id,
            userId=r.user_id,
            userEmail=r.user_email,
            source=r.source,
            destination=r.destination,
            optimizationMode=r.optimization_mode,
            routePath=r.route_path,
            totalDistance=r.total_distance,
            estimatedTimeMinutes=r.estimated_time_minutes,
            trafficCondition=r.traffic_condition,
            createdAt=r.created_at
        ) for r in records
    ]

@router.delete("/history/{history_id}", status_code=204)
def delete_history_item(
    history_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    is_admin = current_user.role == "ROLE_ADMIN"
    success = svc_delete_history(db, history_id, current_user.id, is_admin)
    if not success:
        raise HTTPException(
            status_code=400,
            detail="Could not delete history item (not found or unauthorized)"
        )
    return None

@router.delete("/history/clear", status_code=204)
def clear_history(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    svc_clear_history(db, current_user.id)
    return None

from fastapi import APIRouter, Depends, Response
from sqlalchemy.orm import Session
from app.database import get_db
from app.schemas import DashboardMetricsDto, ReportDto
from app.services.report_service import (
    get_dashboard_metrics,
    get_detailed_statistics,
    generate_csv_history
)

router = APIRouter(prefix="/reports", tags=["Reports & Analytics API"])

@router.get("/dashboard", response_model=DashboardMetricsDto)
def dashboard_metrics(db: Session = Depends(get_db)):
    return get_dashboard_metrics(db)

@router.get("/statistics", response_model=ReportDto)
def statistics_report(db: Session = Depends(get_db)):
    return get_detailed_statistics(db)

@router.get("/export/csv")
def export_csv(db: Session = Depends(get_db)):
    csv_data = generate_csv_history(db)
    return Response(
        content=csv_data,
        media_type="text/csv",
        headers={
            "Content-Disposition": "attachment; filename=route_history_export.csv"
        }
    )

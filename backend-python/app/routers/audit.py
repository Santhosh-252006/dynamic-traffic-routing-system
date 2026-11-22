from typing import List
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from app.database import get_db
from app.models import AuditLog, User
from app.schemas import AuditLogDto
from app.security import require_admin

router = APIRouter(prefix="/admin/audit-logs", tags=["Admin Audit Logs API"])

@router.get("", response_model=List[AuditLogDto])
def get_audit_logs(
    db: Session = Depends(get_db),
    admin: User = Depends(require_admin)
):
    logs = db.query(AuditLog).order_by(AuditLog.timestamp.desc()).all()
    return [
        AuditLogDto(
            id=log.id,
            userEmail=log.user_email,
            action=log.action,
            details=log.details,
            timestamp=log.timestamp
        ) for log in logs
    ]

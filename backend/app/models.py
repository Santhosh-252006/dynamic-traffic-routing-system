import datetime
from sqlalchemy import Column, Integer, String, Float, Boolean, DateTime
from app.database import Base

TRAFFIC_MULTIPLIERS = {
    "LOW": 1.0,
    "MEDIUM": 1.3,
    "HIGH": 1.8,
    "HEAVY": 2.5
}

class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(255), nullable=False)
    email = Column(String(255), unique=True, index=True, nullable=False)
    hashed_password = Column(String(255), nullable=False)
    role = Column(String(50), nullable=False, default="ROLE_USER")
    created_at = Column(DateTime, default=datetime.datetime.utcnow)

class Intersection(Base):
    __tablename__ = "intersections"

    id = Column(Integer, primary_key=True, index=True)
    code = Column(String(50), unique=True, index=True, nullable=False)
    name = Column(String(255), nullable=False)
    x_coord = Column(Float, nullable=False)
    y_coord = Column(Float, nullable=False)
    latitude = Column(Float, nullable=False)
    longitude = Column(Float, nullable=False)

class Road(Base):
    __tablename__ = "roads"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(255), nullable=False)
    source = Column(String(50), nullable=False, index=True)
    destination = Column(String(50), nullable=False, index=True)
    distance = Column(Float, nullable=False)
    speed_limit = Column(Float, nullable=False)
    traffic_level = Column(String(50), nullable=False, default="LOW")  # LOW, MEDIUM, HIGH, HEAVY
    road_status = Column(String(50), nullable=False, default="OPEN")   # OPEN, CLOSED
    two_way = Column(Boolean, default=True)
    updated_time = Column(DateTime, default=datetime.datetime.utcnow, onupdate=datetime.datetime.utcnow)

    @property
    def estimated_travel_time_minutes(self) -> float:
        if self.road_status == "CLOSED" or self.speed_limit <= 0:
            return float("inf")
        mult = TRAFFIC_MULTIPLIERS.get(self.traffic_level, 1.0)
        base_time_hours = self.distance / self.speed_limit
        adjusted_hours = base_time_hours * mult
        return round(adjusted_hours * 60.0, 2)

class RouteHistory(Base):
    __tablename__ = "routes_history"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, nullable=False)
    user_email = Column(String(255), nullable=True)
    source = Column(String(100), nullable=False)
    destination = Column(String(100), nullable=False)
    optimization_mode = Column(String(50), nullable=False)  # FASTEST or SHORTEST
    route_path = Column(String(2000), nullable=False)
    total_distance = Column(Float, nullable=False)
    estimated_time_minutes = Column(Float, nullable=False)
    traffic_condition = Column(String(100), nullable=True)
    created_at = Column(DateTime, default=datetime.datetime.utcnow)

class TrafficLog(Base):
    __tablename__ = "traffic_logs"

    id = Column(Integer, primary_key=True, index=True)
    road_id = Column(Integer, nullable=False)
    road_name = Column(String(255), nullable=True)
    traffic_level = Column(String(50), nullable=False)
    road_status = Column(String(50), nullable=True)
    updated_time = Column(DateTime, default=datetime.datetime.utcnow)

class AuditLog(Base):
    __tablename__ = "audit_logs"

    id = Column(Integer, primary_key=True, index=True)
    user_email = Column(String(255), nullable=True)
    action = Column(String(100), nullable=False)
    details = Column(String(1000), nullable=False)
    timestamp = Column(DateTime, default=datetime.datetime.utcnow)

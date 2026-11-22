from typing import Optional, List, Dict, Any
from datetime import datetime
from pydantic import BaseModel, EmailStr, Field, ConfigDict

# --- Auth Schemas ---
class RegisterRequest(BaseModel):
    name: str
    email: str
    password: str
    role: Optional[str] = "ROLE_USER"

class LoginRequest(BaseModel):
    email: str
    password: str

class UserDto(BaseModel):
    model_config = ConfigDict(populate_by_name=True, from_attributes=True)
    id: int
    name: str
    email: str
    role: str

class AuthResponse(BaseModel):
    model_config = ConfigDict(populate_by_name=True)
    accessToken: str
    tokenType: str = "Bearer"
    user: UserDto

# --- Intersection Schemas ---
class IntersectionDto(BaseModel):
    model_config = ConfigDict(populate_by_name=True, from_attributes=True)
    id: Optional[int] = None
    code: str
    name: str
    xCoord: float = Field(alias="xCoord")
    yCoord: float = Field(alias="yCoord")
    latitude: float
    longitude: float

# --- Road Schemas ---
class RoadDto(BaseModel):
    model_config = ConfigDict(populate_by_name=True, from_attributes=True)
    id: Optional[int] = None
    name: str
    source: str
    destination: str
    distance: float
    speedLimit: float = Field(alias="speedLimit")
    trafficLevel: str = Field(alias="trafficLevel", default="LOW")
    roadStatus: str = Field(alias="roadStatus", default="OPEN")
    twoWay: bool = Field(alias="twoWay", default=True)
    estimatedTravelTimeMinutes: Optional[float] = Field(alias="estimatedTravelTimeMinutes", default=None)

class TrafficUpdateDto(BaseModel):
    model_config = ConfigDict(populate_by_name=True)
    trafficLevel: Optional[str] = Field(default=None, alias="trafficLevel")
    roadStatus: Optional[str] = Field(default=None, alias="roadStatus")

# --- Route Optimization Schemas ---
class RouteSearchRequest(BaseModel):
    model_config = ConfigDict(populate_by_name=True)
    source: str
    destination: str
    optimizationMode: Optional[str] = Field(default="FASTEST", alias="optimizationMode")

class RouteSegmentDto(BaseModel):
    model_config = ConfigDict(populate_by_name=True)
    roadName: str
    fromNode: str
    toNode: str
    distanceKm: float
    speedLimitKmh: float
    travelTimeMinutes: float
    trafficLevel: str

class RouteResponse(BaseModel):
    model_config = ConfigDict(populate_by_name=True)
    source: str
    destination: str
    optimizationMode: str
    nodePath: List[str] = Field(default_factory=list)
    segments: List[RouteSegmentDto] = Field(default_factory=list)
    totalDistanceKm: float = 0.0
    estimatedTimeMinutes: float = 0.0
    totalIntersections: int = 0
    overallTrafficCondition: Optional[str] = None
    trafficScore: float = 1.0
    reachable: bool = False
    alternateRoute: Optional[Any] = None

class RouteHistoryDto(BaseModel):
    model_config = ConfigDict(populate_by_name=True, from_attributes=True)
    id: int
    userId: int = Field(alias="userId")
    userEmail: Optional[str] = Field(default=None, alias="userEmail")
    source: str
    destination: str
    optimizationMode: str = Field(alias="optimizationMode")
    routePath: str = Field(alias="routePath")
    totalDistance: float = Field(alias="totalDistance")
    estimatedTimeMinutes: float = Field(alias="estimatedTimeMinutes")
    trafficCondition: Optional[str] = Field(default=None, alias="trafficCondition")
    createdAt: Optional[datetime] = Field(default=None, alias="createdAt")

# --- Dashboard & Report Schemas ---
class DashboardMetricsDto(BaseModel):
    model_config = ConfigDict(populate_by_name=True)
    totalRoads: int
    totalIntersections: int
    openRoads: int
    closedRoads: int
    totalSearchedRoutes: int
    averageTravelTimeMinutes: float
    trafficLevelDistribution: Dict[str, int]

class ReportDto(BaseModel):
    model_config = ConfigDict(populate_by_name=True)
    mostCongestedRoads: List[RoadDto]
    frequentlyUsedRoutes: List[str]
    averageSystemTravelTime: float
    trafficLevelCounts: Dict[str, int]
    hourlySearchVolume: Dict[str, int]

class AuditLogDto(BaseModel):
    model_config = ConfigDict(populate_by_name=True, from_attributes=True)
    id: int
    userEmail: Optional[str] = Field(default=None, alias="userEmail")
    action: str
    details: str
    timestamp: datetime

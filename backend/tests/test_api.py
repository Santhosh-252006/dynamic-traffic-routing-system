import pytest
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.pool import StaticPool
import app.models
from app.database import Base, get_db
from app.main import app
from app.seed import seed_database

# Use in-memory SQLite with StaticPool for testing
TEST_DB_URL = "sqlite:///:memory:"
test_engine = create_engine(
    TEST_DB_URL, 
    connect_args={"check_same_thread": False},
    poolclass=StaticPool
)
TestingSessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=test_engine)

Base.metadata.create_all(bind=test_engine)

def override_get_db():
    db = TestingSessionLocal()
    try:
        yield db
    finally:
        db.close()

app.dependency_overrides[get_db] = override_get_db

# Seed test database
db = TestingSessionLocal()
seed_database(db)
db.close()

client = TestClient(app)

def test_root_endpoint():
    response = client.get("/")
    assert response.status_code == 200
    assert response.json()["status"] == "online"

def test_login_demo_admin():
    response = client.post("/api/auth/login", json={
        "email": "admin@traffic.com",
        "password": "Admin@123"
    })
    assert response.status_code == 200
    data = response.json()
    assert "accessToken" in data
    assert data["user"]["role"] == "ROLE_ADMIN"

def test_get_intersections():
    response = client.get("/api/intersections")
    assert response.status_code == 200
    nodes = response.json()
    assert len(nodes) >= 12
    assert any(n["code"] == "INT-01" for n in nodes)

def test_get_roads():
    response = client.get("/api/roads")
    assert response.status_code == 200
    roads = response.json()
    assert len(roads) >= 21

def test_find_route_fastest():
    response = client.post("/api/routes/find", json={
        "source": "INT-01",
        "destination": "INT-04",
        "optimizationMode": "FASTEST"
    })
    assert response.status_code == 200
    data = response.json()
    assert data["reachable"] is True
    assert data["totalDistanceKm"] > 0
    assert len(data["nodePath"]) >= 2
    assert "segments" in data

def test_dashboard_metrics():
    response = client.get("/api/reports/dashboard")
    assert response.status_code == 200
    data = response.json()
    assert data["totalRoads"] >= 21
    assert data["totalIntersections"] >= 12
    assert "trafficLevelDistribution" in data

def test_export_csv():
    response = client.get("/api/reports/export/csv")
    assert response.status_code == 200
    assert "text/csv" in response.headers["content-type"]
    assert "Distance(km)" in response.text

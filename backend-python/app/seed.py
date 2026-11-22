from sqlalchemy.orm import Session
from app.models import User, Intersection, Road
from app.security import get_password_hash

def seed_database(db: Session):
    # Seed Users
    if db.query(User).count() == 0:
        admin_user = User(
            name="System Administrator",
            email="admin@traffic.com",
            hashed_password=get_password_hash("Admin@123"),
            role="ROLE_ADMIN"
        )
        commuter_user = User(
            name="John Commuter",
            email="user@traffic.com",
            hashed_password=get_password_hash("User@123"),
            role="ROLE_USER"
        )
        db.add_all([admin_user, commuter_user])
        db.commit()

    # Seed Intersections (Nodes)
    if db.query(Intersection).count() == 0:
        nodes = [
            Intersection(code="INT-01", name="Downtown Hub", x_coord=120, y_coord=140, latitude=40.7128, longitude=-74.0060),
            Intersection(code="INT-02", name="Central Station", x_coord=320, y_coord=110, latitude=40.7180, longitude=-74.0000),
            Intersection(code="INT-03", name="Financial District", x_coord=540, y_coord=140, latitude=40.7220, longitude=-73.9920),
            Intersection(code="INT-04", name="Tech Park", x_coord=780, y_coord=170, latitude=40.7280, longitude=-73.9800),
            Intersection(code="INT-05", name="River Bridge West", x_coord=220, y_coord=310, latitude=40.7050, longitude=-74.0100),
            Intersection(code="INT-06", name="City Center Circle", x_coord=440, y_coord=290, latitude=40.7100, longitude=-73.9980),
            Intersection(code="INT-07", name="University Campus", x_coord=660, y_coord=310, latitude=40.7150, longitude=-73.9850),
            Intersection(code="INT-08", name="East Gate Plaza", x_coord=860, y_coord=330, latitude=40.7200, longitude=-73.9700),
            Intersection(code="INT-09", name="West End Blvd", x_coord=160, y_coord=470, latitude=40.6950, longitude=-74.0200),
            Intersection(code="INT-10", name="Harbor Port", x_coord=370, y_coord=490, latitude=40.6980, longitude=-74.0050),
            Intersection(code="INT-11", name="South Suburbs", x_coord=600, y_coord=470, latitude=40.7020, longitude=-73.9900),
            Intersection(code="INT-12", name="Airport Junction", x_coord=820, y_coord=500, latitude=40.7080, longitude=-73.9750),
        ]
        db.add_all(nodes)
        db.commit()

    # Seed Road Corridors (Edges)
    if db.query(Road).count() == 0:
        roads = [
            # Northern Expressway
            Road(name="Downtown Avenue", source="INT-01", destination="INT-02", distance=3.2, speed_limit=60.0, traffic_level="LOW", road_status="OPEN", two_way=True),
            Road(name="Station Parkway", source="INT-02", destination="INT-03", distance=2.8, speed_limit=50.0, traffic_level="MEDIUM", road_status="OPEN", two_way=True),
            Road(name="Innovation Way", source="INT-03", destination="INT-04", distance=4.5, speed_limit=70.0, traffic_level="LOW", road_status="OPEN", two_way=True),

            # Central Arterials
            Road(name="River Crossing Rd", source="INT-01", destination="INT-05", distance=2.5, speed_limit=45.0, traffic_level="HIGH", road_status="OPEN", two_way=True),
            Road(name="Central Metro Link", source="INT-02", destination="INT-06", distance=3.0, speed_limit=55.0, traffic_level="HEAVY", road_status="OPEN", two_way=True),
            Road(name="Campus Drive", source="INT-03", destination="INT-07", distance=3.5, speed_limit=50.0, traffic_level="LOW", road_status="OPEN", two_way=True),
            Road(name="Tech Connector", source="INT-04", destination="INT-08", distance=2.2, speed_limit=60.0, traffic_level="LOW", road_status="OPEN", two_way=True),

            # Mid-City Beltway
            Road(name="Bridge Link West", source="INT-05", destination="INT-06", distance=3.8, speed_limit=65.0, traffic_level="MEDIUM", road_status="OPEN", two_way=True),
            Road(name="University Avenue", source="INT-06", destination="INT-07", distance=3.1, speed_limit=50.0, traffic_level="LOW", road_status="OPEN", two_way=True),
            Road(name="East Beltway", source="INT-07", destination="INT-08", distance=3.4, speed_limit=60.0, traffic_level="HIGH", road_status="OPEN", two_way=True),

            # Southern Expressway & Coast
            Road(name="Coastal Highway", source="INT-05", destination="INT-09", distance=2.9, speed_limit=55.0, traffic_level="LOW", road_status="OPEN", two_way=True),
            Road(name="Harbor Connector", source="INT-06", destination="INT-10", distance=3.6, speed_limit=60.0, traffic_level="MEDIUM", road_status="OPEN", two_way=True),
            Road(name="Suburban Link", source="INT-07", destination="INT-11", distance=4.0, speed_limit=65.0, traffic_level="LOW", road_status="OPEN", two_way=True),
            Road(name="Airport Expressway", source="INT-08", destination="INT-12", distance=5.2, speed_limit=90.0, traffic_level="LOW", road_status="OPEN", two_way=True),

            # Southern Arterial
            Road(name="Port Boulevard", source="INT-09", destination="INT-10", distance=3.3, speed_limit=50.0, traffic_level="LOW", road_status="OPEN", two_way=True),
            Road(name="Southern Parkway", source="INT-10", destination="INT-11", distance=3.7, speed_limit=60.0, traffic_level="HIGH", road_status="OPEN", two_way=True),
            Road(name="Airport Link South", source="INT-11", destination="INT-12", distance=4.1, speed_limit=75.0, traffic_level="MEDIUM", road_status="OPEN", two_way=True),

            # Cross Corridors
            Road(name="Grand Diagonal", source="INT-01", destination="INT-06", distance=4.8, speed_limit=65.0, traffic_level="LOW", road_status="OPEN", two_way=True),
            Road(name="Metro Crossway", source="INT-06", destination="INT-12", distance=7.5, speed_limit=80.0, traffic_level="MEDIUM", road_status="OPEN", two_way=True),
            Road(name="Suburban Bypass", source="INT-09", destination="INT-06", distance=5.0, speed_limit=55.0, traffic_level="LOW", road_status="OPEN", two_way=True),
            Road(name="Harbor Express", source="INT-10", destination="INT-12", distance=6.8, speed_limit=85.0, traffic_level="LOW", road_status="OPEN", two_way=True),
        ]
        db.add_all(roads)
        db.commit()

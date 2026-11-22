# Dynamic Traffic Routing System

[![Python](https://img.shields.io/badge/Python-3.11+-3776AB?style=for-the-badge&logo=python&logoColor=white)](https://www.python.org/)
[![FastAPI](https://img.shields.io/badge/FastAPI-009688?style=for-the-badge&logo=fastapi&logoColor=white)](https://fastapi.tiangolo.com/)
[![React](https://img.shields.io/badge/React-18-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)](https://react.dev/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)](https://tailwindcss.com/)
[![SQLite](https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white)](https://sqlite.org/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

A full-stack, enterprise-grade **Dynamic Traffic Routing System** built with **Python 3.11+ (FastAPI + SQLAlchemy)**, **React 18 + Vite + Tailwind CSS**, **Dijkstra's Algorithm**, and **JWT Authentication**.

The system simulates real-time city traffic conditions, calculates optimal **Shortest** and **Fastest** routes, supports dynamic road closure handling, renders an interactive Canvas/SVG traffic map with animated vehicle particle flows and heatmaps, and provides analytics reports.

---

## 🚀 Key Features

1. **Graph-Based Route Optimization (Dijkstra's Algorithm)**:
   - **Shortest Route**: Minimized physical distance ($\text{km}$).
   - **Fastest Route**: Minimized travel time ($\text{mins}$) factoring in distance, speed limit ($\text{km/h}$), and dynamic traffic congestion multipliers.
   - **Closed Road Handling**: Closed corridors are assigned infinite weight ($\infty$) and automatically bypassed by Dijkstra.
   - **Alternate Routes**: Penalty-based alternate path finder.

2. **Real-Time Traffic Simulation & Controls**:
   - Automated simulation engine fluctuating congestion levels across 12 intersections and 21 interconnecting road corridors every 5 seconds.
   - Live route recalculation on traffic flux.
   - Traffic heatmap overlay highlighting congestion density.

3. **Interactive Graph Map Engine**:
   - Custom Canvas/SVG interactive graph visualizer.
   - Color-coded roads (`LOW` = Emerald Green, `MEDIUM` = Amber, `HIGH` = Orange, `HEAVY` = Crimson Red, `CLOSED` = Dashed Gray).
   - Animated vehicle traffic streams moving along open roads.
   - Clickable nodes to select Source and Destination.

4. **Authentication & Security (RBAC + JWT)**:
   - User registration and login with BCrypt password encryption.
   - Role-Based Access Control (`ROLE_ADMIN` and `ROLE_USER`).
   - Admin audit logging for security governance.

5. **Analytics, Export & History**:
   - Recharts visual charts (Congestion distribution pie chart, travel time bar chart, 24-hour volume line chart).
   - Export route history as downloadable CSV file.

---

## 🛠 Technology Stack

- **Backend**: Python 3.11+, FastAPI, SQLAlchemy, Pydantic v2, Python-Jose (JWT), Passlib (BCrypt), Uvicorn.
- **Frontend**: React 18, Vite, Tailwind CSS, Lucide Icons, Recharts, Axios.
- **Database**: SQLite (Default zero-dependency mode: `traffic.db`) / PostgreSQL 16 (Docker Compose mode).
- **API Specs**: OpenAPI 3.0 / Swagger UI (`/docs`) & ReDoc (`/redoc`).
- **Deployment**: Docker & Docker Compose multi-stage builds.

---

## 🔑 Demo Credentials

| Role | Email | Password | Access Rights |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin@traffic.com` | `Admin@123` | Full access: Add/Edit/Delete roads, status toggle, simulation controls, audit logs |
| **User** | `user@traffic.com` | `User@123` | Route optimization search, history, analytics, CSV exports |

---

## 🧮 Dijkstra Algorithm Mathematical Formula

### 1. Distance Weight ($W_{\text{dist}}$)
$$W_{\text{dist}} = \text{Road Distance (km)}$$

### 2. Travel Time Weight ($W_{\text{time}}$)
$$\text{Effective Speed} = \frac{\text{Speed Limit (km/h)}}{\text{Traffic Multiplier}}$$

$$\text{Traffic Multiplier} = \begin{cases} 
1.0 & \text{if LOW (Smooth)} \\ 
1.3 & \text{if MEDIUM (Moderate)} \\ 
1.8 & \text{if HIGH (Heavy)} \\ 
2.5 & \text{if HEAVY (Gridlock)} \\ 
\infty & \text{if CLOSED} 
\end{cases}$$

$$\text{Travel Time (minutes)} = \left( \frac{\text{Distance}}{\text{Effective Speed}} \right) \times 60$$

---

## 📁 Project Structure

```
dynamic-traffic-routing-system/
├── backend/                        # Python FastAPI Backend
│   ├── app/
│   │   ├── algorithm/              # Core Dijkstra Algorithm implementation
│   │   ├── routers/                # REST API Routers (Auth, Roads, Traffic, Routes, Reports, Audit)
│   │   ├── services/               # Business logic & Dijkstra route orchestration
│   │   ├── config.py               # Environment & JWT settings
│   │   ├── database.py             # SQLAlchemy session & engine
│   │   ├── models.py               # Database ORM models
│   │   ├── schemas.py              # Pydantic v2 schemas / DTOs
│   │   ├── security.py             # JWT bearer auth & BCrypt hashing
│   │   ├── seed.py                 # Initial seed dataset (12 intersections, 21 roads)
│   │   └── main.py                 # FastAPI application entrypoint
│   ├── tests/                      # Automated Pytest test suite
│   │   ├── test_dijkstra.py        # Dijkstra unit tests
│   │   └── test_api.py             # REST API integration tests
│   ├── Dockerfile                  # Python Docker container configuration
│   └── requirements.txt            # Python dependencies
├── frontend/                       # React 18 + Vite Frontend
│   ├── src/
│   │   ├── api/                    # Axios API client with JWT interceptors
│   │   ├── components/             # InteractiveMap, Navbar, Sidebar, Toast
│   │   ├── context/                # AuthContext & TrafficContext
│   │   ├── views/                  # Dashboard, RoutePlanner, RoadManagement, Analytics, History
│   │   ├── App.jsx                 # Routing & Layout
│   │   └── main.jsx
│   ├── package.json
│   └── Dockerfile
├── docker-compose.yml              # PostgreSQL + Python FastAPI + React Nginx setup
├── schema.sql                      # Standalone SQL DDL Schema
├── data.sql                        # Standalone SQL Seed Data
└── Dynamic_Traffic_Routing_System.postman_collection.json
```

---

## ⚡ How to Build & Run Locally

### Option A: Standard Dev Server (Zero Dependency - SQLite)

#### 1. Start Python FastAPI Backend:
```bash
cd backend
python -m venv venv
# On Windows:
venv\Scripts\activate
# On Linux/Mac:
source venv/bin/activate

pip install -r requirements.txt
python -m uvicorn app.main:app --host 0.0.0.0 --port 8085 --reload
```
- Backend API: `http://localhost:8085`
- Interactive Swagger UI: `http://localhost:8085/docs`
- ReDoc Docs: `http://localhost:8085/redoc`

#### 2. Start React Frontend:
```bash
cd frontend
npm install
npm run dev
```
- Frontend UI: `http://localhost:3000`

---

### Option B: Docker Compose (FastAPI + PostgreSQL 16 + React Nginx)

Run the entire full-stack system with PostgreSQL via Docker Compose:
```bash
docker-compose up --build -d
```
- Access Frontend UI at `http://localhost:3000`
- Access Backend API at `http://localhost:8085`

---

## 🧪 Running Unit Tests

To verify Dijkstra route calculations and REST API endpoints:
```bash
cd backend
python -m pytest -v
```

---

## 📡 Key REST API Endpoints

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register new user account | Public |
| `POST` | `/api/auth/login` | Authenticate user & return JWT | Public |
| `GET` | `/api/roads` | Fetch all road corridors | Public |
| `GET` | `/api/intersections` | Fetch graph nodes/intersections | Public |
| `POST` | `/api/routes/find` | Calculate Dijkstra route (Shortest/Fastest) | Public / Bearer JWT |
| `GET` | `/api/routes/history` | Get route calculation history | Bearer JWT |
| `PUT` | `/api/traffic/{roadId}` | Update road traffic level / status | Admin Only |
| `POST` | `/api/traffic/simulate` | Trigger real-time traffic flux | Admin Only |
| `GET` | `/api/reports/dashboard` | Fetch high-level stat metrics | Public |
| `GET` | `/api/reports/export/csv` | Download route history CSV | Public |

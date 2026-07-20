# Dynamic Traffic Routing System

A full-stack, enterprise-grade **Dynamic Traffic Routing System** built with **Java 21 + Spring Boot 3.2+**, **React 18 + Vite + Tailwind CSS**, **Dijkstra's Algorithm**, and **Spring Security JWT Authentication**.

The system simulates real-time city traffic conditions, calculates optimal **Shortest** and **Fastest** routes, supports dynamic road closure handling, renders an interactive Canvas/SVG traffic map with animated vehicle particle flows and heatmaps, and provides analytics reports.

---

## 🚀 Key Features

1. **Graph-Based Route Optimization (Dijkstra's Algorithm)**:
   - **Shortest Route**: Minimized distance ($\text{km}$).
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

- **Backend**: Java 21, Spring Boot 3.2.3, Spring Data JPA, Spring Security, JJWT (0.12.5), Maven.
- **Frontend**: React 18, Vite, Tailwind CSS, Lucide Icons, Recharts, Axios.
- **Database**: H2 In-Memory DB (Default zero-dependency mode) / PostgreSQL 16 (Docker Compose mode).
- **API Specs**: OpenAPI 3.0 / Swagger UI.
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
├── backend/                        # Spring Boot Java 21 Backend
│   ├── src/main/java/com/traffic/
│   │   ├── algorithm/              # Core Dijkstra Algorithm implementation
│   │   ├── config/                 # Security, Web CORS, Swagger, DatabaseInitializer
│   │   ├── controller/             # REST Controllers (Auth, Road, Traffic, Route, Reports)
│   │   ├── dto/                    # Request/Response Data Transfer Objects
│   │   ├── exception/              # Global Exception Handler & Custom Errors
│   │   ├── model/                  # JPA Entities (User, Road, Intersection, RouteHistory, AuditLog)
│   │   ├── repository/             # JPA Data Repositories
│   │   ├── security/               # JWT Token Provider, Filters, UserDetails
│   │   └── service/                # Business & Optimization Logic Services
│   ├── src/test/java/              # JUnit 5 & Mockito Unit Tests
│   ├── pom.xml                     # Maven Dependencies
│   └── Dockerfile                  # Multi-stage Java Dockerfile
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
├── docker-compose.yml              # PostgreSQL + Spring Boot + Nginx Compose setup
├── schema.sql                      # Standalone SQL DDL Schema
├── data.sql                        # Standalone SQL Seed Data
└── Dynamic_Traffic_Routing_System.postman_collection.json
```

---

## ⚡ How to Build & Run Locally

### Option A: Standard Dev Server (Zero Dependency - Embedded H2)

#### 1. Start Spring Boot Backend:
```bash
cd backend
mvn spring-boot:run
```
- Backend API will start at: `http://localhost:8080`
- Swagger OpenAPI Specs: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:trafficdb`, Username: `sa`, Password: *empty*)

#### 2. Start React Frontend:
```bash
cd frontend
npm install
npm run dev
```
- Frontend UI will start at: `http://localhost:3000`

---

### Option B: Docker Compose (Spring Boot + PostgreSQL 16 + React Nginx)

Run the entire full-stack system with PostgreSQL via Docker Compose:
```bash
docker-compose up --build -d
```
- Access Frontend UI at `http://localhost:3000`
- Access Backend API at `http://localhost:8080`

---

## 🧪 Running Unit Tests

To verify Dijkstra route calculation unit tests:
```bash
cd backend
mvn clean test
```

---

## 📡 Key REST API Endpoints

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register new user account | Public |
| `POST` | `/api/auth/login` | Authenticate user & return JWT | Public |
| `GET` | `/api/roads` | Fetch all road corridors | Public |
| `GET` | `/api/intersections` | Fetch graph nodes/intersections | Public |
| `POST` | `/api/routes/find` | Calculate Dijkstra route (Shortest/Fastest) | Bearer JWT |
| `GET` | `/api/routes/history` | Get route calculation history | Bearer JWT |
| `PUT` | `/api/traffic/{roadId}` | Update road traffic level / status | Admin Only |
| `POST` | `/api/traffic/simulate` | Trigger real-time traffic flux | Admin Only |
| `GET` | `/api/reports/dashboard` | Fetch high-level stat metrics | Public |
| `GET` | `/api/reports/export/csv` | Download route history CSV | Public |

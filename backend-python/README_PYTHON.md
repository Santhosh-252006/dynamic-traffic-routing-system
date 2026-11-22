# Dynamic Traffic Routing System (Python Backend)

A high-performance Python implementation of the Dynamic Traffic Routing System backend using **FastAPI**, **SQLAlchemy**, **Pydantic v2**, and **Dijkstra's Algorithm**.

---

## 🚀 Features

- **Dijkstra Route Optimization**: Shortest Path & Fastest Path factoring dynamic congestion multipliers (`LOW`: 1.0x, `MEDIUM`: 1.3x, `HIGH`: 1.8x, `HEAVY`: 2.5x).
- **Incident & Road Closure Handling**: Closed roads are automatically assigned $\infty$ weight and bypassed.
- **Alternate Route Discovery**: Penalty-based secondary path search.
- **Authentication**: JWT token bearer authorization with BCrypt password hashing.
- **Swagger / OpenAPI**: Interactive API docs at `http://localhost:8085/docs`.
- **Zero-Config Database**: Embedded SQLite (`traffic.db`) out-of-the-box, easily switchable to PostgreSQL.
- **Frontend Compatible**: Fully drop-in compatible with the React 18 + Vite frontend at `http://localhost:3000`.

---

## 🛠 Local Setup & Running

### 1. Install Dependencies
```bash
cd backend-python
python -m venv venv
venv\Scripts\activate       # On Windows
# or source venv/bin/activate on Linux/Mac
pip install -r requirements.txt
```

### 2. Run the FastAPI Server
```bash
python -m uvicorn app.main:app --host 0.0.0.0 --port 8085 --reload
```
- API Server: `http://localhost:8085`
- Swagger UI Docs: `http://localhost:8085/docs`
- ReDoc: `http://localhost:8085/redoc`

### 3. Run Automated Tests
```bash
pytest -v
```

---

## 🔑 Pre-Seeded Demo Credentials

| Role | Email | Password |
| :--- | :--- | :--- |
| **Admin** | `admin@traffic.com` | `Admin@123` |
| **Commuter** | `user@traffic.com` | `User@123` |

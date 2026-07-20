-- Dynamic Traffic Routing System SQL Schema Definition

DROP TABLE IF EXISTS audit_logs CASCADE;
DROP TABLE IF EXISTS traffic_logs CASCADE;
DROP TABLE IF EXISTS routes_history CASCADE;
DROP TABLE IF EXISTS roads CASCADE;
DROP TABLE IF EXISTS intersections CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Users Table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Intersections Table (Graph Nodes)
CREATE TABLE intersections (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    x_coord DOUBLE PRECISION NOT NULL,
    y_coord DOUBLE PRECISION NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL
);

-- Roads Table (Graph Edges)
CREATE TABLE roads (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    source VARCHAR(50) NOT NULL,
    destination VARCHAR(50) NOT NULL,
    distance DOUBLE PRECISION NOT NULL,
    speed_limit DOUBLE PRECISION NOT NULL,
    traffic_level VARCHAR(50) NOT NULL,
    road_status VARCHAR(50) NOT NULL,
    two_way BOOLEAN DEFAULT TRUE,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Route History Table
CREATE TABLE routes_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    user_email VARCHAR(255),
    source VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    optimization_mode VARCHAR(50) NOT NULL,
    route_path VARCHAR(2000) NOT NULL,
    total_distance DOUBLE PRECISION NOT NULL,
    estimated_time_minutes DOUBLE PRECISION NOT NULL,
    traffic_condition VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Traffic Logs Table
CREATE TABLE traffic_logs (
    id BIGSERIAL PRIMARY KEY,
    road_id BIGINT NOT NULL,
    road_name VARCHAR(255),
    traffic_level VARCHAR(50) NOT NULL,
    road_status VARCHAR(50),
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Audit Logs Table
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_email VARCHAR(255),
    action VARCHAR(100) NOT NULL,
    details VARCHAR(1000) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

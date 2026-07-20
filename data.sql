-- Dynamic Traffic Routing System Initial Seed Data

-- Insert Users (Password: Admin@123 -> BCrypt: $2a$10$wT0Xp749a2g7W2k/R3jI0.r20fW0hK97a7/V4G7A8e8R4a9b6c1d2, User@123 -> $2a$10$3zE2h700/v4p44H3E2h/8.y1a2B3c4D5e6F7g8H9i0J1k2L3m4N5)
INSERT INTO users (name, email, password, role) VALUES
('System Administrator', 'admin@traffic.com', '$2a$10$wT0Xp749a2g7W2k/R3jI0.r20fW0hK97a7/V4G7A8e8R4a9b6c1d2', 'ROLE_ADMIN'),
('John Commuter', 'user@traffic.com', '$2a$10$3zE2h700/v4p44H3E2h/8.y1a2B3c4D5e6F7g8H9i0J1k2L3m4N5', 'ROLE_USER');

-- Insert Intersections
INSERT INTO intersections (code, name, x_coord, y_coord, latitude, longitude) VALUES
('INT-01', 'Downtown Hub', 120, 140, 40.7128, -74.0060),
('INT-02', 'Central Station', 320, 110, 40.7180, -74.0000),
('INT-03', 'Financial District', 540, 140, 40.7220, -73.9920),
('INT-04', 'Tech Park', 780, 170, 40.7280, -73.9800),
('INT-05', 'River Bridge West', 220, 310, 40.7050, -74.0100),
('INT-06', 'City Center Circle', 440, 290, 40.7100, -73.9980),
('INT-07', 'University Campus', 660, 310, 40.7150, -73.9850),
('INT-08', 'East Gate Plaza', 860, 330, 40.7200, -73.9700),
('INT-09', 'West End Blvd', 160, 470, 40.6950, -74.0200),
('INT-10', 'Harbor Port', 370, 490, 40.6980, -74.0050),
('INT-11', 'South Suburbs', 600, 470, 40.7020, -73.9900),
('INT-12', 'Airport Junction', 820, 500, 40.7080, -73.9750);

-- Insert Roads
INSERT INTO roads (name, source, destination, distance, speed_limit, traffic_level, road_status, two_way) VALUES
('Downtown Avenue', 'INT-01', 'INT-02', 3.2, 60, 'LOW', 'OPEN', true),
('Station Parkway', 'INT-02', 'INT-03', 2.8, 50, 'MEDIUM', 'OPEN', true),
('Innovation Way', 'INT-03', 'INT-04', 4.5, 70, 'LOW', 'OPEN', true),
('River Crossing Rd', 'INT-01', 'INT-05', 2.5, 45, 'HIGH', 'OPEN', true),
('Central Metro Link', 'INT-02', 'INT-06', 3.0, 55, 'HEAVY', 'OPEN', true),
('Campus Drive', 'INT-03', 'INT-07', 3.5, 50, 'LOW', 'OPEN', true),
('Tech Connector', 'INT-04', 'INT-08', 2.2, 60, 'LOW', 'OPEN', true),
('Bridge Link West', 'INT-05', 'INT-06', 3.8, 65, 'MEDIUM', 'OPEN', true),
('University Avenue', 'INT-06', 'INT-07', 3.1, 50, 'LOW', 'OPEN', true),
('East Beltway', 'INT-07', 'INT-08', 3.4, 60, 'HIGH', 'OPEN', true),
('Coastal Highway', 'INT-05', 'INT-09', 2.9, 55, 'LOW', 'OPEN', true),
('Harbor Connector', 'INT-06', 'INT-10', 3.6, 60, 'MEDIUM', 'OPEN', true),
('Suburban Link', 'INT-07', 'INT-11', 4.0, 65, 'LOW', 'OPEN', true),
('Airport Expressway', 'INT-08', 'INT-12', 5.2, 90, 'LOW', 'OPEN', true),
('Port Boulevard', 'INT-09', 'INT-10', 3.3, 50, 'LOW', 'OPEN', true),
('Southern Parkway', 'INT-10', 'INT-11', 3.7, 60, 'HIGH', 'OPEN', true),
('Airport Link South', 'INT-11', 'INT-12', 4.1, 75, 'MEDIUM', 'OPEN', true),
('Grand Diagonal', 'INT-01', 'INT-06', 4.8, 65, 'LOW', 'OPEN', true),
('Metro Crossway', 'INT-06', 'INT-12', 7.5, 80, 'MEDIUM', 'OPEN', true),
('Suburban Bypass', 'INT-09', 'INT-06', 5.0, 55, 'LOW', 'OPEN', true),
('Harbor Express', 'INT-10', 'INT-12', 6.8, 85, 'LOW', 'OPEN', true);

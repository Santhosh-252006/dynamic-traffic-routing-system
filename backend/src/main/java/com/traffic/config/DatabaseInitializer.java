package com.traffic.config;

import com.traffic.model.*;
import com.traffic.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IntersectionRepository intersectionRepository;

    @Autowired
    private RoadRepository roadRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedUsers();
        seedIntersections();
        seedRoads();
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            User admin = new User("System Administrator", "admin@traffic.com", passwordEncoder.encode("Admin@123"), Role.ROLE_ADMIN);
            User user = new User("John Commuter", "user@traffic.com", passwordEncoder.encode("User@123"), Role.ROLE_USER);

            userRepository.saveAll(List.of(admin, user));
            logger.info("Database initialized with default Admin (admin@traffic.com) and User (user@traffic.com).");
        }
    }

    private void seedIntersections() {
        if (intersectionRepository.count() == 0) {
            List<Intersection> nodes = Arrays.asList(
                new Intersection("INT-01", "Downtown Hub", 120, 140, 40.7128, -74.0060),
                new Intersection("INT-02", "Central Station", 320, 110, 40.7180, -74.0000),
                new Intersection("INT-03", "Financial District", 540, 140, 40.7220, -73.9920),
                new Intersection("INT-04", "Tech Park", 780, 170, 40.7280, -73.9800),
                new Intersection("INT-05", "River Bridge West", 220, 310, 40.7050, -74.0100),
                new Intersection("INT-06", "City Center Circle", 440, 290, 40.7100, -73.9980),
                new Intersection("INT-07", "University Campus", 660, 310, 40.7150, -73.9850),
                new Intersection("INT-08", "East Gate Plaza", 860, 330, 40.7200, -73.9700),
                new Intersection("INT-09", "West End Blvd", 160, 470, 40.6950, -74.0200),
                new Intersection("INT-10", "Harbor Port", 370, 490, 40.6980, -74.0050),
                new Intersection("INT-11", "South Suburbs", 600, 470, 40.7020, -73.9900),
                new Intersection("INT-12", "Airport Junction", 820, 500, 40.7080, -73.9750)
            );
            intersectionRepository.saveAll(nodes);
            logger.info("Database initialized with 12 Smart City Intersections.");
        }
    }

    private void seedRoads() {
        if (roadRepository.count() == 0) {
            List<Road> roads = Arrays.asList(
                // Corridor 1: Northern Expressway
                new Road("Downtown Avenue", "INT-01", "INT-02", 3.2, 60, TrafficLevel.LOW, RoadStatus.OPEN, true),
                new Road("Station Parkway", "INT-02", "INT-03", 2.8, 50, TrafficLevel.MEDIUM, RoadStatus.OPEN, true),
                new Road("Innovation Way", "INT-03", "INT-04", 4.5, 70, TrafficLevel.LOW, RoadStatus.OPEN, true),

                // Corridor 2: Central Arterial
                new Road("River Crossing Rd", "INT-01", "INT-05", 2.5, 45, TrafficLevel.HIGH, RoadStatus.OPEN, true),
                new Road("Central Metro Link", "INT-02", "INT-06", 3.0, 55, TrafficLevel.HEAVY, RoadStatus.OPEN, true),
                new Road("Campus Drive", "INT-03", "INT-07", 3.5, 50, TrafficLevel.LOW, RoadStatus.OPEN, true),
                new Road("Tech Connector", "INT-04", "INT-08", 2.2, 60, TrafficLevel.LOW, RoadStatus.OPEN, true),

                // Corridor 3: Mid-City Beltway
                new Road("Bridge Link West", "INT-05", "INT-06", 3.8, 65, TrafficLevel.MEDIUM, RoadStatus.OPEN, true),
                new Road("University Avenue", "INT-06", "INT-07", 3.1, 50, TrafficLevel.LOW, RoadStatus.OPEN, true),
                new Road("East Beltway", "INT-07", "INT-08", 3.4, 60, TrafficLevel.HIGH, RoadStatus.OPEN, true),

                // Corridor 4: Southern Expressway & Coast
                new Road("Coastal Highway", "INT-05", "INT-09", 2.9, 55, TrafficLevel.LOW, RoadStatus.OPEN, true),
                new Road("Harbor Connector", "INT-06", "INT-10", 3.6, 60, TrafficLevel.MEDIUM, RoadStatus.OPEN, true),
                new Road("Suburban Link", "INT-07", "INT-11", 4.0, 65, TrafficLevel.LOW, RoadStatus.OPEN, true),
                new Road("Airport Expressway", "INT-08", "INT-12", 5.2, 90, TrafficLevel.LOW, RoadStatus.OPEN, true),

                // Corridor 5: Southern Arterial
                new Road("Port Boulevard", "INT-09", "INT-10", 3.3, 50, TrafficLevel.LOW, RoadStatus.OPEN, true),
                new Road("Southern Parkway", "INT-10", "INT-11", 3.7, 60, TrafficLevel.HIGH, RoadStatus.OPEN, true),
                new Road("Airport Link South", "INT-11", "INT-12", 4.1, 75, TrafficLevel.MEDIUM, RoadStatus.OPEN, true),

                // Diagonal / Cross Highways
                new Road("Grand Diagonal", "INT-01", "INT-06", 4.8, 65, TrafficLevel.LOW, RoadStatus.OPEN, true),
                new Road("Metro Crossway", "INT-06", "INT-12", 7.5, 80, TrafficLevel.MEDIUM, RoadStatus.OPEN, true),
                new Road("Suburban Bypass", "INT-09", "INT-06", 5.0, 55, TrafficLevel.LOW, RoadStatus.OPEN, true),
                new Road("Harbor Express", "INT-10", "INT-12", 6.8, 85, TrafficLevel.LOW, RoadStatus.OPEN, true)
            );
            roadRepository.saveAll(roads);
            logger.info("Database initialized with 21 Smart City Road Edges.");
        }
    }
}

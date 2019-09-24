package net.joedoe.traffictracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrafficTrackerApp {

    public static void main(String[] args) {
        SpringApplication.run(TrafficTrackerApp.class, args);
    }
}

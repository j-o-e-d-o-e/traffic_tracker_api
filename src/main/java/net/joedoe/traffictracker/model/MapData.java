package net.joedoe.traffictracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class MapData {
    private List<CurrentFlight> flights;
    private boolean updated;

    @Data
    @AllArgsConstructor
    public static class CurrentFlight {
        private LocalDateTime date;
        private String icao;
        private int altitude;
        private int speed;
        private double longitude;
        private double latitude;
        private boolean updated;

        public CurrentFlight(Flight flight, double longitude, double latitude) {
            this.date = flight.getDate();
            this.icao = flight.getIcao();
            this.altitude = flight.getAltitude();
            this.speed = flight.getSpeed();
            this.longitude = longitude;
            this.latitude = latitude;
            this.updated = true;
        }
    }
}



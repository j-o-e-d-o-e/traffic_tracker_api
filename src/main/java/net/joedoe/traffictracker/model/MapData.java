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
    private List<CurrentPlane> planes;
    private boolean updated;

    @Data
    @AllArgsConstructor
    public static class CurrentPlane {
        private LocalDateTime date;
        private String icao;
        private int altitude;
        private int speed;
        private double longitude;
        private double latitude;
        private boolean updated;

        public CurrentPlane(Plane plane, double longitude, double latitude) {
            this.date = plane.getDate();
            this.icao = plane.getIcao();
            this.altitude = plane.getAltitude();
            this.speed = plane.getSpeed();
            this.longitude = longitude;
            this.latitude = latitude;
            this.updated = true;
        }
    }
}



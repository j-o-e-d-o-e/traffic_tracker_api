package net.joedoe.traffictracker.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Day {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private LocalDate date;
    @Column
    private int total;
    @Column
    private boolean lessThanThirtyPlanes = true;
    @Column
    private int planes23;
    @Column
    private int planes0;
    @Column
    private int avgAltitude;
    @Column
    private int avgSpeed;
    @SuppressWarnings("JpaAttributeTypeInspection")
    @Column
    private int[] hoursPlane = new int[24];
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "day", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Plane> planes = new ArrayList<>();
    @Column
    private float windSpeed;
    @SuppressWarnings("JpaAttributeTypeInspection")
    @Column
    private int[] hoursWind = new int[24];
    @Column
    private int absAltitude;
    @Column
    private int absSpeed;
    @Column
    private int absWind;
    @Column
    private float absWindSpeed;

    public Day(LocalDate date) {
        this.date = date;
    }

    public void addPlane(Plane plane) {
        total += 1;
        lessThanThirtyPlanes = total < 30;
        if (plane.getDate().toLocalTime().isAfter(LocalTime.of(22, 57))) {
            planes23 += 1;
        } else if (plane.getDate().toLocalTime().isBefore(LocalTime.of(5, 45))) {
            planes0 += 1;
        }
        int altitude = plane.getAltitude();
        if (altitude != 0) {
            absAltitude += altitude;
            avgAltitude = absAltitude / total;
        }
        int speed = plane.getSpeed();
        if (speed != 0) {
            absSpeed += speed;
            avgSpeed = absSpeed / total;
        }
        hoursPlane[plane.getDate().getHour()] += 1;
        plane.setDay(this);
        planes.add(plane);
    }

    public void addWind(Wind wind) {
        absWind += 1;
        absWindSpeed += wind.getSpeed();
        windSpeed = Math.round(absWindSpeed / absWind * 100) / 100f;
        hoursWind[wind.getDate().getHour()] = wind.getDeg();
    }

    public void clearPlanes() {
        this.planes.clear();
    }
}

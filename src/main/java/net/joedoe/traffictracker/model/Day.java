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
    @Column
    private int[] hoursPlane = new int[24];
    //    @OneToMany(cascade = {CascadeType.MERGE}, mappedBy = "day", fetch = FetchType.EAGER)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "day", fetch = FetchType.EAGER)
    private List<Plane> planes = new ArrayList<>();
    @Column
    private float windSpeed;
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
        if (plane.getDate().getHour() == 23) {
            planes23 += 1;
        } else if (plane.getDate().toLocalTime().isBefore((LocalTime.of(5, 45)))) {
            planes0 += 1;
        }
        absAltitude += plane.getAltitude();
        avgAltitude = absAltitude / total;
        absSpeed += plane.getSpeed();
        avgSpeed = absSpeed / total;
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
}

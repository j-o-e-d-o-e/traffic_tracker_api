package net.joedoe.traffictracker.ml.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ForecastHourly {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    LocalDateTime date;
    @Column
    boolean lessThanThreePlanes;
    @Column
    int deg;

    public ForecastHourly(LocalDateTime date, boolean lessThanThreePlanes, int deg) {
        this.date = date;
        this.lessThanThreePlanes = lessThanThreePlanes;
        this.deg = deg;
    }
}


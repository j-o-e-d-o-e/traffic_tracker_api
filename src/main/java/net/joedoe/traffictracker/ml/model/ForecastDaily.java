package net.joedoe.traffictracker.ml.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class ForecastDaily {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private LocalDate date;
    @Column
    private float lessThanThirtyPlanes;
    @SuppressWarnings("JpaDataSourceORMInspection")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "hour_id", referencedColumnName = "id")
    private List<ForecastHourly> hours = new ArrayList<>();

    public ForecastDaily(LocalDate date) {
        this.date = date;
    }

    public void addForecast(ForecastHourly hour) {
        hours.add(hour);
    }
}

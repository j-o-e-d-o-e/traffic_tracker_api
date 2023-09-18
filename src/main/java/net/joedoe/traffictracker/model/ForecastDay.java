package net.joedoe.traffictracker.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class ForecastDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate date;
    private float probability;
    @SuppressWarnings("JpaDataSourceORMInspection")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "hour_id", referencedColumnName = "id")
    private List<ForecastHour> hours = new ArrayList<>();

    public ForecastDay(LocalDate date) {
        this.date = date;
    }
}

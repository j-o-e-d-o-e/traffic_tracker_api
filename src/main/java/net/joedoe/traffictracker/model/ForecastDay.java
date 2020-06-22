package net.joedoe.traffictracker.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
    @Column
    private LocalDate date;
    @Column
    private float probability;
    @SuppressWarnings("JpaDataSourceORMInspection")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "hour_id", referencedColumnName = "id")
    private List<ForecastHour> hours = new ArrayList<>();
}

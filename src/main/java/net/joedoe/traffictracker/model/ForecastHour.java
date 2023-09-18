package net.joedoe.traffictracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ForecastHour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    LocalTime time;
    @JsonProperty("wind_degree")
    int windDegree;
    @Column
    float probability;

    public ForecastHour(LocalTime time, int windDegree) {
        this.time = time;
        this.windDegree = windDegree;
    }
}

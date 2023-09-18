package net.joedoe.traffictracker.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.joedoe.traffictracker.dto.WindDto;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
public class WindDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    @Basic
    @JdbcTypeCode(SqlTypes.VARBINARY)
    private int[] hoursWind = new int[24];
    private int absWind;
    private float windSpeed;
    private float absWindSpeed;

    public WindDay(LocalDate date) {
        this.date = date;
    }

    public void addWind(WindDto windDto) {
        absWind += 1;
        absWindSpeed += windDto.getSpeed();
        windSpeed = Math.round(absWindSpeed / absWind * 100) / 100f;
        hoursWind[windDto.getDateTime().getHour()] = windDto.getDeg();
    }
}

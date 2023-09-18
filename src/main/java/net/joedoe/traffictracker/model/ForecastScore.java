package net.joedoe.traffictracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@NoArgsConstructor
public class ForecastScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private float precision;
    private float recall;
    @JsonProperty("mean_abs_error")
    private float meanAbsoluteError;
    @Basic
    @JdbcTypeCode(SqlTypes.VARBINARY)
    @JsonProperty("confusion_matrix")
    private int[][] confusionMatrix;
}

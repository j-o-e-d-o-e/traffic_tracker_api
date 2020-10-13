package net.joedoe.traffictracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
public class ForecastScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private float precision;
    @Column
    @JsonProperty("mean_abs_error")
    private float meanAbsoluteError;
    @SuppressWarnings("JpaAttributeTypeInspection")
    @Column
    @JsonProperty("confusion_matrix")
    private int[][] confusionMatrix;
}

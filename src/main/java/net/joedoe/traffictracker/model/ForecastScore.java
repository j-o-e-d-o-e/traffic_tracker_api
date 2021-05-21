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
    private float precision;
    @JsonProperty("mean_abs_error")
    private float meanAbsoluteError;
    @SuppressWarnings("JpaAttributeTypeInspection")
    @JsonProperty("confusion_matrix")
    private int[][] confusionMatrix;
}

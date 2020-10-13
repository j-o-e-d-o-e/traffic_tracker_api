package net.joedoe.traffictracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
public class ForecastScoreDto {
    private float precision;
    private float recall;
    private float mean_abs_error;
    private int[][] confusion_matrix;
}

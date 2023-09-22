package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.ForecastScoreDto;
import net.joedoe.traffictracker.model.ForecastScore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class ForecastScoreMapperTest {

    @Test
    public void toResource() {
        ForecastScore score = new ForecastScore();
        score.setPrecision(87.68f);
        int[][] confusionMatrix = new int[][]{{33, 17}, {9, 121}};
        score.setConfusionMatrix(confusionMatrix);

        ForecastScoreDto act = ForecastScoreMapper.toResource(score);

        assertEquals(87.68f, act.getPrecision(), 0.01f);
    }
}

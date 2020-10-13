package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.ForecastScoreDto;
import net.joedoe.traffictracker.model.ForecastScore;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

@Slf4j
public class ForecastScoreMapperTest {

    @Before
    public void setUp() {
    }

    @Test
    public void toResource() {
        ForecastScore score = new ForecastScore();
        score.setPrecision(87.68f);
        int[][] confusionMatrix = new int[][]{{33, 17}, {9, 121}};
        score.setConfusionMatrix(confusionMatrix);
        float exp = (confusionMatrix[1][1] / (float) (confusionMatrix[1][0] + confusionMatrix[1][1])) * 100;
        exp = Math.round(exp * 100) / 100f;

        ForecastScoreDto act = ForecastScoreMapper.toResource(score);
//        log.info(String.valueOf(act));

        assertEquals(exp, act.getRecall(), 0.01f);
    }
}

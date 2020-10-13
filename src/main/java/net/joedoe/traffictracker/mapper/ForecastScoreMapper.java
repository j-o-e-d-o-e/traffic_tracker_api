package net.joedoe.traffictracker.mapper;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.dto.ForecastScoreDto;
import net.joedoe.traffictracker.model.ForecastScore;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ForecastScoreMapper {

    public static ForecastScoreDto toResource(ForecastScore score) {
        if (score == null) return null;
        float recall = score.getConfusionMatrix()[1][1] / (float) (score.getConfusionMatrix()[1][0] + score.getConfusionMatrix()[1][1]) * 100;
        recall = Math.round(recall * 100) / 100f;
        return new ForecastScoreDto(score.getPrecision(), recall, score.getMeanAbsoluteError(), score.getConfusionMatrix());
    }
}

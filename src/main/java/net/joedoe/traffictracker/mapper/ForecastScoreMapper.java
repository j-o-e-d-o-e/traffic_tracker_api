package net.joedoe.traffictracker.mapper;

import net.joedoe.traffictracker.dto.ForecastScoreDto;
import net.joedoe.traffictracker.model.ForecastScore;
import org.springframework.stereotype.Component;

@Component
public class ForecastScoreMapper {
    public static ForecastScoreDto toResource(ForecastScore score) {
        if (score == null) return null;
        return new ForecastScoreDto(score.getPrecision(), score.getRecall(), score.getMeanAbsoluteError(), score.getConfusionMatrix());
    }
}

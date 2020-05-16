package net.joedoe.traffictracker.ml;

import lombok.extern.slf4j.Slf4j;
import net.joedoe.traffictracker.ml.ForecastClient.DataWrapper;
import net.joedoe.traffictracker.ml.ForecastClient.DataWrapper.Weather;
import net.joedoe.traffictracker.ml.model.ForecastDaily;
import net.joedoe.traffictracker.ml.model.ForecastHourly;
import net.joedoe.traffictracker.repo.ForecastRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class Estimator {
    private ForecastRepository repository;
    private ForecastClient client;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private DecisionTreeClassifier clf = createClf();

    public Estimator(ForecastRepository repository, ForecastClient client) {
        this.repository = repository;
        this.client = client;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void predict() {
        repository.deleteAll();
        DataWrapper input = client.fetch();
        ForecastDaily day = new ForecastDaily(LocalDate.now());
        for (Weather weather : input.list) {
            LocalDateTime dateTime = LocalDateTime.parse(weather.dt_txt, formatter);
            int deg = weather.wind.deg;
            boolean estimation = clf.predict(new double[]{deg}) == 1;
            ForecastHourly hour = new ForecastHourly(dateTime, estimation, deg);
            if (dateTime.getHour() < 6) {
                //noinspection UnnecessaryContinue
                continue;
            } else if (dateTime.toLocalDate().isEqual(day.getDate())) {
                day.addForecast(hour);
            } else if (dateTime.toLocalDate().isAfter(day.getDate())) {
                int hoursSize = day.getHours().size();
                if (hoursSize > 0) {
                    day.setLessThanThirtyPlanes(Math.round(day.getHours()
                            .stream().filter(ForecastHourly::isLessThanThreePlanes)
                            .count() / (float) hoursSize * 100) / 100f);
                    repository.save(day);
                    log.info(day.toString());
                }
                day = new ForecastDaily(dateTime.toLocalDate());
                day.addForecast(hour);
            }
        }
    }

    private static DecisionTreeClassifier createClf() {
        int[] lChilds = {1, 2, 3, 4, 5, -1, 7, 8, -1, 10, -1, -1, -1, 14, -1, -1, 17, 18, 19, 20, 21, 22, 23, -1, -1, -1, -1, -1, 29, -1, 31, -1, -1, -1, 35, -1, 37, 38, -1, 40, -1, 42, -1, 44, 45, -1, -1, 48, -1, -1, -1, 52, 53, 54, -1, 56, -1, 58, 59, 60, 61, 62, -1, -1, -1, -1, 67, -1, -1, -1, 71, -1, -1, 74, -1, 76, -1, -1, 79, 80, 81, 82, -1, 84, -1, 86, 87, 88, 89, -1, 91, -1, 93, -1, 95, -1, -1, 98, -1, 100, 101, -1, -1, -1, 105, 106, -1, -1, -1, 110, -1, -1, 113, -1, -1, 116, 117, 118, 119, -1, -1, 122, -1, -1, -1, 126, 127, 128, -1, -1, 131, -1, 133, -1, -1, -1, 137, 138, 139, 140, 141, 142, -1, -1, -1, 146, -1, 148, 149, -1, -1, -1, 153, -1, 155, -1, -1, -1, 159, 160, -1, 162, 163, -1, 165, -1, -1, -1, -1};
        int[] rChilds = {78, 51, 16, 13, 6, -1, 12, 9, -1, 11, -1, -1, -1, 15, -1, -1, 34, 33, 28, 27, 26, 25, 24, -1, -1, -1, -1, -1, 30, -1, 32, -1, -1, -1, 36, -1, 50, 39, -1, 41, -1, 43, -1, 47, 46, -1, -1, 49, -1, -1, -1, 73, 70, 55, -1, 57, -1, 69, 66, 65, 64, 63, -1, -1, -1, -1, 68, -1, -1, -1, 72, -1, -1, 75, -1, 77, -1, -1, 136, 115, 112, 83, -1, 85, -1, 109, 104, 97, 90, -1, 92, -1, 94, -1, 96, -1, -1, 99, -1, 103, 102, -1, -1, -1, 108, 107, -1, -1, -1, 111, -1, -1, 114, -1, -1, 125, 124, 121, 120, -1, -1, 123, -1, -1, -1, 135, 130, 129, -1, -1, 132, -1, 134, -1, -1, -1, 158, 157, 152, 145, 144, 143, -1, -1, -1, 147, -1, 151, 150, -1, -1, -1, 154, -1, 156, -1, -1, -1, 168, 161, -1, 167, 164, -1, 166, -1, -1, -1, -1};
        double[] thresholds = {118.5, 96.5, 11.5, 9.5, 3.5, -2.0, 7.0, 4.5, -2.0, 5.5, -2.0, -2.0, -2.0, 10.5, -2.0, -2.0, 77.5, 63.5, 58.0, 46.5, 45.5, 23.5, 22.5, -2.0, -2.0, -2.0, -2.0, -2.0, 59.5, -2.0, 62.5, -2.0, -2.0, -2.0, 79.0, -2.0, 94.5, 83.5, -2.0, 84.5, -2.0, 88.5, -2.0, 90.5, 89.5, -2.0, -2.0, 93.5, -2.0, -2.0, -2.0, 112.5, 110.5, 97.5, -2.0, 99.5, -2.0, 109.5, 107.5, 105.5, 103.5, 101.0, -2.0, -2.0, -2.0, -2.0, 108.5, -2.0, -2.0, -2.0, 111.5, -2.0, -2.0, 116.5, -2.0, 117.5, -2.0, -2.0, 327.5, 139.5, 137.5, 120.5, -2.0, 121.5, -2.0, 135.5, 132.5, 128.5, 123.0, -2.0, 124.5, -2.0, 125.5, -2.0, 127.0, -2.0, -2.0, 129.5, -2.0, 131.5, 130.5, -2.0, -2.0, -2.0, 134.5, 133.5, -2.0, -2.0, -2.0, 136.5, -2.0, -2.0, 138.5, -2.0, -2.0, 147.5, 146.5, 142.5, 141.5, -2.0, -2.0, 145.5, -2.0, -2.0, -2.0, 323.0, 149.5, 148.5, -2.0, -2.0, 227.5, -2.0, 228.5, -2.0, -2.0, -2.0, 347.0, 342.0, 336.5, 332.5, 331.0, 329.5, -2.0, -2.0, -2.0, 333.5, -2.0, 335.5, 334.5, -2.0, -2.0, -2.0, 337.5, -2.0, 339.0, -2.0, -2.0, -2.0, 359.5, 349.5, -2.0, 353.5, 350.5, -2.0, 351.5, -2.0, -2.0, -2.0, -2.0};
        int[] indices = {0, 0, 0, 0, 0, -2, 0, 0, -2, 0, -2, -2, -2, 0, -2, -2, 0, 0, 0, 0, 0, 0, 0, -2, -2, -2, -2, -2, 0, -2, 0, -2, -2, -2, 0, -2, 0, 0, -2, 0, -2, 0, -2, 0, 0, -2, -2, 0, -2, -2, -2, 0, 0, 0, -2, 0, -2, 0, 0, 0, 0, 0, -2, -2, -2, -2, 0, -2, -2, -2, 0, -2, -2, 0, -2, 0, -2, -2, 0, 0, 0, 0, -2, 0, -2, 0, 0, 0, 0, -2, 0, -2, 0, -2, 0, -2, -2, 0, -2, 0, 0, -2, -2, -2, 0, 0, -2, -2, -2, 0, -2, -2, 0, -2, -2, 0, 0, 0, 0, -2, -2, 0, -2, -2, -2, 0, 0, 0, -2, -2, 0, -2, 0, -2, -2, -2, 0, 0, 0, 0, 0, 0, -2, -2, -2, 0, -2, 0, 0, -2, -2, -2, 0, -2, 0, -2, -2, -2, 0, 0, -2, 0, 0, -2, 0, -2, -2, -2, -2};
        int[][] classes = {{879, 204}, {23, 142}, {14, 116}, {5, 13}, {2, 11}, {0, 4}, {2, 7}, {2, 4}, {1, 1}, {1, 3}, {0, 1}, {1, 2}, {0, 3}, {3, 2}, {2, 1}, {1, 1}, {9, 103}, {4, 74}, {4, 57}, {2, 53}, {2, 35}, {1, 33}, {1, 17}, {0, 15}, {1, 2}, {0, 16}, {1, 2}, {0, 18}, {2, 4}, {1, 0}, {1, 4}, {0, 2}, {1, 2}, {0, 17}, {5, 29}, {1, 0}, {4, 29}, {4, 23}, {0, 5}, {4, 18}, {1, 0}, {3, 18}, {0, 6}, {3, 12}, {2, 3}, {1, 2}, {1, 1}, {1, 9}, {0, 5}, {1, 4}, {0, 6}, {9, 26}, {7, 16}, {5, 15}, {1, 1}, {4, 14}, {0, 2}, {4, 12}, {4, 10}, {2, 7}, {2, 5}, {1, 4}, {1, 2}, {0, 2}, {1, 1}, {0, 2}, {2, 3}, {1, 1}, {1, 2}, {0, 2}, {2, 1}, {1, 0}, {1, 1}, {2, 10}, {0, 7}, {2, 3}, {2, 2}, {0, 1}, {856, 62}, {841, 38}, {40, 29}, {37, 22}, {3, 0}, {34, 22}, {0, 1}, {34, 21}, {24, 17}, {15, 8}, {5, 4}, {1, 0}, {4, 4}, {0, 1}, {4, 3}, {2, 1}, {2, 2}, {1, 1}, {1, 1}, {10, 4}, {4, 2}, {6, 2}, {4, 1}, {3, 1}, {1, 0}, {2, 1}, {9, 9}, {7, 6}, {3, 3}, {4, 3}, {2, 3}, {10, 4}, {5, 2}, {5, 2}, {3, 7}, {2, 6}, {1, 1}, {801, 9}, {37, 6}, {34, 4}, {9, 2}, {5, 0}, {4, 2}, {25, 2}, {16, 0}, {9, 2}, {3, 2}, {764, 3}, {761, 2}, {16, 1}, {7, 0}, {9, 1}, {745, 1}, {514, 0}, {231, 1}, {8, 1}, {223, 0}, {3, 1}, {15, 24}, {11, 12}, {7, 12}, {6, 6}, {2, 4}, {2, 2}, {1, 2}, {1, 0}, {0, 2}, {4, 2}, {1, 0}, {3, 2}, {2, 2}, {1, 1}, {1, 1}, {1, 0}, {1, 6}, {0, 2}, {1, 4}, {1, 2}, {0, 2}, {4, 0}, {4, 12}, {3, 12}, {0, 5}, {3, 7}, {3, 3}, {1, 2}, {2, 1}, {1, 0}, {1, 1}, {0, 4}, {1, 0}};
        return new DecisionTreeClassifier(lChilds, rChilds, thresholds, indices, classes);
    }
}


package net.joedoe.traffictracker.config;

import net.joedoe.traffictracker.dto.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;

@EnableWebMvc
@Configuration
public class SwaggerConfig {
    public static final String FlightControllerTag = "flight-controller";
    @SuppressWarnings("rawtypes")
    Class[] classes = {DeparturesDto.class, ForecastScoreDto.class, StatsDto.StatsPlane.class,
            TemplateVariable.class, UriTemplate.class, Links.class, LinkRelation.class};

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("net.joedoe.traffictracker"))
                .paths(PathSelectors.any())
                .build()
                .pathMapping("/")
                .tags(new Tag(FlightControllerTag, "Only flights for last 30 days"))
                .ignoredParameterTypes(classes)
                .apiInfo(metaData());
    }

    private ApiInfo metaData() {

        Contact contact = new Contact("Joe Doe", "https://github.com/j-o-e-d-o-e",
                "d_joe@gmx.net");

        return new ApiInfo(
                "Traffic tracker",
                "Tracks planes landing <strong>DUS</strong> airport coming from the east " +
                        "- since <strong>9.9.2019</strong>." +
                        "\n<a href='https://j-o-e-d-o-e.github.io/traffic-tracker'>Angular client</a>" +
                        "\n<a href='https://github.com/j-o-e-d-o-e/traffic_tracker_api'>Github Repo</a>",
                "2.0",
                null,
                contact,
                "Apache License Version 2.0",
                "https://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList<>());
    }
}


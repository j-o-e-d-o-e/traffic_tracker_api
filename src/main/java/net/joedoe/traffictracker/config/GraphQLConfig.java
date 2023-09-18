package net.joedoe.traffictracker.config;

import graphql.analysis.MaxQueryComplexityInstrumentation;
import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.scalars.ExtendedScalars;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLConfig {
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(ExtendedScalars.Date)
                .scalar(ExtendedScalars.Time);
    }
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.graphql.instrumentation", name = "max-query-complexity")
    public MaxQueryComplexityInstrumentation maxQueryComplexityInstrumentation(@Value("${spring.graphql.instrumentation.max-query-complexity}") int maxComplexity) {
        return new MaxQueryComplexityInstrumentation(maxComplexity);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.graphql.instrumentation", name = "max-query-depth")
    public MaxQueryDepthInstrumentation maxQueryDepthInstrumentation(@Value("${spring.graphql.instrumentation.max-query-depth}") int maxDepth) {
        return new MaxQueryDepthInstrumentation(maxDepth);
    }
}

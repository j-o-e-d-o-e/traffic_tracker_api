package net.joedoe.traffictracker.config;

import graphql.ExecutionResult;
import graphql.execution.ExecutionId;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;

@Slf4j
//@Component
@RequiredArgsConstructor
public class GraphQLInterceptor extends SimpleInstrumentation {

    @Override
    public InstrumentationContext<ExecutionResult> beginExecution(InstrumentationExecutionParameters parameters) {
        Instant start = Instant.now();
        ExecutionId executionId = parameters.getExecutionInput().getExecutionId();
        log.info("{}: query: {} with variables: {}", executionId, parameters.getQuery(), parameters.getVariables());
        return SimpleInstrumentationContext.whenCompleted((executionResult, throwable) -> {
            Duration duration = Duration.between(start, Instant.now());
            if (throwable == null) {
                log.info("{}: completed successfully in: {}", executionId, duration);
            } else {
                log.warn("{}: failed in: {}", executionId, duration, throwable);
            }
        });
    }
}

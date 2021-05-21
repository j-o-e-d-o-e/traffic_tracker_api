package net.joedoe.traffictracker.exception;

import graphql.GraphQLException;
import graphql.kickstart.spring.error.ThrowableGraphQLError;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

@Component
public class GraphQLExceptionHandler {

    @ExceptionHandler({GraphQLException.class, ConstraintViolationException.class})
    public ThrowableGraphQLError handleGraphQLException(Exception exception) {
        return new ThrowableGraphQLError(exception);
    }
    @ExceptionHandler(RuntimeException.class)
    public ThrowableGraphQLError handleRuntimeException(RuntimeException exception) {
        return new ThrowableGraphQLError(exception, "Internal Server Error");
    }
}

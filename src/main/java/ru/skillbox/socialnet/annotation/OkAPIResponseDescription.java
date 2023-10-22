package ru.skillbox.socialnet.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
@Operation
@ApiResponse(responseCode = "200", description = "OK")
@Content(mediaType = "application/json")
@Schema(description = "default response from server")
public @interface OkAPIResponseDescription {
    @AliasFor(annotation = Operation.class, attribute = "summary")
    String summary();
    @AliasFor(annotation = Schema.class, attribute = "ref")
    String value();
}

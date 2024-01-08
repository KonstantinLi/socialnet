package com.socialnet.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD})
@Retention(RUNTIME)
@AuthRequired
@BadRequestResponseDescription
@ApiResponse(responseCode = "200")
@Operation
public @interface FullSwaggerDescription {

    @AliasFor(annotation = Operation.class, attribute = "summary")
    String summary();
}

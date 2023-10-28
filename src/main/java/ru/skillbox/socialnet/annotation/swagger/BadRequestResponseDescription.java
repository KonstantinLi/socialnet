package ru.skillbox.socialnet.annotation.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.annotation.AliasFor;
import ru.skillbox.socialnet.dto.response.ErrorRs;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, TYPE})
@Retention(RUNTIME)
@ApiResponse(responseCode = "400")
@Operation
public @interface BadRequestResponseDescription {

    @AliasFor(annotation = ApiResponse.class, attribute = "content")
    Content content() default @Content(schema = @Schema(implementation = ErrorRs.class));

    @AliasFor(annotation = Operation.class, attribute = "summary")
    String summary() default "";
}

package ru.skillbox.socialnet.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import ru.skillbox.socialnet.dto.response.ErrorRs;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Target(TYPE)
@Retention(RUNTIME)
@ApiResponses({@ApiResponse(responseCode = "400", description = "name of error",
        content = {@Content(mediaType = "application/json",
                schema = @Schema(description = "common error response",
                        implementation = ErrorRs.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
public @interface ErrorAPIResponsesDescription {
}

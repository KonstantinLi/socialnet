package ru.skillbox.socialnet.annotation.swagger;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.core.annotation.AliasFor;

@Parameter
@Content(mediaType = "application/json")
@Schema()
public @interface ParameterDescription {
    @AliasFor(annotation = Parameter.class, attribute = "description")
    String description();

    @AliasFor(annotation = Schema.class, attribute = "implementation")
    Class<?> implementation();
}

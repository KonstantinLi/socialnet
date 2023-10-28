package ru.skillbox.socialnet.annotation.swagger;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({PARAMETER})
@Retention(RUNTIME)
@Parameter
public @interface Token {

    @AliasFor(annotation = Parameter.class, attribute = "description")
    String description() default "JWT Token";
}

package ru.skillbox.socialnet;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;


@OpenAPIDefinition(
        info = @Info(
                title = "Zerone API",
                description = "API for social network", version = "1.0.0",
                contact = @Contact(
                        name = "JAVA Pro 41 Group",
                        url = "https://gitlab.skillbox.ru/javapro_team401/javaproteams41backend.git",
                        email = "hello@skillbox.ru"
                )
        )
)
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@ConfigurationPropertiesScan
@EnableScheduling
@Configuration
@EnableAspectJAutoProxy
public class SocialNetApp {

    public static void main(String[] args) {
        SpringApplication.run(SocialNetApp.class, args);
    }

}

package com.socialnet.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8086/");
        devServer.setDescription("localhost");

        Server prodServer = new Server();
        prodServer.setUrl("https://217.107.219.242:8086/");
        prodServer.setDescription("Server");

        Contact contact = new Contact();
        contact.setName("JAVA Pro 41 Group");
        contact.setUrl("http://217.107.219.242:8080");
        contact.setEmail("aaa@aaaa.aa");

        License mitLicense = new License().name("Apache 2.0").url("http://springdoc.org");

        List<Tag> tagList = new ArrayList<>();
        tagList.add(new Tag().name("MessageWsController").description("message WebSocket"));

        Info info = new Info()
                .title("Zerone API")
                .version("1.0")
                .contact(contact)
                .description("API for social network")
                .license(mitLicense);


        String securitySchemeName = "JWTToken";
        return new OpenAPI()
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .type(Type.APIKEY)
                                                .name("Authorization")
                                                .in(In.HEADER)
                                )
                )
                .security(List.of(new SecurityRequirement().addList(securitySchemeName))).info(info).tags(tagList).servers(List.of(devServer, prodServer));
    }

}

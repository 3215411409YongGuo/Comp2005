package com.api.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI maternityOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Maternity Ward API")
                        .description("Maternity Ward API Service for hospital management")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@example.com"))
                        .license(new License().name("API License").url("https://example.com/license")))
                .externalDocs(new ExternalDocumentation()
                        .description("External Maternity API Documentation")
                        .url("https://web.socem.plymouth.ac.uk/COMP2005/api/"));
    }
} 
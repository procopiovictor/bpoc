package com.victorprocopio.bpoc.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI apiDoc() {
    return new OpenAPI()
        .info(new Info()
            .title("Bank POC - Hero API")
            .version("v1")
            .description("POC that proxies a free superhero API.")
            .contact(new Contact().name("Victor Procopio")));
  }
}
package com.victorprocopio.bpoc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hero.api")
public record HeroApiProperties(String baseUrl) {}

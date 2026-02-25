package com.victorprocopio.bpoc.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;

import com.victorprocopio.bpoc.dto.HeroDto;

@Service
public class HeroService {

    private final RestClient heroClient;
    private final Timer heroApiTimer;

    public HeroService(RestClient heroClient, MeterRegistry registry) {
    this.heroClient = heroClient;
    this.heroApiTimer = Timer.builder("hero.api.calls")
        .description("Time spent calling external Hero API")
        .publishPercentileHistogram()
        .publishPercentiles(0.5, 0.9, 0.95, 0.99)
        .sla(Duration.ofMillis(50), Duration.ofMillis(100), Duration.ofMillis(250), Duration.ofMillis(500), Duration.ofSeconds(1))
        .register(registry);
    }

    public HeroDto getHeroById(int id) {
        try {
            return heroApiTimer.record(() ->
                heroClient.get()
                .uri("/id/{id}.json", id)
                .retrieve()
                .body(HeroDto.class));
        } catch (Exception ex) {
            throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "Failed to fetch hero from upstream API",
                ex
            );
        }
    }
}
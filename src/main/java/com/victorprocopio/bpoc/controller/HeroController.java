package com.victorprocopio.bpoc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.victorprocopio.bpoc.dto.HeroDto;
import com.victorprocopio.bpoc.service.HeroService;

@Tag(name = "Heroes")
@RestController
@RequestMapping("/api/v1/heroes")
public class HeroController {

    private final HeroService heroService;

    public HeroController(HeroService heroService) {
        this.heroService = heroService;
    }

    @Operation(summary = "Get hero by id", description = "Fetches hero data and returns it.")
    @GetMapping("/{id}")
    public HeroDto getById(@PathVariable int id) {
        return heroService.getHeroById(id);
    }
}
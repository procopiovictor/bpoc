package com.victorprocopio.bpoc.dto;

import java.util.Map;

public record HeroDto(
    int id,
    String name,
    String slug,
    Map<String, Object> powerstats,
    Map<String, Object> biography,
    Map<String, Object> appearance,
    Map<String, Object> work,
    Map<String, Object> connections,
    Map<String, String> images
) {}

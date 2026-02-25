package com.victorprocopio.bpoc.controller;

import com.victorprocopio.bpoc.dto.HeroDto;
import com.victorprocopio.bpoc.service.HeroService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HeroController.class)
class HeroControllerTest {

@Autowired MockMvc mockMvc;

    @MockBean HeroService heroService;

    @Test
    void getHeroById_returns200() throws Exception {
        var hero = new HeroDto(
            1, "A-Bomb", "1-a-bomb",
            Map.of("strength", 100),
            Map.of(), Map.of(), Map.of(), Map.of(),
            Map.of("sm", "http://example.com/sm.png")
        );

        when(heroService.getHeroById(1)).thenReturn(hero);

        mockMvc.perform(get("/api/v1/heroes/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("A-Bomb"));
    }
}
package com.victorprocopio.bpoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.victorprocopio.bpoc.config.HeroApiProperties;

@SpringBootApplication
@EnableConfigurationProperties(HeroApiProperties.class)
public class BpocApplication {

	public static void main(String[] args) {
		SpringApplication.run(BpocApplication.class, args);
	}

}

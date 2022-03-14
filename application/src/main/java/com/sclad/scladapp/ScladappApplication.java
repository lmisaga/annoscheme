package com.sclad.scladapp;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ScladappApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScladappApplication.class, args);
    }

	@Bean
	public ModelMapper objectMapper() {
		return new ModelMapper();
	}

}

package com.sloth.plan_puzzle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PlanPuzzleApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlanPuzzleApplication.class, args);
	}

}

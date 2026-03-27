package com.project.gamegrimoire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan("com.project.gamegrimoire.model")
public class GamegrimoireApplication {

	public static void main(String[] args) {
		SpringApplication.run(GamegrimoireApplication.class, args);
	}

}

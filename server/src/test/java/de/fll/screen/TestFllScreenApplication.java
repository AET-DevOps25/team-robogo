package de.fll.screen;

import org.springframework.boot.SpringApplication;

public class TestFllScreenApplication {

	public static void main(String[] args) {
		SpringApplication.from(FllScreenApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

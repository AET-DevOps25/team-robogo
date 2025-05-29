package de.fll.screen.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRessource {

	@GetMapping("/api/test")
	public String test() {
		return "Hi there!";
	}
}

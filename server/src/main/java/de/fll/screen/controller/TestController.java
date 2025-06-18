package de.fll.screen.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/server")
public class TestRessource {

	@GetMapping("/test")
	public String test() {
		return "Hi there!";
	}
}

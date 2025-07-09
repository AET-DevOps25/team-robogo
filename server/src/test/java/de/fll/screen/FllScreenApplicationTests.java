package de.fll.screen;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(classes = TestFllScreenApplication.class)
public class FllScreenApplicationTests {

	@Test
	void contextLoads() {
	}

}

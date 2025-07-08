package de.fll.screen.init;

import de.fll.screen.model.SlideDeck;
import de.fll.screen.repository.SlideDeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Profile("dev") // 只在开发环境启用
public class SlideDeckLoader implements CommandLineRunner {
    @Autowired
    private SlideDeckRepository slideDeckRepository;

    @Override
    public void run(String... args) {
        if (slideDeckRepository.count() == 0) {
            SlideDeck deck = new SlideDeck("Demo Deck", 10, new ArrayList<>());
            slideDeckRepository.save(deck);
        }
    }
} 
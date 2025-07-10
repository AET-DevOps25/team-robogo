package de.fll.screen.init;

import de.fll.screen.model.Competition;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.repository.CompetitionRepository;
import de.fll.screen.repository.SlideDeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Order(2)
public class SlideDeckLoader implements CommandLineRunner {
    @Autowired
    private SlideDeckRepository slideDeckRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Override
    public void run(String... args) {
        if (slideDeckRepository.count() == 0) {
            Competition competition = competitionRepository.findById(1L).orElseThrow();
            SlideDeck deck = new SlideDeck("Demo Deck", 10, new ArrayList<>(), competition);
            slideDeckRepository.save(deck);
        }
    }
} 
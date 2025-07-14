package de.fll.screen.init;

import de.fll.screen.model.Screen;
import de.fll.screen.model.ScreenStatus;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.repository.ScreenRepository;
import de.fll.screen.repository.SlideDeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(6)
public class ScreenLoader implements CommandLineRunner {

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private SlideDeckRepository slideDeckRepository;

    @Override
    public void run(String... args) throws Exception {
        if (screenRepository.count() == 0) {
            List<SlideDeck> decks = slideDeckRepository.findAll();
            SlideDeck deck1 = decks.isEmpty() ? null : decks.get(0);
            SlideDeck deck2 = decks.size() > 1 ? decks.get(1) : deck1;

            Screen screen1 = new Screen();
            screen1.setName("Main Hall");
            screen1.setStatus(ScreenStatus.ONLINE);
            screen1.setSlideDeck(deck1);

            Screen screen2 = new Screen();
            screen2.setName("Side Room");
            screen2.setStatus(ScreenStatus.OFFLINE);
            screen2.setSlideDeck(deck2);

            screenRepository.save(screen1);
            screenRepository.save(screen2);
        }
    }
}

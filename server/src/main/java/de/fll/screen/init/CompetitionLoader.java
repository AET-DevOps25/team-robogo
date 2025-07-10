package de.fll.screen.init;

import de.fll.screen.model.Competition;
import de.fll.screen.repository.CompetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Order(1)
public class CompetitionLoader implements CommandLineRunner {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Override
    public void run(String... args) throws Exception {
        if (competitionRepository.count() == 0) {
            Competition fll = new Competition();
            fll.setName("FLL Competition");
            fll.setInternalId(UUID.randomUUID());
            competitionRepository.save(fll);

            Competition wro = new Competition();
            wro.setName("WRO Competition");
            wro.setInternalId(UUID.randomUUID());
            competitionRepository.save(wro);
        }
    }
}

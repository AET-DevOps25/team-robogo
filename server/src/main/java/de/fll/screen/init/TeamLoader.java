package de.fll.screen.init;

import de.fll.screen.model.Team;
import de.fll.screen.repository.TeamRepository;
import de.fll.screen.model.Category;
import de.fll.screen.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(3)
public class TeamLoader implements CommandLineRunner {

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (teamRepository.count() == 0) {
            List<Category> categories = categoryRepository.findAll();
            Team team1 = new Team("Alpha Team");
            team1.setCategory(categories.get(0));
            Team team2 = new Team("Beta Team");
            team2.setCategory(categories.get(1));
            Team team3 = new Team("Gamma Team");
            team3.setCategory(categories.get(2));
            teamRepository.save(team1);
            teamRepository.save(team2);
            teamRepository.save(team3);
        }
    }
}

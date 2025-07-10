package de.fll.screen.init;

import de.fll.screen.model.Category;
import de.fll.screen.model.CategoryScoring;
import de.fll.screen.model.Competition;
import de.fll.screen.repository.CategoryRepository;
import de.fll.screen.repository.CompetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)
public class CategoryLoader implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            List<Competition> competitions = competitionRepository.findAll();
            if (competitions.isEmpty()) return;
            Competition fll = competitions.get(0);
            Competition wro = competitions.size() > 1 ? competitions.get(1) : fll;

            Category c1 = new Category();
            c1.setName("FLL Robot Game");
            c1.setCompetition(fll);
            c1.setCategoryScoring(CategoryScoring.FLL_ROBOT_GAME);
            categoryRepository.save(c1);

            Category c2 = new Category();
            c2.setName("FLL Quarter Final");
            c2.setCompetition(fll);
            c2.setCategoryScoring(CategoryScoring.FLL_QUARTER_FINAL);
            categoryRepository.save(c2);

            Category c3 = new Category();
            c3.setName("FLL Test Round");
            c3.setCompetition(fll);
            c3.setCategoryScoring(CategoryScoring.FLL_TESTROUND);
            categoryRepository.save(c3);

            Category c4 = new Category();
            c4.setName("WRO Starter");
            c4.setCompetition(wro);
            c4.setCategoryScoring(CategoryScoring.WRO_STARTER);
            categoryRepository.save(c4);

            Category c5 = new Category();
            c5.setName("WRO RoboMission 2025");
            c5.setCompetition(wro);
            c5.setCategoryScoring(CategoryScoring.WRO_ROBOMISSION_2025);
            categoryRepository.save(c5);
        }
    }
}

package de.fll.screen.init;

import de.fll.screen.model.Score;
import de.fll.screen.model.ScoreSlide;
import de.fll.screen.model.Team;
import de.fll.screen.model.Slide;
import de.fll.screen.repository.SlideRepository;
import de.fll.screen.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(4)
public class ScoreLoader implements CommandLineRunner {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private SlideRepository slideRepository;

    @Override
    public void run(String... args) throws Exception {
        List<Team> teams = teamRepository.findAll();
        if (teams.isEmpty()) return;
        List<ScoreSlide> scoreSlides = new ArrayList<>();
        for (Slide slide : slideRepository.findAll()) {
            if (slide instanceof ScoreSlide) {
                scoreSlides.add((ScoreSlide) slide);
            }
        }
        if (scoreSlides.isEmpty()) return;
        int slideIdx = 0;
        for (Team team : teams) {
            for (int i = 0; i < 3; i++) {
                Score s = new Score(100 - i * 10, 120 + i * 10);
                s.setTeam(team);
                s.setScoreSlide(scoreSlides.get(slideIdx % scoreSlides.size()));
                team.getScores().add(s);
                slideIdx++;
            }
            teamRepository.save(team);
        }
    }
}

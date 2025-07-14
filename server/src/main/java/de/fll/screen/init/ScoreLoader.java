package de.fll.screen.init;

import de.fll.screen.model.Score;
import de.fll.screen.model.ScoreSlide;
import de.fll.screen.model.Team;
import de.fll.screen.model.Slide;
import de.fll.screen.repository.SlideRepository;
import de.fll.screen.repository.TeamRepository;
import de.fll.screen.repository.ScoreRepository;
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

    @Autowired
    private ScoreRepository scoreRepository;

    @Override
    public void run(String... args) throws Exception {
        List<Team> teams = teamRepository.findAll();
        System.out.println("[ScoreLoader] Team count: " + teams.size());
        if (teams.isEmpty()) return;
        List<ScoreSlide> scoreSlides = new ArrayList<>();
        for (Slide slide : slideRepository.findAll()) {
            if (slide instanceof ScoreSlide) {
                scoreSlides.add((ScoreSlide) slide);
            }
        }
        System.out.println("[ScoreLoader] ScoreSlide count: " + scoreSlides.size());
        if (scoreSlides.isEmpty()) return;
        int slideIdx = 0;
        for (Team team : teams) {
            for (int i = 0; i < 3; i++) {
                Score s = new Score(100 - i * 10, 120 + i * 10);
                ScoreSlide scoreSlide = scoreSlides.get(slideIdx % scoreSlides.size());
                s.setScoreSlide(scoreSlide);
                s.setTeam(team);
                System.out.println("[ScoreLoader] Saving score: teamId=" + team.getId() + ", scoreSlideId=" + scoreSlide.getId() + ", points=" + s.getPoints());
                scoreRepository.save(s);
                slideIdx++;
            }
        }
    }
}

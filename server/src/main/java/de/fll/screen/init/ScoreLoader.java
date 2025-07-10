package de.fll.screen.init;

import de.fll.screen.model.Score;
import de.fll.screen.model.Team;
import de.fll.screen.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(4)
public class ScoreLoader implements CommandLineRunner {

    @Autowired
    private TeamRepository teamRepository;

    @Override
    public void run(String... args) throws Exception {
        List<Team> teams = teamRepository.findAll();
        if (teams.isEmpty()) return;
        // 为每个队伍添加三条分数，通过 getScores().add()
        for (Team team : teams) {
            Score s1 = new Score(100, 120);
            s1.setTeam(team);
            Score s2 = new Score(80, 110);
            s2.setTeam(team);
            Score s3 = new Score(60, 130);
            s3.setTeam(team);
            team.getScores().add(s1);
            team.getScores().add(s2);
            team.getScores().add(s3);
            teamRepository.save(team);
        }
    }
}

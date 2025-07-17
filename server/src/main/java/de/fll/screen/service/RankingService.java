package de.fll.screen.service;

import de.fll.screen.model.Category;
import de.fll.screen.model.Team;
import de.fll.core.dto.ScoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RankingService {

    public List<ScoreDTO> getRankedTeams(Category category, Set<Team> teams) {
        List<Team> sorted = new ArrayList<>(teams);
        // 按score分数降序排序
        sorted.sort((t1, t2) -> {
            if (t1.getScore() == null && t2.getScore() == null) return 0;
            if (t1.getScore() == null) return 1;
            if (t2.getScore() == null) return -1;
            return Double.compare(t2.getScore().getPoints(), t1.getScore().getPoints());
        });
        List<ScoreDTO> result = new ArrayList<>();
        int rank = 1;
        double lastScore = Double.NaN;
        for (int i = 0; i < sorted.size(); i++) {
            Team team = sorted.get(i);
            ScoreDTO.ScoreDTOBuilder builder = ScoreDTO.builder();
            if (team.getScore() != null) {
                builder.points(team.getScore().getPoints())
                       .time(team.getScore().getTime());
            }
            builder.team(de.fll.core.dto.TeamDTO.builder().id(team.getId()).name(team.getName()).build());
            // 相同分数并列
            if (team.getScore() != null && team.getScore().getPoints() != lastScore) {
                rank = i + 1;
                lastScore = team.getScore().getPoints();
            }
            builder.rank(rank);
            // 第一名高亮
            builder.highlight(rank == 1);
            result.add(builder.build());
        }
        return result;
    }
} 
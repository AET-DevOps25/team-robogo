package de.fll.screen.service;

import de.fll.screen.model.Category;
import de.fll.screen.model.Team;
import de.fll.core.dto.ScoreDTO;
import de.fll.screen.model.CategoryScoring;
import de.fll.screen.service.comparators.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RankingService {
    private final Map<CategoryScoring, CategoryComparator> comparatorMap = new HashMap<>();

    @Autowired
    public RankingService(
        FLLRobotGameComparator fllRobotGameComparator,
        FLLQuarterFinalComparator fllQuarterFinalComparator,
        FLLTestRoundComparator fllTestRoundComparator,
        WROStarterComparator wroStarterComparator,
        WRO2025Comparator wro2025Comparator
    ) {
        comparatorMap.put(CategoryScoring.FLL_ROBOT_GAME, fllRobotGameComparator);
        comparatorMap.put(CategoryScoring.FLL_QUARTER_FINAL, fllQuarterFinalComparator);
        comparatorMap.put(CategoryScoring.FLL_TESTROUND, fllTestRoundComparator);
        comparatorMap.put(CategoryScoring.WRO_STARTER, wroStarterComparator);
        comparatorMap.put(CategoryScoring.WRO_ROBOMISSION_2025, wro2025Comparator);
    }

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
            result.add(builder.build());
        }
        return result;
    }
} 
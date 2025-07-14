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
        CategoryComparator comparator = comparatorMap.get(category.getCategoryScoring());
        if (comparator == null) {
            throw new IllegalArgumentException("No comparator for scoring: " + category.getCategoryScoring());
        }
        return comparator.assignRanks(teams);
    }
} 
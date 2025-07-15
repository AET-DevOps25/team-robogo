package de.fll.screen.service;

import de.fll.core.dto.ScoreDTO;
import de.fll.core.dto.TeamDTO;
import de.fll.screen.model.*;
import de.fll.screen.service.comparators.FLLRobotGameComparator;
import de.fll.screen.service.comparators.FLLQuarterFinalComparator;
import de.fll.screen.service.comparators.FLLTestRoundComparator;
import de.fll.screen.service.comparators.WROStarterComparator;
import de.fll.screen.service.comparators.WRO2025Comparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RankingServiceTest {
    private RankingService rankingService;
    private Category category;
    private Team team1, team2;

    @BeforeEach
    void setUp() {
        rankingService = new RankingService(null, null, null, null, null);
        category = new Category();
        team1 = new Team("T1");
        team2 = new Team("T2");
    }

    @Test
    void testGetRankedTeams_Normal() {
        // team1 100分，team2 90分
        Score score1 = new Score(100.0, 120);
        score1.setTeam(team1);
        team1.setScore(score1);
        team1.setId(1L);
        Score score2 = new Score(90.0, 130);
        score2.setTeam(team2);
        team2.setScore(score2);
        team2.setId(2L);
        Set<Team> teams = Set.of(team1, team2);
        List<ScoreDTO> result = rankingService.getRankedTeams(category, teams);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getRank());
        assertEquals(2, result.get(1).getRank());
        assertEquals("T1", result.get(0).getTeam().getName());
        assertEquals("T2", result.get(1).getTeam().getName());
    }

    @Test
    void testGetRankedTeams_Tie() {
        // 两队同分并列
        Score score1 = new Score(100.0, 120);
        score1.setTeam(team1);
        team1.setScore(score1);
        team1.setId(1L);
        Score score2 = new Score(100.0, 130);
        score2.setTeam(team2);
        team2.setScore(score2);
        team2.setId(2L);
        Set<Team> teams = Set.of(team1, team2);
        List<ScoreDTO> result = rankingService.getRankedTeams(category, teams);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getRank());
        assertEquals(1, result.get(1).getRank());
    }
} 
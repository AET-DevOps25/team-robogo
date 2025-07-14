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
    @Mock
    private FLLRobotGameComparator fllRobotGameComparator;
    @Mock
    private FLLQuarterFinalComparator fllQuarterFinalComparator;
    @Mock
    private FLLTestRoundComparator fllTestRoundComparator;
    @Mock
    private WROStarterComparator wroStarterComparator;
    @Mock
    private WRO2025Comparator wro2025Comparator;
    @InjectMocks
    private RankingService rankingService;
    private Category category;
    private Team team1, team2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rankingService = new RankingService(
            fllRobotGameComparator,
            fllQuarterFinalComparator,
            fllTestRoundComparator,
            wroStarterComparator,
            wro2025Comparator
        );
        category = new Category();
        category.setCategoryScoring(CategoryScoring.FLL_ROBOT_GAME);
        team1 = new Team("T1");
        team2 = new Team("T2");
    }

    @Test
    void testGetRankedTeams_Normal() {
        Set<Team> teams = Set.of(team1, team2);
        TeamDTO teamDTO1 = TeamDTO.builder().id(1L).name("T1").build();
        TeamDTO teamDTO2 = TeamDTO.builder().id(2L).name("T2").build();
        ScoreDTO score1 = ScoreDTO.builder().points(100.0).time(120).highlight(false).team(teamDTO1).rank(1).build();
        ScoreDTO score2 = ScoreDTO.builder().points(90.0).time(130).highlight(false).team(teamDTO2).rank(2).build();
        List<ScoreDTO> expected = List.of(score1, score2);

        when(fllRobotGameComparator.assignRanks(teams)).thenReturn(expected);

        List<ScoreDTO> result = rankingService.getRankedTeams(category, teams);
        assertEquals(expected, result);
        assertEquals(1, result.get(0).getRank());
        assertEquals(2, result.get(1).getRank());
        assertEquals("T1", result.get(0).getTeam().getName());
        assertEquals("T2", result.get(1).getTeam().getName());
    }

    @Test
    void testGetRankedTeams_NoComparator() {
        Category category2 = new Category();
        category2.setCategoryScoring(null);
        Set<Team> teams = Set.of(team1);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> rankingService.getRankedTeams(category2, teams));
        assertTrue(ex.getMessage().contains("No comparator"));
    }
} 
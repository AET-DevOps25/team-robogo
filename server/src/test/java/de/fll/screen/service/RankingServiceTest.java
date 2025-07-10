package de.fll.screen.service;

import de.fll.screen.model.*;
import de.fll.core.dto.TeamDTO;
import de.fll.screen.model.CategoryScoring;
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
    private Team team;

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
        team = new Team("T1");
    }

    @Test
    void testGetRankedTeams_Normal() {
        Set<Team> teams = Set.of(team);
        List<TeamDTO> expected = List.of(TeamDTO.builder().build());
        when(fllRobotGameComparator.assignRanks(teams)).thenReturn(expected);
        List<TeamDTO> result = rankingService.getRankedTeams(category, teams);
        assertEquals(expected, result);
    }

    @Test
    void testGetRankedTeams_NoComparator() {
        Category category2 = new Category();
        category2.setCategoryScoring(null);
        Set<Team> teams = Set.of(team);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> rankingService.getRankedTeams(category2, teams));
        assertTrue(ex.getMessage().contains("No comparator"));
    }
} 
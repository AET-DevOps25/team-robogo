package de.fll.screen.service;

import de.fll.core.dto.ScoreDTO;
import de.fll.screen.model.*;
import de.fll.screen.repository.ScoreRepository;
import de.fll.screen.repository.TeamRepository;
import de.fll.screen.service.comparators.FLLRobotGameComparator;
import de.fll.screen.service.comparators.FLLQuarterFinalComparator;
import de.fll.screen.service.comparators.FLLTestRoundComparator;
import de.fll.screen.service.comparators.WROStarterComparator;
import de.fll.screen.service.comparators.WRO2025Comparator;
import de.fll.screen.model.CategoryScoring;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ScoreServiceTest {
    @InjectMocks
    private ScoreService scoreService;

    @Mock
    private ScoreRepository scoreRepository;
    @Mock
    private TeamRepository teamRepository;
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

    private Category category;
    private Team team;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scoreService = new ScoreService(
            scoreRepository,
            teamRepository,
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
    void testAddScore() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(scoreRepository.save(any(Score.class))).thenAnswer(i -> i.getArgument(0));
        Score score = scoreService.addScore(1L, 88.5, 120);
        assertEquals(88.5, score.getPoints());
        assertEquals(120, score.getTime());
        assertEquals(team, score.getTeam());
        assertTrue(team.getScores().contains(score));
    }

    @Test
    void testGetScoresForTeam() {
        List<Score> scores = List.of(new Score(10, 1), new Score(20, 2));
        when(scoreRepository.findByTeam(team)).thenReturn(scores);
        List<Score> result = scoreService.getScoresForTeam(team);
        assertEquals(2, result.size());
        assertEquals(10, result.get(0).getPoints());
    }

    @Test
    void testGetScoreDTOsWithHighlight() {
        Score s1 = new Score(10, 1);
        Score s2 = new Score(20, 2);
        team.getScores().addAll(List.of(s1, s2));
        Set<Integer> highlight = Set.of(1);
        when(fllRobotGameComparator.getHighlightIndices(team)).thenReturn(highlight);
        // categoryScoring 必须和构造时一致
        category.setCategoryScoring(CategoryScoring.FLL_ROBOT_GAME);
        List<ScoreDTO> dtos = scoreService.getScoreDTOsWithHighlight(team, category);
        assertEquals(2, dtos.size());
        assertFalse(dtos.get(0).getHighlight());
        assertTrue(dtos.get(1).getHighlight());
    }

    @Test
    void testGetAllTeamsScoreDTOsWithHighlight() {
        Team t1 = new Team("A");
        Team t2 = new Team("B");
        t1.getScores().add(new Score(10, 1));
        t2.getScores().add(new Score(20, 2));
        when(fllRobotGameComparator.getHighlightIndices(t1)).thenReturn(Set.of(0));
        when(fllRobotGameComparator.getHighlightIndices(t2)).thenReturn(Set.of());
        category.setCategoryScoring(CategoryScoring.FLL_ROBOT_GAME);
        List<ScoreDTO> dtos = scoreService.getAllTeamsScoreDTOsWithHighlight(List.of(t1, t2), category);
        assertEquals(2, dtos.size());
        assertTrue(dtos.get(0).getHighlight());
        assertFalse(dtos.get(1).getHighlight());
    }
} 
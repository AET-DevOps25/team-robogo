package de.fll.screen.service.comparators;

import de.fll.screen.model.Team;
import de.fll.screen.model.Score;
import de.fll.screen.model.ScoreSlide;
import de.fll.core.dto.ScoreDTO;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class FLLQuarterFinalComparatorTest {
    private Team buildTeam(long id, String name, double points) {
        Team team = new Team();
        team.setId(id);
        team.setName(name);
        ScoreSlide slide = new ScoreSlide();
        Score score = new Score(points, 100);
        score.setTeam(team);
        score.setScoreSlide(slide);
        team.setScore(score);
        slide.setScores(List.of(score));
        return team;
    }

    @Test
    void testCompare() {
        FLLQuarterFinalComparator comparator = new FLLQuarterFinalComparator();
        Team t1 = buildTeam(1, "A", 100);
        Team t2 = buildTeam(2, "B", 80);
        assertTrue(comparator.compare(t1, t2) < 0);
        assertTrue(comparator.compare(t2, t1) > 0);
        assertEquals(0, comparator.compare(t1, t1));
    }

    @Test
    void testAssignRanks() {
        FLLQuarterFinalComparator comparator = new FLLQuarterFinalComparator();
        Team t1 = buildTeam(1, "A", 100);
        Team t2 = buildTeam(2, "B", 80);
        Set<Team> teams = Set.of(t1, t2);
        List<ScoreDTO> ranked = comparator.assignRanks(teams);
        assertEquals(2, ranked.size());
        assertEquals(1, ranked.get(0).getRank());
        assertEquals(2, ranked.get(1).getRank());
    }
} 
package de.fll.screen.service.comparators;

import de.fll.screen.model.Team;
import de.fll.screen.model.Score;
import de.fll.core.dto.TeamDTO;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class FLLRobotGameComparatorTest {
    private Team buildTeam(long id, String name, double... points) {
        Team team = new Team();
        team.setId(id);
        team.setName(name);
        for (double p : points) {
            team.getScores().add(new Score(p, 100));
        }
        return team;
    }

    @Test
    void testCompare() {
        FLLRobotGameComparator comparator = new FLLRobotGameComparator();
        Team t1 = buildTeam(1, "A", 100, 90, 80);
        Team t2 = buildTeam(2, "B", 80, 70, 60);
        assertTrue(comparator.compare(t1, t2) < 0);
        assertTrue(comparator.compare(t2, t1) > 0);
        assertEquals(0, comparator.compare(t1, t1));
    }

    @Test
    void testAssignRanks() {
        FLLRobotGameComparator comparator = new FLLRobotGameComparator();
        Team t1 = buildTeam(1, "A", 100, 90, 80);
        Team t2 = buildTeam(2, "B", 80, 70, 60);
        Set<Team> teams = Set.of(t1, t2);
        List<TeamDTO> ranked = comparator.assignRanks(teams);
        assertEquals(2, ranked.size());
    }
} 
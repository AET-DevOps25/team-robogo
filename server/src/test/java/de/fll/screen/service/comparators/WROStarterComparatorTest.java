package de.fll.screen.service.comparators;

import de.fll.screen.model.Team;
import de.fll.screen.model.Score;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class WROStarterComparatorTest {
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
        WROStarterComparator comparator = new WROStarterComparator();
        Team t1 = buildTeam(1, "A", 100, 90);
        Team t2 = buildTeam(2, "B", 80, 70);
        assertTrue(comparator.compare(t1, t2) > 0);
        assertTrue(comparator.compare(t2, t1) < 0);
        assertEquals(0, comparator.compare(t1, t1));
    }

    @Test
    void testAssignRanks() {
        WROStarterComparator comparator = new WROStarterComparator();
        Team t1 = buildTeam(1, "A", 100, 90);
        Team t2 = buildTeam(2, "B", 80, 70);
        Set<Team> teams = Set.of(t1, t2);
        List<?> ranked = comparator.assignRanks(teams);
        assertEquals(2, ranked.size());
    }
} 
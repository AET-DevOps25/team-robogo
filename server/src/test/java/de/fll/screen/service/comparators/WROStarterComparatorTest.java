package de.fll.screen.service.comparators;

import de.fll.core.proto.TeamOuterClass;
import de.fll.core.proto.ScoreOuterClass;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class WROStarterComparatorTest {
    private TeamOuterClass.Team buildTeam(long id, String name, double... points) {
        TeamOuterClass.Team.Builder builder = TeamOuterClass.Team.newBuilder().setId(id).setName(name);
        for (double p : points) {
            builder.addScores(ScoreOuterClass.Score.newBuilder().setPoints(p).setTime(100).build());
        }
        return builder.build();
    }

    @Test
    void testCompare() {
        WROStarterComparator comparator = new WROStarterComparator();
        TeamOuterClass.Team t1 = buildTeam(1, "A", 100, 90);
        TeamOuterClass.Team t2 = buildTeam(2, "B", 80, 70);
        assertTrue(comparator.compare(t1, t2) > 0);
        assertTrue(comparator.compare(t2, t1) < 0);
        assertEquals(0, comparator.compare(t1, t1));
    }

    @Test
    void testAssignRanks() {
        WROStarterComparator comparator = new WROStarterComparator();
        TeamOuterClass.Team t1 = buildTeam(1, "A", 100, 90);
        TeamOuterClass.Team t2 = buildTeam(2, "B", 80, 70);
        Set<TeamOuterClass.Team> teams = Set.of(t1, t2);
        List<TeamOuterClass.Team> ranked = comparator.assignRanks(teams);
        assertEquals(2, ranked.size());
        assertEquals(1, ranked.get(0).getRank());
        assertEquals(2, ranked.get(1).getRank());
    }
} 
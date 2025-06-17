package de.fll.screen.service.comparators;

import de.fll.core.proto.TeamOuterClass;
import de.fll.core.proto.ScoreOuterClass;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class FLLTestRoundComparator extends AbstractFLLComparator {

	// TODO: Implement score extraction and saving

	@Override
	protected List<ScoreOuterClass.Score> getRelevantScores(TeamOuterClass.Team team) {
		return team.getScoresList();
	}

	@Override
	public Set<Integer> getHighlightIndices(TeamOuterClass.Team team) {
		// No highlighting for test rounds
		return Set.of();
	}

	@Override
	public List<TeamOuterClass.Team> assignRanks(Set<TeamOuterClass.Team> teams) {
		return assignRanks(teams, null);
	}

	@Override
	public int compare(TeamOuterClass.Team o1, TeamOuterClass.Team o2) {
		return compareOneScore(o1, o2, 0);
	}
}

package de.fll.screen.service.comparators;

import de.fll.core.proto.TeamOuterClass;
import de.fll.core.proto.ScoreOuterClass;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class FLLQuarterFinalComparator extends AbstractFLLComparator {

	// TODO: Implement score extraction such that only the relevant scores are used here.

	@Override
	public Set<Integer> getHighlightIndices(TeamOuterClass.Team team) {
		// Only one score for the quarter final, don't highlight anything
		return Collections.emptySet();
	}

	@Override
	public int compare(TeamOuterClass.Team t1, TeamOuterClass.Team t2) {
		return compareOneScore(t1, t2, 0); // Assumes that only the one score is given at index 0
	}

	@Override
	public List<TeamOuterClass.Team> assignRanks(Set<TeamOuterClass.Team> teams) {
		// Quarter final only has 8 competing teams
		return assignRanks(teams, team -> getRelevantScores(team).isEmpty() ? null : getRelevantScores(team).get(0))
			.subList(0, Math.min(8, teams.size()));
	}

	@Override
	protected List<ScoreOuterClass.Score> getRelevantScores(TeamOuterClass.Team team) {
		var scores = team.getScoresList();
		if (scores.isEmpty()) {
			return Collections.emptyList();
		}
		return Collections.singletonList(scores.get(0));
	}
}

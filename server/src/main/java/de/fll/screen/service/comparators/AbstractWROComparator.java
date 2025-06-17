package de.fll.screen.service.comparators;

import de.fll.core.proto.TeamOuterClass;
import de.fll.core.proto.ScoreOuterClass;

import java.util.*;

public abstract class AbstractWROComparator implements CategoryComparator {

	// Use the comparator to assign ranks to the teams
	// The same rank is only assigned if the comparator returns 0
	public List<TeamOuterClass.Team> assignRanks(Set<TeamOuterClass.Team> teams) {
		List<TeamOuterClass.Team> sorted = new ArrayList<>(teams);
		sorted.sort(this);
		List<TeamOuterClass.Team> teamList = new ArrayList<>(teams.size());

		int rank = 0;
		for (int i = 0; i < sorted.size(); i++) {
			TeamOuterClass.Team team = sorted.get(i);
			var highlightIndices = getHighlightIndices(team);
			if (i == 0 || compare(sorted.get(i - 1), team) != 0) {
				// New rank
				rank = i + 1;
			}
			if (rank != 1 && team.getScoresList().stream().noneMatch(score -> score.getPoints() > 0)) {
				// Skip teams with 0 points
				// Except when everyone has 0 points (all rank 1)
				continue;
			}

			List<ScoreOuterClass.Score> scores = new ArrayList<>(team.getScoresList().size());
			for (int j = 0; j < team.getScoresList().size(); j++) {
				ScoreOuterClass.Score score = team.getScoresList().get(j);
				scores.add(score.toBuilder().setHighlight(highlightIndices.contains(j)).build());
			}

			teamList.add(team.toBuilder().setRank(rank).addAllScores(scores).build());
		}

		return teamList;
	}
}

package de.fll.screen.service.comparators;

import de.fll.screen.model.Score;
import de.fll.screen.model.Team;
import de.fll.core.proto.TeamOuterClass;
import de.fll.core.proto.ScoreOuterClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

abstract class AbstractWROComparator implements CategoryComparator {

	// Use the comparator to assign ranks to the teams
	// The same rank is only assigned if the comparator returns 0
	public List<TeamOuterClass.Team> assignRanks(Set<Team> teams) {
		List<Team> sorted = new ArrayList<>(teams);
		sorted.sort(this);
		List<TeamOuterClass.Team> teamDTOs = new ArrayList<>(teams.size());

		int rank = 0;
		for (int i = 0; i < sorted.size(); i++) {
			Team team = sorted.get(i);
			var highlightIndices = getHighlightIndices(team);
			if (i == 0 || compare(sorted.get(i - 1), team) != 0) {
				// New rank
				rank = i + 1;
			}
			if (rank != 1 && team.getScores().stream().noneMatch(score -> score.getPoints() > 0)) {
				// Skip teams with 0 points
				// Except when everyone has 0 points (all rank 1)
				continue;
			}

			List<ScoreOuterClass.Score> scores = new ArrayList<>(team.getScores().size());
			for (int j = 0; j < team.getScores().size(); j++) {
				Score score = team.getScores().get(j);
				scores.add(ScoreOuterClass.Score.newBuilder()
					.setPoints(score.getPoints())
					.setTime(score.getTime())
					.setHighlight(highlightIndices.contains(j))
					.build());
			}

			teamDTOs.add(TeamOuterClass.Team.newBuilder()
				.setId(team.getId())
				.setName(team.getName())
				.setRank(rank)
				.addAllScores(scores)
				.build());
		}

		return teamDTOs;
	}
}

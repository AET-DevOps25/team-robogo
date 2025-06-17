package de.fll.screen.service.comparators;

import de.fll.core.proto.TeamOuterClass;
import de.fll.core.proto.ScoreOuterClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.springframework.lang.NonNull;

abstract class AbstractFLLComparator implements CategoryComparator {

	protected abstract List<ScoreOuterClass.Score> getRelevantScores(TeamOuterClass.Team team);

	protected List<TeamOuterClass.Team> assignRanks(Set<TeamOuterClass.Team> teams, 
		@NonNull Function<TeamOuterClass.Team, ScoreOuterClass.Score> scoreExtractor) {
		List<TeamOuterClass.Team> teamList = new ArrayList<>(teams.size());
		List<TeamOuterClass.Team> sorted = new ArrayList<>(teams);
		sorted.sort(this);

		double previousScore = -1;
		int rank = 0;
		for (int i = 0; i < sorted.size(); i++) {
			TeamOuterClass.Team team = sorted.get(i);
			ScoreOuterClass.Score bestScore = scoreExtractor != null ? scoreExtractor.apply(team) : null;
			if (bestScore != null && bestScore.getPoints() != previousScore) {
				rank = i + 1;
			}

			var highlightIndices = getHighlightIndices(team);

			var scores = getRelevantScores(team).stream()
					.map(score -> ScoreOuterClass.Score.newBuilder()
						.setPoints(score.getPoints())
						.setTime(score.getTime())
						.setHighlight(highlightIndices.contains(team.getScoresList().indexOf(score)))
						.build())
					.toList();
			teamList.add(TeamOuterClass.Team.newBuilder()
				.setId(team.getId())
				.setName(team.getName())
				.setRank(rank)
				.addAllScores(scores)
				.build());
			previousScore = bestScore != null ? bestScore.getPoints() : previousScore;
		}
		return teamList;
	}

	protected int compareOneScore(TeamOuterClass.Team t1, TeamOuterClass.Team t2, int roundIndex) {
		var s1 = t1.getScoresList().get(roundIndex);
		var s2 = t2.getScoresList().get(roundIndex);
		if (s1 == null && s2 == null) {
			return 0;
		}
		if (s1 == null) {
			return 1;
		}
		if (s2 == null) {
			return -1;
		}
		return Double.compare(s2.getPoints(), s1.getPoints());
	}
}

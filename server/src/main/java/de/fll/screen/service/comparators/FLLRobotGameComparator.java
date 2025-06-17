package de.fll.screen.service.comparators;

import de.fll.core.proto.TeamOuterClass;
import de.fll.core.proto.ScoreOuterClass;

import java.util.*;

public class FLLRobotGameComparator extends AbstractFLLComparator {

	@Override
	public int compare(TeamOuterClass.Team team1, TeamOuterClass.Team team2) {
		List<ScoreOuterClass.Score> t1Scores = new ArrayList<>(getRelevantScores(team1));
		List<ScoreOuterClass.Score> t2Scores = new ArrayList<>(getRelevantScores(team2));

		t1Scores.sort(Comparator.comparing(ScoreOuterClass.Score::getPoints).reversed());
		t2Scores.sort(Comparator.comparing(ScoreOuterClass.Score::getPoints).reversed());

		for (int i = 0; i < t1Scores.size(); i++) {
			if (t1Scores.get(i).getPoints() != t2Scores.get(i).getPoints()) {
				return Double.compare(t2Scores.get(i).getPoints(), t1Scores.get(i).getPoints());
			}
		}
		return 0;
	}

	@Override
	protected List<ScoreOuterClass.Score> getRelevantScores(TeamOuterClass.Team team) {
		return team.getScoresList().subList(0, Math.min(3, team.getScoresList().size()));
	}

	@Override
	public Set<Integer> getHighlightIndices(TeamOuterClass.Team team) {
		List<ScoreOuterClass.Score> scores = getRelevantScores(team);
		double bestScore = getBestScore(team);
		if (bestScore == 0) {
			return Collections.emptySet();
		}
		for (int i = 0; i < scores.size(); i++) {
			if (scores.get(i).getPoints() == bestScore) {
				return Set.of(i);
			}
		}
		return Set.of();
	}

	@Override
	public List<TeamOuterClass.Team> assignRanks(Set<TeamOuterClass.Team> teams) {
		return super.assignRanks(teams, null);
	}

	private double getBestScore(TeamOuterClass.Team team) {
		return getRelevantScores(team).stream().mapToDouble(ScoreOuterClass.Score::getPoints).max().orElse(-1);
	}
}

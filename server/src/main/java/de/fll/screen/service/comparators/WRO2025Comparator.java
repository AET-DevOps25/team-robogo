package de.fll.screen.service.comparators;

import de.fll.core.proto.TeamOuterClass;
import de.fll.core.proto.ScoreOuterClass;

import java.util.HashSet;
import java.util.Set;

public class WRO2025Comparator extends AbstractWROComparator {

	@Override
	public int compare(TeamOuterClass.Team t1, TeamOuterClass.Team t2) {
		ScoreOuterClass.Score t1Morning = getBestRoundMorning(t1);
		ScoreOuterClass.Score t2Morning = getBestRoundMorning(t2);

		ScoreOuterClass.Score t1Afternoon = getBestRoundAfternoon(t1);
		ScoreOuterClass.Score t2Afternoon = getBestRoundAfternoon(t2);

		ScoreOuterClass.Score t1Points = addScores(t1Morning, t1Afternoon);
		ScoreOuterClass.Score t2Points = addScores(t2Morning, t2Afternoon);

		int resultBoth = compareWithTime(t1Points, t2Points);
		if (resultBoth != 0) {
			return resultBoth;
		}

		return compareWithTime(t1Morning, t2Morning);
	}

	private ScoreOuterClass.Score getBestRoundMorning(TeamOuterClass.Team team) {
		var s1 = team.getScoresList().get(0);
		var s2 = team.getScoresList().get(1);
		return getBetterRound(s1, s2);
	}

	private ScoreOuterClass.Score getBetterRound(ScoreOuterClass.Score s1, ScoreOuterClass.Score s2) {
		if (s1.getPoints() > s2.getPoints()) {
			return s1;
		} else if (s1.getPoints() < s2.getPoints()) {
			return s2;
		} else {
			return s1.getTime() < s2.getTime() ? s1 : s2;
		}
	}

	private ScoreOuterClass.Score getBestRoundAfternoon(TeamOuterClass.Team team) {
		return getBetterRound(team.getScoresList().get(2), team.getScoresList().get(3));
	}

	private ScoreOuterClass.Score addScores(ScoreOuterClass.Score s1, ScoreOuterClass.Score s2) {
		return ScoreOuterClass.Score.newBuilder()
			.setPoints(s1.getPoints() + s2.getPoints())
			.setTime(s1.getTime() + s2.getTime())
			.build();
	}

	private int compareWithTime(ScoreOuterClass.Score s1, ScoreOuterClass.Score s2) {
		int cmpPoints = Double.compare(s1.getPoints(), s2.getPoints());
		if (cmpPoints == 0) {
			return -Integer.compare(s1.getTime(), s2.getTime());
		}
		return cmpPoints;
	}

	@Override
	public Set<Integer> getHighlightIndices(TeamOuterClass.Team team) {
		ScoreOuterClass.Score bestMorning = getBestRoundMorning(team);
		ScoreOuterClass.Score bestAfternoon = getBestRoundAfternoon(team);

		Set<Integer> highlightIndices = new HashSet<>();
		for (int i = 0; i < team.getScoresList().size(); i++) {
			var score = team.getScoresList().get(i);
			if (score.equals(bestMorning) || score.equals(bestAfternoon)) {
				highlightIndices.add(i);
			}
		}
		return highlightIndices;
	}
}

package de.fll.screen.service.comparators;

import de.fll.core.proto.TeamOuterClass;
import de.fll.core.proto.ScoreOuterClass;

import java.util.*;

public class WROStarterComparator extends AbstractWROComparator {

	@Override
	public int compare(TeamOuterClass.Team team1, TeamOuterClass.Team team2) {
		List<ScoreOuterClass.Score> t1Scores = new ArrayList<>(team1.getScoresList());
		List<ScoreOuterClass.Score> t2Scores = new ArrayList<>(team2.getScoresList());

		t1Scores.sort(Comparator.comparing(ScoreOuterClass.Score::getPoints).reversed());
		t2Scores.sort(Comparator.comparing(ScoreOuterClass.Score::getPoints).reversed());

		for (int i = 0; i < t1Scores.size(); i++) {
			int compareWithTime = compareWithTime(t1Scores.get(i), t2Scores.get(i));
			if (compareWithTime != 0) {
				return compareWithTime;
			}
		}
		return 0;
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
		List<ScoreOuterClass.Score> scores = new ArrayList<>(team.getScoresList());
		scores.sort(Comparator.comparing(ScoreOuterClass.Score::getPoints).reversed());

		for (int i = 0; i < scores.size(); i++) {
			if (team.getScoresList().get(i).equals(scores.get(0))) {
				return Set.of(i);
			}
		}
		return Collections.emptySet();
	}
}

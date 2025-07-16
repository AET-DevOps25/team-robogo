package de.fll.screen.service.comparators;

import de.fll.screen.model.Score;
import de.fll.screen.model.Team;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Service
public class WRO2025Comparator extends AbstractWROComparator {

	@Override
	public int compare(Team t1, Team t2) {
		var t1Morning = getBestRoundMorning(t1);
		var t2Morning = getBestRoundMorning(t2);

		var t1Afternoon = getBestRoundAfternoon(t1);
		var t2Afternoon = getBestRoundAfternoon(t2);

		Score t1Points = t1Morning.add(t1Afternoon);
		Score t2Points = t2Morning.add(t2Afternoon);

		int resultBoth = t1Points.compareToWithTime(t2Points);
		if (resultBoth != 0) {
			return resultBoth;
		}

		return t1Morning.compareToWithTime(t2Morning);
	}

	private Score getBestRoundMorning(Team team) {
		List<Score> scores = team.getScore().getScoreSlide().getScores();
		Score s1 = scores.size() > 0 ? scores.get(0) : null;
		Score s2 = scores.size() > 1 ? scores.get(1) : null;
		if (s1 == null && s2 == null) return null;
		if (s1 == null) return s2;
		if (s2 == null) return s1;
		return getBetterRound(s1, s2);
	}

	private Score getBetterRound(Score s1, Score s2) {
		if (s1.getPoints() > s2.getPoints()) {
			return s1;
		} else if (s1.getPoints() < s2.getPoints()) {
			return s2;
		} else {
			return s1.getTime() < s2.getTime() ? s1 : s2;
		}
	}

	private Score getBestRoundAfternoon(Team team) {
		List<Score> scores = team.getScore().getScoreSlide().getScores();
		Score s3 = scores.size() > 2 ? scores.get(2) : null;
		Score s4 = scores.size() > 3 ? scores.get(3) : null;
		if (s3 == null && s4 == null) return null;
		if (s3 == null) return s4;
		if (s4 == null) return s3;
		return getBetterRound(s3, s4);
	}

	@Override
	public Set<Integer> getHighlightIndices(Team team) {
		Score bestMorning = getBestRoundMorning(team);
		Score bestAfternoon = getBestRoundAfternoon(team);

		Set<Integer> highlightIndices = new HashSet<>();
		for (int i = 0; i < team.getScore().getScoreSlide().getScores().size(); i++) {
			var score = team.getScore().getScoreSlide().getScores().get(i);
			if (score.equals(bestMorning) || score.equals(bestAfternoon)) {
				highlightIndices.add(i);
			}
		}
		return highlightIndices;
	}
}

package de.fll.screen.service.comparators;

import de.fll.screen.model.Score;
import de.fll.screen.model.Team;
import de.fll.core.dto.ScoreDTO;
import de.fll.core.dto.TeamDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

abstract class AbstractFLLComparator implements CategoryComparator {

	protected abstract List<Score> getRelevantScores(Team team);

	// assignRanks返回List<ScoreDTO>，每个ScoreDTO带rank
	protected List<ScoreDTO> assignRanks(Set<Team> teams, Function<Team, Score> scoreExtractor) {
		List<Team> sorted = new ArrayList<>(teams);
		sorted.sort(this);
		List<ScoreDTO> scoreDTOs = new ArrayList<>();
		double previousScore = -1;
		int rank = 0;
		for (int i = 0; i < sorted.size(); i++) {
			Team team = sorted.get(i);
			Score bestScore = scoreExtractor.apply(team);
			if (bestScore != null && bestScore.getPoints() != previousScore) {
				rank = i + 1;
			}
			var highlightIndices = getHighlightIndices(team);
			List<Score> scores = getRelevantScores(team);
			for (int j = 0; j < scores.size(); j++) {
				Score s = scores.get(j);
				if (s == null) continue;
				scoreDTOs.add(ScoreDTO.builder()
					.points(s.getPoints())
					.time(s.getTime())
					.highlight(highlightIndices.contains(team.getScores().indexOf(s)))
					.team(TeamDTO.builder().id(team.getId()).name(team.getName()).build())
					.rank(rank)
					.build());
			}
			if (bestScore != null) {
				previousScore = bestScore.getPoints();
			}
		}
		return scoreDTOs;
	}

	protected int compareOneScore(Team t1, Team t2, int roundIndex) {
		var s1 = t1.getScoreForRound(roundIndex);
		var s2 = t2.getScoreForRound(roundIndex);
		if (s1 == null && s2 == null) {
			return 0;
		} else if (s1 == null) {
			return 1;
		} else if (s2 == null) {
			return -1;
		}
		return -s1.compareToWithTime(s2);
	}
}

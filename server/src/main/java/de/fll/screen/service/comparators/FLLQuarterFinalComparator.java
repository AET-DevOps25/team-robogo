package de.fll.screen.service.comparators;

import de.fll.screen.model.Score;
import de.fll.screen.model.Team;
import de.fll.core.dto.ScoreDTO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class FLLQuarterFinalComparator extends AbstractFLLComparator {

	// TODO: Implement score extraction such that only the relevant scores are used here.

	@Override
	public Set<Integer> getHighlightIndices(Team team) {
		// Only one score for the quarter final, don't highlight anything
		return Collections.emptySet();
	}

	@Override
	public int compare(Team t1, Team t2) {
		return compareOneScore(t1, t2, 0); // Assumes that only the one score is given at index 0
	}

	@Override
	public List<ScoreDTO> assignRanks(Set<Team> teams) {
		// Quarter final only has 8 competing teams
		return assignRanks(teams, (team -> team.getScoreForRound(0))).subList(0, Math.min(8, teams.size()));
	}

	@Override
	protected List<Score> getRelevantScores(Team team) {
		var scores = team.getScores();
		if (scores.isEmpty()) {
			return Collections.emptyList();
		}
		return Collections.singletonList(scores.get(0));
	}
}

package de.fll.screen.service.comparators;

import de.fll.screen.model.Score;
import de.fll.screen.model.Team;
import de.fll.core.dto.ScoreDTO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class FLLTestRoundComparator extends AbstractFLLComparator {

	// TODO: Implement score extraction and saving

	@Override
	protected List<Score> getRelevantScores(Team team) {
		// TODO
		return Collections.emptyList();
	}

	@Override
	public Set<Integer> getHighlightIndices(Team team) {
		// No highlighting for test rounds
		return Set.of();
	}

	@Override
	public List<ScoreDTO> assignRanks(Set<Team> teams) {
		return assignRanks(teams, team -> team.getScores().isEmpty() ? null : team.getScores().get(0));
	}

	@Override
	public int compare(Team o1, Team o2) {
		return compareOneScore(o1, o2, 0);
	}
}

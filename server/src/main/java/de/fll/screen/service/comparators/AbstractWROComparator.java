package de.fll.screen.service.comparators;

import de.fll.screen.model.Score;
import de.fll.screen.model.Team;
import de.fll.core.dto.TeamDTO;
import de.fll.core.dto.ScoreDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

abstract class AbstractWROComparator implements CategoryComparator {

	// assignRanks返回List<ScoreDTO>，每个ScoreDTO带rank
	public List<ScoreDTO> assignRanks(Set<Team> teams) {
		List<Team> sorted = new ArrayList<>(teams);
		sorted.sort(this);
		List<ScoreDTO> scoreDTOs = new ArrayList<>();
		int rank = 0;
		for (int i = 0; i < sorted.size(); i++) {
			Team team = sorted.get(i);
			var highlightIndices = getHighlightIndices(team);
			if (i == 0 || compare(sorted.get(i - 1), team) != 0) {
				// New rank
				rank = i + 1;
			}
			if (rank != 1 && team.getScore().getScoreSlide().getScores().stream().noneMatch(score -> score.getPoints() > 0)) {
				// Skip teams with 0 points
				// Except when everyone has 0 points (all rank 1)
				continue;
			}
			List<Score> scores = team.getScore().getScoreSlide().getScores();
			for (int j = 0; j < scores.size(); j++) {
				Score score = scores.get(j);
				if (score == null) continue;
				scoreDTOs.add(ScoreDTO.builder()
					.points(score.getPoints())
					.time(score.getTime())
					.highlight(highlightIndices.contains(j))
					.team(TeamDTO.builder().id(team.getId()).name(team.getName()).build())
					.rank(rank)
					.build());
			}
		}
		return scoreDTOs;
	}
}

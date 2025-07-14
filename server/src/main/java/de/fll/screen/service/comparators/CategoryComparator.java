package de.fll.screen.service.comparators;

import de.fll.screen.model.Team;
import de.fll.core.dto.TeamDTO;
import de.fll.core.dto.ScoreDTO;

import java.util.Comparator;
import java.util.List;
import java.util.Set;


public interface CategoryComparator extends Comparator<Team> {

	Set<Integer> getHighlightIndices(Team team);

	/**
	 * Use the comparator to assign ranks to the teams and convert to ScoreDTOs
	 */
	List<ScoreDTO> assignRanks(Set<Team> teams);

}

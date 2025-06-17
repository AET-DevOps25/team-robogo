package de.fll.screen.service.comparators;

import de.fll.core.proto.TeamOuterClass;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public interface CategoryComparator extends Comparator<TeamOuterClass.Team> {

	Set<Integer> getHighlightIndices(TeamOuterClass.Team team);

	/**
	 * Use the comparator to assign ranks to the teams and convert to TeamOuterClass.Team
	 */
	List<TeamOuterClass.Team> assignRanks(Set<TeamOuterClass.Team> teams);

}

package de.fll.screen.assembler;

import de.fll.core.dto.TeamDTO;
import de.fll.screen.model.Team;
import de.fll.screen.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class TeamAssembler implements AbstractDTOAssembler<Team, TeamDTO> {
    @Autowired
    private TeamRepository teamRepository;

    @Override
    public TeamDTO toDTO(Team team) {
        if (team == null) return null;
        return TeamDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .build();
    }

    @Override
    public Team fromDTO(TeamDTO dto) {
        if (dto == null) return null;
        Team team = teamRepository.findById(dto.getId()).orElse(null);
        if (team == null) {
            throw new IllegalArgumentException("Team not found for id: " + dto.getId());
        }
        return team;
    }
} 
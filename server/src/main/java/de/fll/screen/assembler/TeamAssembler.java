package de.fll.screen.assembler;

import de.fll.core.dto.TeamDTO;
import de.fll.screen.model.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamAssembler implements AbstractDTOAssembler<Team, TeamDTO> {
    @Override
    public TeamDTO toDTO(Team team) {
        if (team == null) return null;
        return TeamDTO.builder()
                .id(team.getId())
                .name(team.getName())
                // 可根据需要补充其它字段
                .build();
    }

    @Override
    public Team fromDTO(TeamDTO dto) {
        if (dto == null) return null;
        Team team = new Team();
        team.setId(dto.getId());
        team.setName(dto.getName());
        // 可根据需要补充其它字段
        return team;
    }
} 
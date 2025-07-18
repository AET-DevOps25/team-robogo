package de.fll.screen.controller;

import de.fll.core.dto.TeamDTO;
import de.fll.screen.model.Team;
import de.fll.screen.assembler.TeamAssembler;
import de.fll.screen.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teams")
public class TeamController {
    @Autowired
    private TeamAssembler teamAssembler;
    @Autowired
    private TeamService teamService;

    // 获取所有团队
    @GetMapping
    public List<TeamDTO> getAllTeams() {
        return teamService.getAllTeams().stream()
                .map(teamAssembler::toDTO)
                .collect(Collectors.toList());
    }

    // 获取单个团队
    @GetMapping("/{id}")
    public TeamDTO getTeamById(@PathVariable Long id) {
        Team team = teamService.getTeamById(id);
        return teamAssembler.toDTO(team);
    }

    // 创建团队
    @PostMapping
    public TeamDTO createTeam(@RequestBody TeamDTO teamDTO) {
        Team team = new Team();
        team.setName(teamDTO.getName());
        Team saved = teamService.createTeam(team);
        return teamAssembler.toDTO(saved);
    }

    // 更新团队
    @PutMapping("/{id}")
    public TeamDTO updateTeam(@PathVariable Long id, @RequestBody TeamDTO teamDTO) {
        Team teamDetails = new Team();
        teamDetails.setName(teamDTO.getName());
        
        Team saved = teamService.updateTeam(id, teamDetails);
        return teamAssembler.toDTO(saved);
    }

    // 更新团队分类
    @PutMapping("/{id}/category")
    public TeamDTO updateTeamCategory(@PathVariable Long id, @RequestBody Long categoryId) {
        Team saved = teamService.updateTeamCategory(id, categoryId);
        return teamAssembler.toDTO(saved);
    }

    // 删除团队
    @DeleteMapping("/{id}")
    public void deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
    }
} 
package de.fll.screen.controller;

import de.fll.core.dto.TeamDTO;
import de.fll.screen.model.Team;
import de.fll.screen.model.Category;
import de.fll.screen.repository.TeamRepository;
import de.fll.screen.repository.CategoryRepository;
import de.fll.screen.assembler.TeamAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teams")
public class TeamController {
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TeamAssembler teamAssembler;

    // 获取所有团队
    @GetMapping
    public List<TeamDTO> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(teamAssembler::toDTO)
                .collect(Collectors.toList());
    }

    // 获取单个团队
    @GetMapping("/{id}")
    public TeamDTO getTeamById(@PathVariable Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        return teamAssembler.toDTO(team);
    }

    // 创建团队
    @PostMapping
    public TeamDTO createTeam(@RequestBody TeamDTO teamDTO) {
        Team team = new Team();
        team.setName(teamDTO.getName());
        Team saved = teamRepository.save(team);
        return teamAssembler.toDTO(saved);
    }

    // 更新团队分类
    @PutMapping("/{id}/category")
    public TeamDTO updateTeamCategory(@PathVariable Long id, @RequestBody Long categoryId) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        
        team.setCategory(category);
        Team saved = teamRepository.save(team);
        return teamAssembler.toDTO(saved);
    }

    // 删除团队
    @DeleteMapping("/{id}")
    public void deleteTeam(@PathVariable Long id) {
        teamRepository.deleteById(id);
    }
} 
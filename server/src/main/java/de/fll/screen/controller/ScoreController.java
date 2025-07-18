package de.fll.screen.controller;

import de.fll.core.dto.ScoreDTO;
import de.fll.core.dto.ScoreSlideDTO;
import de.fll.core.dto.CategoryDTO;
import de.fll.screen.model.Score;
import de.fll.screen.model.ScoreSlide;
import de.fll.screen.model.Category;
import de.fll.screen.service.ScoreService;
import de.fll.screen.assembler.ScoreSlideAssembler;
import de.fll.screen.assembler.CategoryAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/scores")
public class ScoreController {
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private ScoreSlideAssembler scoreSlideAssembler;

    // 根据分类获取分数（包含排名和高亮信息）
    @GetMapping("/category/{categoryId}")
    public List<ScoreDTO> getScoresByCategory(@PathVariable Long categoryId) {
        return scoreService.getScoreDTOsWithHighlight(categoryId);
    }

    // 添加分数
    @PostMapping
    public ScoreDTO addScore(@RequestBody ScoreDTO scoreDTO) {
        if (scoreDTO.getTeam() == null || scoreDTO.getTeam().getId() == null) {
            throw new IllegalArgumentException("Team id required");
        }
        Score score = scoreService.addScore(scoreDTO.getTeam().getId(), scoreDTO.getPoints(), scoreDTO.getTime());
        ScoreDTO dto = new ScoreDTO();
        dto.setId(score.getId());
        dto.setPoints(score.getPoints());
        dto.setTime(score.getTime());
        if (score.getTeam() != null) {
            dto.setTeam(de.fll.core.dto.TeamDTO.builder()
                    .id(score.getTeam().getId())
                    .name(score.getTeam().getName())
                    .build());
        }
        return dto;
    }

    // 更新分数
    @PutMapping("/{id}")
    public ScoreDTO updateScore(@PathVariable Long id, @RequestBody ScoreDTO scoreDTO) {
        if (scoreDTO.getTeam() == null || scoreDTO.getTeam().getId() == null) {
            throw new IllegalArgumentException("Team id required");
        }
        Score score = scoreService.updateScore(id, scoreDTO.getTeam().getId(), scoreDTO.getPoints(), scoreDTO.getTime());
        ScoreDTO dto = new ScoreDTO();
        dto.setId(score.getId());
        dto.setPoints(score.getPoints());
        dto.setTime(score.getTime());
        if (score.getTeam() != null) {
            dto.setTeam(de.fll.core.dto.TeamDTO.builder()
                    .id(score.getTeam().getId())
                    .name(score.getTeam().getName())
                    .build());
        }
        return dto;
    }

    // 删除分数
    @DeleteMapping("/{id}")
    public void deleteScore(@PathVariable Long id) {
        scoreService.deleteScore(id);
    }

    // 创建ScoreSlide
    @PostMapping("/slide")
    public ScoreSlideDTO createScoreSlide(@RequestBody ScoreSlideDTO scoreSlideDTO) {
        ScoreSlide slide = scoreService.createScoreSlideFromDTO(scoreSlideDTO);
        return scoreSlideAssembler.toDTO(slide);
    }
} 
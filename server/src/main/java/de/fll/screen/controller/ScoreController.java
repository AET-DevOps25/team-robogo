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
    @Autowired
    private CategoryAssembler categoryAssembler;

    // 获取所有分数
    @GetMapping
    public List<ScoreDTO> getAllScores() {
        return scoreService.getScoresForAllTeams().stream()
                .map(score -> {
                    ScoreDTO dto = new ScoreDTO();
                    dto.setPoints(score.getPoints());
                    dto.setTime(score.getTime());
                    // 这里可补充team、rank等
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 根据分类获取分数高亮
    @PostMapping("/highlight")
    public List<ScoreDTO> getScoresWithHighlight(@RequestBody CategoryDTO categoryDTO) {
        Category category = categoryAssembler.fromDTO(categoryDTO);
        return scoreService.getScoreDTOsWithHighlight(category);
    }

    // 添加分数
    @PostMapping
    public ScoreDTO addScore(@RequestBody ScoreDTO scoreDTO) {
        if (scoreDTO.getTeam() == null || scoreDTO.getTeam().getId() == null) {
            throw new IllegalArgumentException("Team id required");
        }
        Score score = scoreService.addScore(scoreDTO.getTeam().getId(), scoreDTO.getPoints(), scoreDTO.getTime());
        ScoreDTO dto = new ScoreDTO();
        dto.setPoints(score.getPoints());
        dto.setTime(score.getTime());
        // 这里可补充team、rank等
        return dto;
    }

    // 创建ScoreSlide
    @PostMapping("/slide")
    public ScoreSlideDTO createScoreSlide(@RequestBody ScoreSlideDTO scoreSlideDTO) {
        ScoreSlide slide = scoreService.createScoreSlideFromDTO(scoreSlideDTO);
        return scoreSlideAssembler.toDTO(slide);
    }
} 
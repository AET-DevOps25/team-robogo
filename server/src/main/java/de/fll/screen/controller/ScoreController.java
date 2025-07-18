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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/scores")
@Tag(name = "Score Management", description = "Team score management and score slide related APIs")
public class ScoreController {
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private ScoreSlideAssembler scoreSlideAssembler;

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get Scores by Category", description = "Gets scores of all teams in specified category, including ranking and highlight information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Retrieved successfully", 
                    content = @Content(schema = @Schema(implementation = ScoreDTO.class))),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public List<ScoreDTO> getScoresByCategory(
            @Parameter(description = "Category ID", required = true)
            @PathVariable Long categoryId) {
        return scoreService.getScoreDTOsWithHighlight(categoryId);
    }

    @PostMapping
    @Operation(summary = "Add Score", description = "Adds new score record for specified team")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Added successfully", 
                    content = @Content(schema = @Schema(implementation = ScoreDTO.class))),
        @ApiResponse(responseCode = "400", description = "Bad request parameters"),
        @ApiResponse(responseCode = "404", description = "Team not found")
    })
    public ScoreDTO addScore(
            @Parameter(description = "Score information", required = true)
            @RequestBody ScoreDTO scoreDTO) {
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
package de.fll.screen.service;

import de.fll.core.dto.ScoreDTO;
import de.fll.core.dto.ScoreSlideDTO;
import de.fll.screen.model.Score;
import de.fll.screen.model.Team;
import de.fll.screen.model.Category;
import de.fll.screen.model.ScoreSlide;
import de.fll.screen.repository.ScoreRepository;
import de.fll.screen.repository.TeamRepository;
import de.fll.screen.repository.CategoryRepository;
import de.fll.screen.repository.SlideRepository;
import de.fll.screen.repository.SlideDeckRepository;
import de.fll.screen.assembler.TeamAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ScoreService {
    private final ScoreRepository scoreRepository;
    private final TeamRepository teamRepository;
    private final CategoryRepository categoryRepository;
    private final SlideRepository slideRepository;
    private final SlideDeckRepository slideDeckRepository;
    private final TeamAssembler teamAssembler;
    private final RankingService rankingService;

    @Autowired
    public ScoreService(
        ScoreRepository scoreRepository,
        TeamRepository teamRepository,
        CategoryRepository categoryRepository,
        SlideRepository slideRepository,
        SlideDeckRepository slideDeckRepository,
        TeamAssembler teamAssembler,
        RankingService rankingService
    ) {
        this.scoreRepository = scoreRepository;
        this.teamRepository = teamRepository;
        this.categoryRepository = categoryRepository;
        this.slideRepository = slideRepository;
        this.slideDeckRepository = slideDeckRepository;
        this.teamAssembler = teamAssembler;
        this.rankingService = rankingService;
    }

    public Score addScore(Long teamId, double points, int time) {
        Optional<Score> existingScore = scoreRepository.findByTeam_Id(teamId);
        Score score;
        if (existingScore.isPresent()) {
            score = existingScore.get();
            score.setPoints(points);
            score.setTime(time);
        } else {
            score = new Score(points, time);
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));
            score.setTeam(team);
        }
        
        Score savedScore = scoreRepository.save(score);
        
        // 更新相关的SlideDeck版本
        updateSlideDeckVersionsForScore(savedScore);
        
        return savedScore;
    }

    public Score updateScore(Long scoreId, Long teamId, double points, int time) {
        Score score = scoreRepository.findById(scoreId)
                .orElseThrow(() -> new IllegalArgumentException("Score not found: " + scoreId));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));
        
        score.setPoints(points);
        score.setTime(time);
        score.setTeam(team);
        
        Score savedScore = scoreRepository.save(score);
        
        // 更新相关的SlideDeck版本
        updateSlideDeckVersionsForScore(savedScore);
        
        return savedScore;
    }

    public void deleteScore(Long scoreId) {
        Score score = scoreRepository.findById(scoreId)
                .orElseThrow(() -> new IllegalArgumentException("Score not found: " + scoreId));
        
        // 在删除前更新相关的SlideDeck版本
        updateSlideDeckVersionsForScore(score);
        
        scoreRepository.delete(score);
    }

    /**
     * 更新包含特定Score的SlideDeck版本
     * 当Score发生变化时，需要更新显示该Score的SlideDeck版本
     */
    private void updateSlideDeckVersionsForScore(Score score) {
        if (score == null || score.getTeam() == null || score.getTeam().getCategory() == null) {
            return;
        }
        
        Long categoryId = score.getTeam().getCategory().getId();
        Long scoreId = score.getId();
        
        // 查找包含该Score的SlideDeck
        List<de.fll.screen.model.SlideDeck> affectedSlideDecks = 
            slideDeckRepository.findSlideDecksByScore(scoreId, categoryId);
        
        // 更新每个受影响SlideDeck的版本
        for (de.fll.screen.model.SlideDeck slideDeck : affectedSlideDecks) {
            slideDeck.setVersion(incrementVersion(slideDeck.getVersion()));
            slideDeckRepository.save(slideDeck);
        }
    }

    /**
     * 安全地递增版本号，处理溢出问题
     * 当版本号接近最大值时，重置为1
     */
    private int incrementVersion(int currentVersion) {
        // 当版本号达到 2,000,000,000 时重置为 1，避免溢出
        if (currentVersion >= 2000000000) {
            return 1;
        }
        return currentVersion + 1;
    }

    public List<Score> getScoresForAllTeams() {
        return scoreRepository.findAll();
    }

    public List<Score> getScoresByCategoryId(Long categoryId) {
        return categoryRepository.findScoresByCategoryId(categoryId);
    }

    public List<Score> getScoresByScoreSlide(ScoreSlide scoreSlide) {
        if (scoreSlide == null || scoreSlide.getCategory() == null) {
            return new ArrayList<>();
        }
        return slideRepository.findScoresByScoreSlideCategory(scoreSlide.getCategory().getId());
    }

    public List<ScoreDTO> getScoreDTOsWithHighlight(Category category) {
        if (category == null) return List.of();
        return rankingService.getRankedTeams(category, categoryRepository.findTeamsByCategoryId(category.getId()));
    }

    public List<ScoreDTO> getScoreDTOsWithHighlight(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) return List.of();
        return rankingService.getRankedTeams(category, categoryRepository.findTeamsByCategoryId(categoryId));
    }

    public List<ScoreDTO> getAllTeamsScoreDTOsWithHighlight(List<Category> categories) {
        List<ScoreDTO> allScoreDTOs = new ArrayList<>();
        for (Category category : categories) {
            allScoreDTOs.addAll(getScoreDTOsWithHighlight(category));
        }
        return allScoreDTOs;
    }

    public ScoreSlide createScoreSlideFromDTO(ScoreSlideDTO dto) {
        if (dto == null) return null;
        ScoreSlide slide = new ScoreSlide();
        slide.setName(dto.getName());
        slide.setIndex(dto.getIndex());
        if (dto.getCategory() != null) {
            Category category = categoryRepository.findById(dto.getCategory().getId()).orElse(null);
            slide.setCategory(category);
        }
        
        // 先保存slide，然后通过slide直接查询相关的scores
        slideRepository.save(slide);
        
        List<Score> scores = new ArrayList<>();
        if (slide.getCategory() != null) {
            // 使用新的方法直接从ScoreSlide查询相关的scores
            scores = getScoresByScoreSlide(slide);
        } else if (dto.getScores() != null) {
            // 如果没有category，则使用DTO中的scores
            for (ScoreDTO scoreDTO : dto.getScores()) {
                Team team = teamAssembler.fromDTO(scoreDTO.getTeam());
                if (team != null) {
                    Score score = new Score();
                    score.setTeam(team);
                    score.setPoints(scoreDTO.getPoints());
                    score.setTime(scoreDTO.getTime());
                    scores.add(score);
                }
            }
        }
        
        for (Score score : scores) {
            scoreRepository.save(score);
        }
        return slide;
    }

} 
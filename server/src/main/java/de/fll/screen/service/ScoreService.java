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
    private final TeamAssembler teamAssembler;
    private final RankingService rankingService;

    @Autowired
    public ScoreService(
        ScoreRepository scoreRepository,
        TeamRepository teamRepository,
        CategoryRepository categoryRepository,
        SlideRepository slideRepository,
        TeamAssembler teamAssembler,
        RankingService rankingService
    ) {
        this.scoreRepository = scoreRepository;
        this.teamRepository = teamRepository;
        this.categoryRepository = categoryRepository;
        this.slideRepository = slideRepository;
        this.teamAssembler = teamAssembler;
        this.rankingService = rankingService;
    }

    public Score addScore(Long teamId, double points, int time) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));
        Score score = new Score(points, time);
        score.setTeam(team);
        team.setScore(score);
        return scoreRepository.save(score);
    }

    public List<Score> getScoresForTeam(Team team) {
        return scoreRepository.findByTeam(team);
    }

    public List<Score> getScoresForAllTeams() {
        return scoreRepository.findAll();
    }

    public List<ScoreDTO> getScoreDTOsWithHighlight(Category category) {
        if (category == null) return List.of();
        return rankingService.getRankedTeams(category, categoryRepository.findTeamsByCategoryId(category.getId()));
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
        List<Score> scores = new ArrayList<>();
        if (dto.getScores() != null) {
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
        slide.setScores(scores);
        slideRepository.save(slide);
        for (Score score : scores) {
            scoreRepository.save(score);
        }
        return slide;
    }

} 
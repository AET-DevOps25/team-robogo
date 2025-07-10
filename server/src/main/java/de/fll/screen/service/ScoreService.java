package de.fll.screen.service;

import de.fll.core.dto.ScoreDTO;
import de.fll.core.dto.ScoreSlideDTO;
import de.fll.screen.model.Score;
import de.fll.screen.model.Team;
import de.fll.screen.model.Category;
import de.fll.screen.model.CategoryScoring;
import de.fll.screen.model.ScoreSlide;
import de.fll.screen.repository.ScoreRepository;
import de.fll.screen.repository.TeamRepository;
import de.fll.screen.repository.CategoryRepository;
import de.fll.screen.repository.SlideRepository;
import de.fll.screen.service.comparators.CategoryComparator;
import de.fll.screen.service.comparators.FLLQuarterFinalComparator;
import de.fll.screen.service.comparators.FLLRobotGameComparator;
import de.fll.screen.service.comparators.FLLTestRoundComparator;
import de.fll.screen.service.comparators.WRO2025Comparator;
import de.fll.screen.service.comparators.WROStarterComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ScoreService {
    private final ScoreRepository scoreRepository;
    private final TeamRepository teamRepository;
    private final CategoryRepository categoryRepository;
    private final SlideRepository slideRepository;
    private final Map<CategoryScoring, CategoryComparator> comparatorMap = new HashMap<>();

    @Autowired
    public ScoreService(
        ScoreRepository scoreRepository,
        TeamRepository teamRepository,
        CategoryRepository categoryRepository,
        SlideRepository slideRepository,
        FLLRobotGameComparator fllRobotGameComparator,
        FLLQuarterFinalComparator fllQuarterFinalComparator,
        FLLTestRoundComparator fllTestRoundComparator,
        WROStarterComparator wroStarterComparator,
        WRO2025Comparator wro2025Comparator
    ) {
        this.scoreRepository = scoreRepository;
        this.teamRepository = teamRepository;
        this.categoryRepository = categoryRepository;
        this.slideRepository = slideRepository;
        comparatorMap.put(CategoryScoring.FLL_ROBOT_GAME, fllRobotGameComparator);
        comparatorMap.put(CategoryScoring.FLL_QUARTER_FINAL, fllQuarterFinalComparator);
        comparatorMap.put(CategoryScoring.FLL_TESTROUND, fllTestRoundComparator);
        comparatorMap.put(CategoryScoring.WRO_STARTER, wroStarterComparator);
        comparatorMap.put(CategoryScoring.WRO_ROBOMISSION_2025, wro2025Comparator);
    }

    public Score addScore(Long teamId, double points, int time) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));
        Score score = new Score(points, time);
        score.setTeam(team);
        team.getScores().add(score);
        return scoreRepository.save(score);
    }

    public List<Score> getScoresForTeam(Team team) {
        return scoreRepository.findByTeam(team);
    }

    public List<Score> getScoresForAllTeams() {
        return scoreRepository.findAll();
    }

    public List<ScoreDTO> getScoreDTOsWithHighlight(Team team) {
        Category category = team.getCategory();
        CategoryComparator comparator = comparatorMap.get(category.getCategoryScoring());
        if (comparator == null) {
            throw new IllegalArgumentException("No comparator for scoring: " + category.getCategoryScoring());
        }
        Set<Integer> highlightIndices = comparator.getHighlightIndices(team);
        List<ScoreDTO> dtos = new ArrayList<>();
        List<Score> scores = team.getScores();
        for (int i = 0; i < scores.size(); i++) {
            Score s = scores.get(i);
            dtos.add(ScoreDTO.builder()
                .points(s.getPoints())
                .time(s.getTime())
                .highlight(highlightIndices.contains(i))
                .build());
        }
        return dtos;
    }

    public List<ScoreDTO> getAllTeamsScoreDTOsWithHighlight(List<Team> teams) {
        List<ScoreDTO> allScoreDTOs = new ArrayList<>();
        for (Team team : teams) {
            allScoreDTOs.addAll(getScoreDTOsWithHighlight(team));
        }
        return allScoreDTOs;
    }

    public ScoreSlide createScoreSlideFromDTO(ScoreSlideDTO dto) {
        if (dto == null) return null;
        ScoreSlide slide = new ScoreSlide();
        slide.setName(dto.getName());
        slide.setIndex(dto.getIndex());
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            slide.setCategory(category);
        }
        List<Score> scores = new ArrayList<>();
        if (dto.getScores() != null) {
            for (ScoreDTO scoreDTO : dto.getScores()) {
                Team team = teamRepository.findById(scoreDTO.getTeamId()).orElse(null);
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
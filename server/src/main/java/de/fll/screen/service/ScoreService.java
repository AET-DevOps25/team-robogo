package de.fll.screen.service;

import de.fll.core.dto.ScoreDTO;
import de.fll.screen.model.Score;
import de.fll.screen.model.Team;
import de.fll.screen.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Map;

@Service
public class ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;

    public List<Score> getScoresForTeam(Team team) {
        return scoreRepository.findByTeam(team);
    }

    public List<Score> getScoresForAllTeams() {
        return scoreRepository.findAll();
    }

    // highlight the highest score for each team
    public List<ScoreDTO> getScoreDTOsWithHighlight(List<Score> scores) {
        double maxPoints = scores.stream()
            .mapToDouble(Score::getPoints)
            .max()
            .orElse(Double.NaN);

        return scores.stream()
            .map(score -> ScoreDTO.builder()
                .points(score.getPoints())
                .time(score.getTime())
                .highlight(score.getPoints() == maxPoints)
                .build())
            .collect(Collectors.toList());
    }

    // group by team, return all teams' scores with highlight for each team
    public List<ScoreDTO> getAllTeamsScoreDTOsWithHighlight(List<Score> allScores) {
        Map<Team, List<Score>> teamScores = allScores.stream().collect(Collectors.groupingBy(Score::getTeam));
        List<ScoreDTO> allScoreDTOs = new ArrayList<>();
        for (List<Score> teamScoreList : teamScores.values()) {
            allScoreDTOs.addAll(getScoreDTOsWithHighlight(teamScoreList));
        }
        return allScoreDTOs;
    }

} 
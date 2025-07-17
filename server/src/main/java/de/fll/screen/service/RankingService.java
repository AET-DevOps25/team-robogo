package de.fll.screen.service;

import de.fll.screen.model.Category;
import de.fll.screen.model.Team;
import de.fll.screen.model.Score;
import de.fll.core.dto.ScoreDTO;
import de.fll.screen.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RankingService {

    @Autowired
    private ScoreRepository scoreRepository;

    public List<ScoreDTO> getRankedTeams(Category category, Set<Team> teams) {
        // 通过ScoreRepository获取该分类下所有有分数的Teams
        List<Score> scores = scoreRepository.findByTeam_Category_Id(category.getId());
        
        // 按分数降序排序
        scores.sort((s1, s2) -> Double.compare(s2.getPoints(), s1.getPoints()));
        
        List<ScoreDTO> result = new ArrayList<>();
        int rank = 1;
        double lastScore = Double.NaN;
        
        for (int i = 0; i < scores.size(); i++) {
            Score score = scores.get(i);
            Team team = score.getTeam();
            
            ScoreDTO.ScoreDTOBuilder builder = ScoreDTO.builder();
            builder.id(score.getId())
                   .points(score.getPoints())
                   .time(score.getTime())
                   .team(de.fll.core.dto.TeamDTO.builder().id(team.getId()).name(team.getName()).build());
            
            // 相同分数并列
            if (score.getPoints() != lastScore) {
                rank = i + 1;
                lastScore = score.getPoints();
            }
            builder.rank(rank);
            // 第一名高亮
            builder.highlight(rank == 1);
            result.add(builder.build());
        }
        return result;
    }
} 
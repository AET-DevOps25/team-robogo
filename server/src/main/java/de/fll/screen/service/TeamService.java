package de.fll.screen.service;

import de.fll.screen.model.Team;
import de.fll.screen.model.Category;
import de.fll.screen.model.Score;
import de.fll.screen.repository.TeamRepository;
import de.fll.screen.repository.CategoryRepository;
import de.fll.screen.repository.ScoreRepository;
import de.fll.screen.repository.SlideDeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final CategoryRepository categoryRepository;
    private final ScoreRepository scoreRepository;
    private final SlideDeckRepository slideDeckRepository;

    @Autowired
    public TeamService(
        TeamRepository teamRepository,
        CategoryRepository categoryRepository,
        ScoreRepository scoreRepository,
        SlideDeckRepository slideDeckRepository
    ) {
        this.teamRepository = teamRepository;
        this.categoryRepository = categoryRepository;
        this.scoreRepository = scoreRepository;
        this.slideDeckRepository = slideDeckRepository;
    }

    public Team createTeam(Team team) {
        Team savedTeam = teamRepository.save(team);
        return savedTeam;
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Team getTeamById(Long id) {
        return teamRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Team not found: " + id));
    }

    public Team updateTeam(Long teamId, Team teamDetails) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));
        
        // 更新team信息
        if (teamDetails.getName() != null) {
            team.setName(teamDetails.getName());
        }
        if (teamDetails.getCategory() != null) {
            team.setCategory(teamDetails.getCategory());
        }
        
        Team savedTeam = teamRepository.save(team);
        
        // 当team信息发生变化时，更新相关的SlideDeck版本
        updateSlideDeckVersionsForTeam(savedTeam);
        
        return savedTeam;
    }

    public Team updateTeamCategory(Long teamId, Long categoryId) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));
        
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
        
        team.setCategory(category);
        Team savedTeam = teamRepository.save(team);
        
        // 当team的category发生变化时，更新相关的SlideDeck版本
        updateSlideDeckVersionsForTeam(savedTeam);
        
        return savedTeam;
    }

    public void deleteTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));
        
        // 在删除前更新相关的SlideDeck版本
        updateSlideDeckVersionsForTeam(team);
        
        teamRepository.delete(team);
    }

    /**
     * 更新包含特定Team的Score的SlideDeck版本
     * 当Team发生变化时，需要更新显示该Team的Score的SlideDeck版本
     */
    private void updateSlideDeckVersionsForTeam(Team team) {
        if (team == null || team.getCategory() == null) {
            return;
        }
        
        Long teamId = team.getId();
        Long categoryId = team.getCategory().getId();
        
        // 查找该team的score
        Score teamScore = scoreRepository.findByTeam_Id(teamId).orElse(null);
        if (teamScore == null) {
            return; // 如果team没有score，不需要更新SlideDeck
        }
        
        // 查找包含该Score的SlideDeck
        List<de.fll.screen.model.SlideDeck> affectedSlideDecks = 
            slideDeckRepository.findSlideDecksByScore(teamScore.getId(), categoryId);
        
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
} 
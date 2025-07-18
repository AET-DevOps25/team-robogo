package de.fll.screen.repository;

import de.fll.screen.model.Category;
import de.fll.screen.model.Competition;
import de.fll.screen.model.Team;
import de.fll.screen.model.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    private Competition competition;
    private Category category1;
    private Category category2;
    private Team team1;
    private Team team2;
    private Team team3;
    private Score score1;
    private Score score2;

    @BeforeEach
    void setUp() {
        // 创建测试数据
        competition = new Competition();
        competition.setName("Test Competition");
        competition.setInternalId(UUID.randomUUID());
        competition = competitionRepository.save(competition);

        category1 = new Category();
        category1.setName("Test Category 1");
        category1.setCompetition(competition);
        category1 = categoryRepository.save(category1);

        category2 = new Category();
        category2.setName("Test Category 2");
        category2.setCompetition(competition);
        category2 = categoryRepository.save(category2);

        team1 = new Team();
        team1.setName("Test Team 1");
        team1.setCategory(category1);
        team1 = teamRepository.save(team1);

        team2 = new Team();
        team2.setName("Test Team 2");
        team2.setCategory(category1);
        team2 = teamRepository.save(team2);

        team3 = new Team();
        team3.setName("Test Team 3");
        team3.setCategory(category2);
        team3 = teamRepository.save(team3);

        score1 = new Score();
        score1.setPoints(100.0);
        score1.setTime(120);
        score1.setTeam(team1);
        score1 = scoreRepository.save(score1);

        score2 = new Score();
        score2.setPoints(200.0);
        score2.setTime(180);
        score2.setTeam(team2);
        score2 = scoreRepository.save(score2);
    }

    @Test
    void findTeamsByCategoryId_ShouldReturnTeamsForCategory() {
        // Act
        Set<Team> result = categoryRepository.findTeamsByCategoryId(category1.getId());

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting("id").contains(team1.getId(), team2.getId());
    }

    @Test
    void findTeamsByCategoryId_ShouldReturnEmptySet_WhenCategoryDoesNotExist() {
        // Act
        Set<Team> result = categoryRepository.findTeamsByCategoryId(999L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findTeamsByCategoryId_ShouldReturnEmptySet_WhenCategoryHasNoTeams() {
        // Arrange - 创建一个没有团队的 category
        Category emptyCategory = new Category();
        emptyCategory.setName("Empty Category");
        emptyCategory.setCompetition(competition);
        emptyCategory = categoryRepository.save(emptyCategory);

        // Act
        Set<Team> result = categoryRepository.findTeamsByCategoryId(emptyCategory.getId());

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findScoresByCategoryId_ShouldReturnScoresForCategory() {
        // Act
        List<Score> result = categoryRepository.findScoresByCategoryId(category1.getId());

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting("id").contains(score1.getId(), score2.getId());
    }

    @Test
    void findScoresByCategoryId_ShouldReturnEmptyList_WhenCategoryDoesNotExist() {
        // Act
        List<Score> result = categoryRepository.findScoresByCategoryId(999L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findScoresByCategoryId_ShouldReturnEmptyList_WhenCategoryHasNoScores() {
        // Act
        List<Score> result = categoryRepository.findScoresByCategoryId(category2.getId());

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findScoresByCategoryId_ShouldReturnScoresForMultipleTeamsInCategory() {
        // Arrange - 为 category2 添加一个团队和分数
        Score score3 = new Score();
        score3.setPoints(300.0);
        score3.setTime(240);
        score3.setTeam(team3);
        score3 = scoreRepository.save(score3);

        // Act
        List<Score> result = categoryRepository.findScoresByCategoryId(category2.getId());

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(score3.getId());
    }
} 
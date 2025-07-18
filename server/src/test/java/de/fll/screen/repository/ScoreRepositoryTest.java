package de.fll.screen.repository;

import de.fll.screen.model.Score;
import de.fll.screen.model.Team;
import de.fll.screen.model.Category;
import de.fll.screen.model.Competition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class ScoreRepositoryTest {

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    private Competition competition;
    private Category category1;
    private Category category2;
    private Team team1;
    private Team team2;
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
    void findByTeam_Id_ShouldReturnScore_WhenScoreExists() {
        // Act
        Optional<Score> result = scoreRepository.findByTeam_Id(team1.getId());

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(score1.getId());
        assertThat(result.get().getTeam().getId()).isEqualTo(team1.getId());
    }

    @Test
    void findByTeam_Id_ShouldReturnEmpty_WhenScoreDoesNotExist() {
        // Act
        Optional<Score> result = scoreRepository.findByTeam_Id(999L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByTeam_Category_Id_ShouldReturnScoresForCategory() {
        // Act
        List<Score> result = scoreRepository.findByTeam_Category_Id(category1.getId());

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting("id").contains(score1.getId(), score2.getId());
    }

    @Test
    void findByTeam_Category_Id_ShouldReturnEmptyList_WhenCategoryDoesNotExist() {
        // Act
        List<Score> result = scoreRepository.findByTeam_Category_Id(999L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByTeamIn_ShouldReturnScoresForTeams() {
        // Arrange
        List<Team> teams = List.of(team1, team2);

        // Act
        List<Score> result = scoreRepository.findByTeamIn(teams);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting("id").contains(score1.getId(), score2.getId());
    }

    @Test
    void findByTeamIn_ShouldReturnEmptyList_WhenNoTeamsProvided() {
        // Act
        List<Score> result = scoreRepository.findByTeamIn(List.of());

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByTeamIn_ShouldReturnScoresForSpecificTeam() {
        // Arrange
        List<Team> teams = List.of(team1);

        // Act
        List<Score> result = scoreRepository.findByTeamIn(teams);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(score1.getId());
    }

    @Test
    void findByTeamIn_ShouldReturnEmptyList_WhenTeamsDoNotHaveScores() {
        // Arrange
        Team teamWithoutScore = new Team();
        teamWithoutScore.setName("Team Without Score");
        teamWithoutScore.setCategory(category2);
        teamWithoutScore = teamRepository.save(teamWithoutScore);

        List<Team> teams = List.of(teamWithoutScore);

        // Act
        List<Score> result = scoreRepository.findByTeamIn(teams);

        // Assert
        assertThat(result).isEmpty();
    }
} 
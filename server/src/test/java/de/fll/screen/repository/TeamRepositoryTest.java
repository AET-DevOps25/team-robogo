package de.fll.screen.repository;

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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class TeamRepositoryTest {

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
    private Team team3;

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
    }

    @Test
    void findByCategory_ShouldReturnTeamsForCategory() {
        // Act
        List<Team> result = teamRepository.findByCategory(category1);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting("id").contains(team1.getId(), team2.getId());
    }

    @Test
    void findByCategory_ShouldReturnEmptyList_WhenCategoryHasNoTeams() {
        // Arrange - 创建一个没有团队的 category
        Category emptyCategory = new Category();
        emptyCategory.setName("Empty Category");
        emptyCategory.setCompetition(competition);
        emptyCategory = categoryRepository.save(emptyCategory);

        // Act
        List<Team> result = teamRepository.findByCategory(emptyCategory);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByCategory_ShouldReturnSingleTeam_WhenCategoryHasOneTeam() {
        // Act
        List<Team> result = teamRepository.findByCategory(category2);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(team3.getId());
    }

    @Test
    void findByCategory_ShouldReturnEmptyList_WhenCategoryIsNull() {
        // Act
        List<Team> result = teamRepository.findByCategory(null);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByCategory_ShouldReturnTeamsInCorrectOrder() {
        // Arrange - 创建更多团队来测试顺序
        Team team4 = new Team();
        team4.setName("Test Team 4");
        team4.setCategory(category1);
        team4 = teamRepository.save(team4);

        // Act
        List<Team> result = teamRepository.findByCategory(category1);

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result).extracting("id").contains(team1.getId(), team2.getId(), team4.getId());
    }
} 
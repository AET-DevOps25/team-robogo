package de.fll.screen.repository;

import de.fll.screen.model.Slide;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.model.ScoreSlide;
import de.fll.screen.model.ImageSlide;
import de.fll.screen.model.Category;
import de.fll.screen.model.Team;
import de.fll.screen.model.Score;
import de.fll.screen.model.Competition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class SlideRepositoryTest {

    @Autowired
    private SlideRepository slideRepository;

    @Autowired
    private SlideDeckRepository slideDeckRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private EntityManager entityManager;

    private Competition competition;
    private Category category;
    private Team team;
    private Score score;
    private SlideDeck slideDeck;
    private ScoreSlide scoreSlide;

    @BeforeEach
    void setUp() {
        // 创建测试数据
        competition = new Competition();
        competition.setName("Test Competition");
        competition.setInternalId(UUID.randomUUID());
        competition = competitionRepository.save(competition);

        category = new Category();
        category.setName("Test Category");
        category.setCompetition(competition);
        category = categoryRepository.save(category);

        team = new Team();
        team.setName("Test Team");
        team.setCategory(category);
        team = teamRepository.save(team);

        score = new Score();
        score.setPoints(100.0);
        score.setTime(120);
        score.setTeam(team);
        score = scoreRepository.save(score);

        slideDeck = new SlideDeck();
        slideDeck.setName("Test SlideDeck");
        slideDeck.setTransitionTime(1000);
        slideDeck.setCompetition(competition);
        slideDeck = slideDeckRepository.save(slideDeck);

        scoreSlide = new ScoreSlide();
        scoreSlide.setName("Test ScoreSlide");
        scoreSlide.setIndex(0);
        scoreSlide.setSlidedeck(slideDeck);
        scoreSlide.setCategory(category);
        scoreSlide = (ScoreSlide) slideRepository.save(scoreSlide);
    }

    @Test
    void findBySlidedeck_ShouldReturnSlidesForDeck() {
        // Arrange
        SlideDeck deck2 = new SlideDeck();
        deck2.setName("Test Deck 2");
        deck2.setTransitionTime(1000);
        deck2.setCompetition(competition);
        deck2 = slideDeckRepository.save(deck2);

        ScoreSlide slide2 = new ScoreSlide();
        slide2.setName("Test ScoreSlide 2");
        slide2.setIndex(0);
        slide2.setSlidedeck(deck2);
        slide2.setCategory(category);
        slideRepository.save(slide2);

        // Act
        List<Slide> result = slideRepository.findBySlidedeck(slideDeck);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(scoreSlide.getId());
    }

    @Test
    void findBySlidedeck_Id_ShouldReturnSlidesForDeckId() {
        // Act
        List<Slide> result = slideRepository.findBySlidedeck_Id(slideDeck.getId());

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(scoreSlide.getId());
    }

    @Test
    void findBySlidedeck_Id_ShouldReturnEmptyList_WhenDeckDoesNotExist() {
        // Act
        List<Slide> result = slideRepository.findBySlidedeck_Id(999L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findScoresByScoreSlideCategory_ShouldReturnScoresForCategory() {
        // Act
        List<Score> result = slideRepository.findScoresByScoreSlideCategory(category.getId());

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(score.getId());
    }

    @Test
    void findScoresByScoreSlideCategory_ShouldReturnEmptyList_WhenCategoryDoesNotExist() {
        // Act
        List<Score> result = slideRepository.findScoresByScoreSlideCategory(999L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void deleteSlideFromDeck_ShouldDeleteSlideFromSpecificDeck() {
        // Arrange
        SlideDeck deck2 = new SlideDeck();
        deck2.setName("Test Deck 2");
        deck2.setTransitionTime(1000);
        deck2.setCompetition(competition);
        deck2 = slideDeckRepository.save(deck2);

        ScoreSlide slide2 = new ScoreSlide();
        slide2.setName("Test ScoreSlide 2");
        slide2.setIndex(0);
        slide2.setSlidedeck(deck2);
        slide2.setCategory(category);
        slide2 = (ScoreSlide) slideRepository.save(slide2);

        // Act
        slideRepository.deleteSlideFromDeck(deck2.getId(), slide2.getId());

        // Assert
        slideRepository.flush();
        entityManager.clear();

        Slide deletedSlide = slideRepository.findById(slide2.getId()).orElse(null);
        Slide existingSlide = slideRepository.findById(scoreSlide.getId()).orElse(null);

        assertThat(deletedSlide).isNull();
        assertThat(existingSlide).isNotNull();
    }

    @Test
    void deleteSlideFromDeck_ShouldNotDeleteSlideFromWrongDeck() {
        // Arrange
        SlideDeck deck2 = new SlideDeck();
        deck2.setName("Test Deck 2");
        deck2.setTransitionTime(1000);
        deck2.setCompetition(competition);
        deck2 = slideDeckRepository.save(deck2);

        // Act - 尝试从错误的 deck 删除 slide
        slideRepository.deleteSlideFromDeck(deck2.getId(), scoreSlide.getId());

        // Assert
        slideRepository.flush();
        entityManager.clear();

        Slide slide = slideRepository.findById(scoreSlide.getId()).orElse(null);
        assertThat(slide).isNotNull(); // slide 应该仍然存在
    }

    @Test
    void deleteSlideFromDeck_ShouldNotDeleteNonExistentSlide() {
        // Act
        slideRepository.deleteSlideFromDeck(slideDeck.getId(), 999L);

        // Assert - 不应该抛出异常，只是不删除任何内容
        slideRepository.flush();
        entityManager.clear();

        Slide slide = slideRepository.findById(scoreSlide.getId()).orElse(null);
        assertThat(slide).isNotNull(); // 现有的 slide 应该仍然存在
    }
} 
package de.fll.screen.repository;

import de.fll.screen.model.SlideDeck;
import de.fll.screen.model.Slide;
import de.fll.screen.model.ScoreSlide;
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
public class SlideDeckRepositoryTest {

    @Autowired
    private SlideDeckRepository slideDeckRepository;

    @Autowired
    private SlideRepository slideRepository;

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
    void setSlidesIndexNegative_ShouldSetNegativeIndexes() {
        // Arrange
        SlideDeck deck = new SlideDeck();
        deck.setName("Test Deck");
        deck.setTransitionTime(1000);
        deck.setCompetition(competition);
        deck = slideDeckRepository.save(deck);

        Slide slide1 = new ScoreSlide();
        slide1.setName("Slide 1");
        slide1.setIndex(0);
        slide1.setSlidedeck(deck);
        slide1 = slideRepository.save(slide1);

        Slide slide2 = new ScoreSlide();
        slide2.setName("Slide 2");
        slide2.setIndex(1);
        slide2.setSlidedeck(deck);
        slide2 = slideRepository.save(slide2);

        // Act
        slideDeckRepository.setSlidesIndexNegative(deck.getId());

        // Assert
        slideRepository.flush();
        entityManager.clear();

        Slide updatedSlide1 = slideRepository.findById(slide1.getId()).orElse(null);
        Slide updatedSlide2 = slideRepository.findById(slide2.getId()).orElse(null);

        assertThat(updatedSlide1).isNotNull();
        assertThat(updatedSlide2).isNotNull();
        assertThat(updatedSlide1.getIndex()).isEqualTo(-1000); // -0 - 1000
        assertThat(updatedSlide2.getIndex()).isEqualTo(-1001); // -1 - 1000
    }

    @Test
    void updateSlideIndexById_ShouldUpdateSlideIndex() {
        // Arrange
        SlideDeck deck = new SlideDeck();
        deck.setName("Test Deck");
        deck.setTransitionTime(1000);
        deck.setCompetition(competition);
        deck = slideDeckRepository.save(deck);

        Slide slide = new ScoreSlide();
        slide.setName("Test Slide");
        slide.setIndex(0);
        slide.setSlidedeck(deck);
        slide = slideRepository.save(slide);

        // Act
        slideDeckRepository.updateSlideIndexById(slide.getId(), 5);

        // Assert
        slideRepository.flush();
        entityManager.clear();

        Slide updatedSlide = slideRepository.findById(slide.getId()).orElse(null);
        assertThat(updatedSlide).isNotNull();
        assertThat(updatedSlide.getIndex()).isEqualTo(5);
    }

    @Test
    void findSlideDecksByScore_ShouldReturnSlideDecksWithMatchingScore() {
        // Act
        List<SlideDeck> result = slideDeckRepository.findSlideDecksByScore(score.getId(), category.getId());

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(slideDeck.getId());
    }

    @Test
    void findSlideDecksByScore_ShouldReturnEmptyList_WhenNoMatchingScore() {
        // Act
        List<SlideDeck> result = slideDeckRepository.findSlideDecksByScore(999L, category.getId());

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findSlideDecksByScore_ShouldReturnEmptyList_WhenNoMatchingCategory() {
        // Act
        List<SlideDeck> result = slideDeckRepository.findSlideDecksByScore(score.getId(), 999L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findSlideDecksByScore_ShouldReturnMultipleSlideDecks_WhenMultipleDecksContainScore() {
        // Arrange - 创建第二个 SlideDeck
        SlideDeck slideDeck2 = new SlideDeck();
        slideDeck2.setName("Test SlideDeck 2");
        slideDeck2.setTransitionTime(1000);
        slideDeck2.setCompetition(competition);
        slideDeck2 = slideDeckRepository.save(slideDeck2);

        ScoreSlide scoreSlide2 = new ScoreSlide();
        scoreSlide2.setName("Test ScoreSlide 2");
        scoreSlide2.setIndex(0);
        scoreSlide2.setSlidedeck(slideDeck2);
        scoreSlide2.setCategory(category);
        scoreSlide2 = (ScoreSlide) slideRepository.save(scoreSlide2);

        // Act
        List<SlideDeck> result = slideDeckRepository.findSlideDecksByScore(score.getId(), category.getId());

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting("id").contains(slideDeck.getId(), slideDeck2.getId());
    }
} 
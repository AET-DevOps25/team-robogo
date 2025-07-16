package de.fll.screen.init;

import de.fll.screen.model.*;
import de.fll.screen.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.UUID;

@Component
public class InitDataLoader implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(InitDataLoader.class);
    @Autowired private CompetitionRepository competitionRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private SlideDeckRepository slideDeckRepository;
    @Autowired private SlideImageMetaRepository slideImageMetaRepository;
    @Autowired private SlideImageContentRepository slideImageContentRepository;
    @Autowired private SlideRepository slideRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private ScoreRepository scoreRepository;
    @Autowired private ScreenRepository screenRepository;

    @Value("${slides.path:/data/slides}")
    private String slidesPath;

    @Override
    public void run(String... args) throws Exception {
        // 1. Competition
        if (competitionRepository.count() == 0) {
            Competition fll = new Competition();
            fll.setName("FLL Competition");
            fll.setInternalId(UUID.randomUUID());
            competitionRepository.save(fll);
            Competition wro = new Competition();
            wro.setName("WRO Competition");
            wro.setInternalId(UUID.randomUUID());
            competitionRepository.save(wro);
            logger.info("[InitDataLoader] Inserted Competitions");
        }
        List<Competition> competitions = competitionRepository.findAll();
        Competition fll = competitions.get(0);
        Competition wro = competitions.size() > 1 ? competitions.get(1) : fll;

        // 2. Category
        if (categoryRepository.count() == 0) {
            Category c1 = new Category();
            c1.setName("FLL Robot Game");
            c1.setCompetition(fll);
            c1.setCategoryScoring(CategoryScoring.FLL_ROBOT_GAME);
            categoryRepository.save(c1);
            Category c2 = new Category();
            c2.setName("FLL Quarter Final");
            c2.setCompetition(fll);
            c2.setCategoryScoring(CategoryScoring.FLL_QUARTER_FINAL);
            categoryRepository.save(c2);
            Category c3 = new Category();
            c3.setName("FLL Test Round");
            c3.setCompetition(fll);
            c3.setCategoryScoring(CategoryScoring.FLL_TESTROUND);
            categoryRepository.save(c3);
            Category c4 = new Category();
            c4.setName("WRO Starter");
            c4.setCompetition(wro);
            c4.setCategoryScoring(CategoryScoring.WRO_STARTER);
            categoryRepository.save(c4);
            Category c5 = new Category();
            c5.setName("WRO RoboMission 2025");
            c5.setCompetition(wro);
            c5.setCategoryScoring(CategoryScoring.WRO_ROBOMISSION_2025);
            categoryRepository.save(c5);
            logger.info("[InitDataLoader] Inserted Categories");
        }
        List<Category> categories = categoryRepository.findAll();

        // 3. SlideDeck
        if (slideDeckRepository.count() == 0) {
            SlideDeck deck = new SlideDeck("Demo Deck", 10, new ArrayList<>(), fll);
            slideDeckRepository.save(deck);
            logger.info("[InitDataLoader] Inserted SlideDeck");
        }
        List<SlideDeck> decks = slideDeckRepository.findAll();

        // 4. SlideImageMeta/SlideImageContent
        File slidesDir = new File(slidesPath);
        if (slideImageMetaRepository.count() == 0 && slidesDir.exists() && slidesDir.isDirectory()) {
            File[] files = slidesDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        String contentType = Files.probeContentType(file.toPath());
                        byte[] content = Files.readAllBytes(file.toPath());
                        SlideImageMeta meta = new SlideImageMeta();
                        meta.setName(file.getName());
                        meta.setContentType(contentType);
                        SlideImageMeta savedMeta = slideImageMetaRepository.save(meta);
                        SlideImageContent imageContent = new SlideImageContent();
                        imageContent.setContent(content);
                        imageContent.setMeta(savedMeta);
                        slideImageContentRepository.save(imageContent);
                        logger.info("[InitDataLoader] Loaded image: " + file.getName());
                    }
                }
            }
        }
        List<SlideImageMeta> images = slideImageMetaRepository.findAll();
        SlideImageMeta meta = images.isEmpty() ? null : images.get(0);

        // 5. Slide (ImageSlide/ScoreSlide)
        if (slideRepository.count() == 0 && !decks.isEmpty() && meta != null && !categories.isEmpty()) {
            Category category = categories.get(0);
            for (SlideDeck deck : decks) {
                for (int i = 0; i < 3; i++) {
                    ScoreSlide scoreSlide = new ScoreSlide();
                    scoreSlide.setName("Score Board " + (i + 1));
                    scoreSlide.setSlidedeck(deck);
                    scoreSlide.setIndex(i);
                    scoreSlide.setCategory(category);
                    slideRepository.save(scoreSlide);
                }
                ImageSlide imageSlide = new ImageSlide();
                imageSlide.setName("Demo Image");
                imageSlide.setImageMeta(meta);
                imageSlide.setSlidedeck(deck);
                imageSlide.setIndex(1);
                slideRepository.save(imageSlide);
            }
            logger.info("[InitDataLoader] Inserted Slides");
        }
        List<Slide> slides = slideRepository.findAll();
        List<ScoreSlide> scoreSlides = new ArrayList<>();
        for (Slide slide : slides) {
            if (slide instanceof ScoreSlide) {
                scoreSlides.add((ScoreSlide) slide);
            }
        }

        // 6. Team
        if (teamRepository.count() == 0 && !categories.isEmpty()) {
            Team team1 = new Team("Alpha Team");
            team1.setCategory(categories.get(0));
            Team team2 = new Team("Beta Team");
            team2.setCategory(categories.get(1));
            Team team3 = new Team("Gamma Team");
            team3.setCategory(categories.get(2));
            teamRepository.save(team1);
            teamRepository.save(team2);
            teamRepository.save(team3);
            logger.info("[InitDataLoader] Inserted Teams");
        }
        List<Team> teams = teamRepository.findAll();

        // 7. Score
        // TODO: 后续根据实际业务完善分数与 ScoreSlide 的分配策略，目前每个 ScoreSlide 只包含同一轮的所有 team 的分数
        // 应该按照轮次来分配分数，每个 ScoreSlide 包含同一轮的所有 team 的分数，不是team
        if (scoreRepository.count() == 0 && !teams.isEmpty() && !scoreSlides.isEmpty()) {
            int rounds = Math.min(scoreSlides.size(), 3);
            for (int round = 0; round < rounds; round++) {
                ScoreSlide scoreSlide = scoreSlides.get(round);
                for (Team team : teams) {
                    Score s = new Score(100 - round * 10, 120 + round * 10);
                    s.setScoreSlide(scoreSlide);
                    s.setTeam(team);
                    s.setRound(round);
                    scoreRepository.save(s);
                }
            }
            logger.info("[InitDataLoader] Inserted Scores");
        }

        // 8. Screen
        if (screenRepository.count() == 0 && !decks.isEmpty()) {
            SlideDeck deck1 = decks.get(0);
            SlideDeck deck2 = decks.size() > 1 ? decks.get(1) : deck1;
            Screen screen1 = new Screen();
            screen1.setName("Main Hall");
            screen1.setStatus(ScreenStatus.ONLINE);
            screen1.setSlideDeck(deck1);
            Screen screen2 = new Screen();
            screen2.setName("Side Room");
            screen2.setStatus(ScreenStatus.OFFLINE);
            screen2.setSlideDeck(deck2);
            screenRepository.save(screen1);
            screenRepository.save(screen2);
            logger.info("[InitDataLoader] Inserted Screens");
        }
    }
} 
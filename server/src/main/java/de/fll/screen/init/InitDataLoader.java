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
            categoryRepository.save(c1);
            Category c2 = new Category();
            c2.setName("FLL Quarter Final");
            c2.setCompetition(fll);
            categoryRepository.save(c2);
            Category c3 = new Category();
            c3.setName("FLL Test Round");
            c3.setCompetition(fll);
            categoryRepository.save(c3);
            Category c4 = new Category();
            c4.setName("WRO Starter");
            c4.setCompetition(wro);
            categoryRepository.save(c4);
            Category c5 = new Category();
            c5.setName("WRO RoboMission 2025");
            c5.setCompetition(wro);
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
            for (SlideDeck deck : decks) {
                int slideIndex = 0;
                // 先为每个category插入一个ScoreSlide，index递增
                for (Category category : categories) {
                    ScoreSlide scoreSlide = new ScoreSlide();
                    scoreSlide.setName(category.getName() + " Score Board");
                    scoreSlide.setSlidedeck(deck);
                    scoreSlide.setIndex(slideIndex++);
                    scoreSlide.setCategory(category);
                    slideRepository.save(scoreSlide);
                }
                // 再插入一个ImageSlide，index为下一个
                ImageSlide imageSlide = new ImageSlide();
                imageSlide.setName("Demo Image");
                imageSlide.setImageMeta(meta);
                imageSlide.setSlidedeck(deck);
                imageSlide.setIndex(slideIndex);
                slideRepository.save(imageSlide);
            }
            logger.info("[InitDataLoader] Inserted Slides");
        }

        // 6. Team
        if (teamRepository.count() == 0 && !categories.isEmpty()) {
            List<Team> allTeams = new ArrayList<>();
            for (Category category : categories) {
                for (int i = 1; i <= 3; i++) { // 每个category 3个team
                    Team team = new Team(category.getName() + " Team " + i);
                    // 设置competitionId
                    team.setCategory(category);
                    teamRepository.save(team);
                    allTeams.add(team);
                }
            }
            logger.info("[InitDataLoader] Inserted Teams for each Category");
        }
        List<Team> teams = teamRepository.findAll();

        // 7. Score
        // 每个team只插入一条score，不再关联ScoreSlide
        if (scoreRepository.count() == 0 && !teams.isEmpty()) {
            for (Team team : teams) {
                int baseScore = 20 + (int)(Math.random() * 40); // 20~59
                int baseTime = 40 + (int)(Math.random() * 60); // 40~99
                Score s = new Score(baseScore, baseTime);
                s.setTeam(team);
                scoreRepository.save(s);
            }
            logger.info("[InitDataLoader] Inserted one Score per Team");
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
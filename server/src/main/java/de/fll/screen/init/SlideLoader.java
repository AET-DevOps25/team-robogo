package de.fll.screen.init;

import de.fll.screen.model.ImageSlide;
import de.fll.screen.model.ScoreSlide;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.model.SlideImageMeta;
import de.fll.screen.model.Category;
import de.fll.screen.repository.CategoryRepository;
import de.fll.screen.repository.SlideDeckRepository;
import de.fll.screen.repository.SlideImageMetaRepository;
import de.fll.screen.repository.SlideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(4)
public class SlideLoader implements CommandLineRunner {

    @Autowired
    private SlideDeckRepository slideDeckRepository;

    @Autowired
    private SlideImageMetaRepository slideImageMetaRepository;

    @Autowired
    private SlideRepository slideRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        List<SlideDeck> decks = slideDeckRepository.findAll();
        if (decks.isEmpty()) {
            System.out.println("No SlideDeck found, skip slide injection.");
            return;
        }
        List<SlideImageMeta> images = slideImageMetaRepository.findAll();
        if (images.isEmpty()) {
            System.out.println("No SlideImageMeta found, skip image slide injection.");
            return;
        }
        SlideImageMeta meta = images.get(0);

        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            System.out.println("No Category found, skip score slide injection.");
            return;
        }
        Category category = categories.get(0);

        for (SlideDeck deck : decks) {
            for (int i = 0; i < 3; i++) {
                ScoreSlide scoreSlide = new ScoreSlide();
                scoreSlide.setName("Score Board " + (i + 1));
                scoreSlide.setSlidedeck(deck);
                scoreSlide.setIndex(i);
                scoreSlide.setCategory(category);
                deck.getSlides().add(scoreSlide);
                slideRepository.save(scoreSlide);
            }
            ImageSlide imageSlide = new ImageSlide();
            imageSlide.setName("Demo Image");
            imageSlide.setImageMeta(meta);
            imageSlide.setSlidedeck(deck);
            imageSlide.setIndex(1);
            deck.getSlides().add(imageSlide);
            slideRepository.save(imageSlide);

            slideDeckRepository.save(deck);
        }
    }
}

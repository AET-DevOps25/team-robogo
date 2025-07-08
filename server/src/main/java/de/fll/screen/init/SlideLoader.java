package de.fll.screen.init;

import de.fll.screen.model.ImageSlide;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.model.SlideImageMeta;
import de.fll.screen.repository.SlideDeckRepository;
import de.fll.screen.repository.SlideImageMetaRepository;
import de.fll.screen.repository.SlideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SlideLoader implements CommandLineRunner {

    @Autowired
    private SlideDeckRepository slideDeckRepository;

    @Autowired
    private SlideImageMetaRepository slideImageMetaRepository;

    @Autowired
    private SlideRepository slideRepository;

    @Override
    public void run(String... args) throws Exception {
        // 只为已存在的 SlideDeck 添加 ImageSlide
        List<SlideDeck> decks = slideDeckRepository.findAll();
        if (decks.isEmpty()) {
            System.out.println("No SlideDeck found, skip dummy slide injection.");
            return;
        }
        SlideDeck deck = decks.get(0);

        // 获取所有图片元数据
        List<SlideImageMeta> images = slideImageMetaRepository.findAll();
        if (images.isEmpty()) return;

        // 为每个图片创建一个 ImageSlide 并加入 deck
        for (SlideImageMeta meta : images) {
            ImageSlide slide = new ImageSlide();
            slide.setName("Demo Slide for " + meta.getName());
            slide.setImageMeta(meta);
            slide.setSlidedeck(deck);
            deck.getSlides().add(slide);
            slideRepository.save(slide);
        }
        slideDeckRepository.save(deck);
    }
} 
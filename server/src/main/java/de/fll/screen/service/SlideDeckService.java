package de.fll.screen.service;

import de.fll.screen.model.SlideDeck;
import de.fll.screen.repository.SlideDeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import de.fll.screen.model.Slide;
import java.util.ArrayList;
import de.fll.screen.model.ScoreSlide;
import de.fll.screen.model.ImageSlide;

@Service
@Transactional
public class SlideDeckService {

    @Autowired
    private SlideDeckRepository slideDeckRepository;


    public List<SlideDeck> getAllSlideDecks() {
        return slideDeckRepository.findAll();
    }

    public Optional<SlideDeck> getSlideDeckById(Long id) {
        return slideDeckRepository.findById(id);
    }

    public void deleteSlideDeck(Long id) {
        slideDeckRepository.deleteById(id);
    }

    public SlideDeck createSlideDeck(SlideDeck slideDeck) {
        return slideDeckRepository.save(slideDeck);
    }

    @Transactional
    public SlideDeck addSlideToDeck(Long deckId, Slide slide) {
        SlideDeck deck = slideDeckRepository.findById(deckId)
            .orElseThrow(() -> new IllegalArgumentException("SlideDeck not found"));
        Slide newSlide;
        if (slide instanceof ScoreSlide) {
            ScoreSlide s = (ScoreSlide) slide;
            ScoreSlide scoreSlide = new ScoreSlide();
            scoreSlide.setName(s.getName());
            scoreSlide.setCategory(s.getCategory());
            newSlide = scoreSlide;
        } else if (slide instanceof ImageSlide) {
            ImageSlide s = (ImageSlide) slide;
            ImageSlide imageSlide = new ImageSlide();
            imageSlide.setName(s.getName());
            imageSlide.setImageMeta(s.getImageMeta());
            newSlide = imageSlide;
        } else {
            // 其他slide类型
            Slide generic = new Slide() {
                // 匿名子类实现抽象类
            };
            generic.setName(slide.getName());
            newSlide = generic;
        }
        newSlide.setSlidedeck(deck);
        newSlide.setIndex(deck.getSlides().size());
        deck.getSlides().add(newSlide);
        deck.setVersion(deck.getVersion() + 1);
        return slideDeckRepository.save(deck);
    }

    public SlideDeck reorderSlides(Long deckId, List<Long> newOrder) {
        // 第一步：全部设为负数，避免唯一约束冲突
        slideDeckRepository.setSlidesIndexNegative(deckId);

        // 第二步：按新顺序批量赋值
        for (int i = 0; i < newOrder.size(); i++) {
            slideDeckRepository.updateSlideIndexById(newOrder.get(i), i);
        }

        SlideDeck deck = slideDeckRepository.findById(deckId)
            .orElseThrow(() -> new IllegalArgumentException("SlideDeck not found"));
        deck.setVersion(deck.getVersion() + 1);
        return slideDeckRepository.save(deck);
    }

    @Transactional
    public SlideDeck removeSlideFromDeck(Long deckId, Long slideId) {
        SlideDeck deck = slideDeckRepository.findById(deckId)
            .orElseThrow(() -> new IllegalArgumentException("SlideDeck not found"));
        deck.getSlides().removeIf(s -> s.getId() == slideId);
        deck.setVersion(deck.getVersion() + 1);
        return slideDeckRepository.save(deck);
    }

    public SlideDeck updateSlideDeck(Long deckId, SlideDeck updateData) {
        SlideDeck existing = slideDeckRepository.findById(deckId)
            .orElseThrow(() -> new IllegalArgumentException("SlideDeck not found"));
        existing.setName(updateData.getName());
        existing.setTransitionTime(updateData.getTransitionTime());
        existing.setCompetition(updateData.getCompetition());
        // version is incremented to indicate that the slide deck has changed
        existing.setVersion(existing.getVersion() + 1);
        return slideDeckRepository.save(existing);
    }

    public SlideDeck updateSlideToDeck(Long deckId, Slide updatedSlide) {
        SlideDeck deck = slideDeckRepository.findById(deckId)
            .orElseThrow(() -> new IllegalArgumentException("SlideDeck not found"));
        List<Slide> slides = deck.getSlides();
        for (int i = 0; i < slides.size(); i++) {
            Slide s = slides.get(i);
            if (s.getId() == updatedSlide.getId()) {
                updatedSlide.setSlidedeck(deck);
                slides.set(i, updatedSlide);
                break;
            }
        }
        deck.setVersion(deck.getVersion() + 1);
        return slideDeckRepository.save(deck);
    }
} 
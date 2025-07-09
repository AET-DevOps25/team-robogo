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

    public SlideDeck addSlideToDeck(Long deckId, Slide slide) {
        SlideDeck deck = slideDeckRepository.findById(deckId)
            .orElseThrow(() -> new IllegalArgumentException("SlideDeck not found"));
        slide.setSlidedeck(deck);
        deck.getSlides().add(slide);
        deck.setVersion(deck.getVersion() + 1);
        return slideDeckRepository.save(deck);
    }

    public SlideDeck reorderSlides(Long deckId, List<Long> newOrder) {
        SlideDeck deck = slideDeckRepository.findById(deckId)
            .orElseThrow(() -> new IllegalArgumentException("SlideDeck not found"));
        List<Slide> slides = deck.getSlides();
        List<Slide> reordered = new ArrayList<>();
        for (Long id : newOrder) {
            slides.stream().filter(s -> s.getId() == id).findFirst().ifPresent(reordered::add);
        }
        slides.clear();
        slides.addAll(reordered);
        deck.setVersion(deck.getVersion() + 1);
        return slideDeckRepository.save(deck);
    }

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
} 
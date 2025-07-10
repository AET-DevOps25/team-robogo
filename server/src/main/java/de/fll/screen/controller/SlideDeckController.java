package de.fll.screen.controller;

import de.fll.core.dto.SlideDeckDTO;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.model.Slide;
import de.fll.screen.service.SlideDeckService;
import de.fll.screen.assembler.SlideDeckAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/slidedecks")
public class SlideDeckController {

    @Autowired
    private SlideDeckService slideDeckService;

    @Autowired
    private SlideDeckAssembler slideDeckAssembler;

    @GetMapping
    public List<SlideDeckDTO> getAllSlideDecks() {
        List<SlideDeck> decks = slideDeckService.getAllSlideDecks();
        List<SlideDeckDTO> dtos = new ArrayList<>();
        for (SlideDeck deck : decks) {
            dtos.add(slideDeckAssembler.toDTO(deck));
        }
        return dtos;
    }

    @GetMapping("/{deckId}")
    public SlideDeckDTO getSlideDeckById(@PathVariable Long deckId) {
        SlideDeck deck = slideDeckService.getSlideDeckById(deckId)
                .orElseThrow(() -> new IllegalArgumentException("SlideDeck not found"));
        return slideDeckAssembler.toDTO(deck);
    }

    @PostMapping
    public SlideDeckDTO createSlideDeck(@RequestBody SlideDeckDTO deckDTO) {
        SlideDeck deck = slideDeckAssembler.fromDTO(deckDTO);
        SlideDeck saved = slideDeckService.createSlideDeck(deck);
        return slideDeckAssembler.toDTO(saved);
    }

    @PutMapping("/{deckId}")
    public SlideDeckDTO updateSlideDeck(@PathVariable Long deckId, @RequestBody SlideDeckDTO deckDTO) {
        SlideDeck deck = slideDeckAssembler.fromDTO(deckDTO);
        SlideDeck updated = slideDeckService.updateSlideDeck(deckId, deck);
        return slideDeckAssembler.toDTO(updated);
    }

    @DeleteMapping("/{deckId}")
    public void deleteSlideDeck(@PathVariable Long deckId) {
        slideDeckService.deleteSlideDeck(deckId);
    }

    @PostMapping("/{deckId}/slides")
    public SlideDeckDTO addSlideToDeck(@PathVariable Long deckId, @RequestBody Slide slide) {
        SlideDeck deck = slideDeckService.addSlideToDeck(deckId, slide);
        return slideDeckAssembler.toDTO(deck);
    }

    @PostMapping("/{deckId}/slides/reorder")
    public SlideDeckDTO reorderSlides(@PathVariable Long deckId, @RequestBody List<Long> newOrder) {
        SlideDeck deck = slideDeckService.reorderSlides(deckId, newOrder);
        return slideDeckAssembler.toDTO(deck);
    }

    @DeleteMapping("/{deckId}/slides/{slideId}")
    public SlideDeckDTO removeSlideFromDeck(@PathVariable Long deckId, @PathVariable Long slideId) {
        SlideDeck deck = slideDeckService.removeSlideFromDeck(deckId, slideId);
        return slideDeckAssembler.toDTO(deck);
    }
} 
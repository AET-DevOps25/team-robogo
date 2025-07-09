package de.fll.screen.controller;

import de.fll.screen.model.SlideDeck;
import de.fll.screen.model.Slide;
import de.fll.screen.service.SlideDeckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/slidedecks")
public class SlideDeckController {

    @Autowired
    private SlideDeckService slideDeckService;

    // 获取所有SlideDeck
    @GetMapping
    public List<SlideDeck> getAllSlideDecks() {
        return slideDeckService.getAllSlideDecks();
    }

    // 根据ID获取SlideDeck
    @GetMapping("/{deckId}")
    public SlideDeck getSlideDeckById(@PathVariable Long deckId) {
        return slideDeckService.getSlideDeckById(deckId)
                .orElseThrow(() -> new IllegalArgumentException("SlideDeck not found"));
    }

    // 创建SlideDeck
    @PostMapping
    public SlideDeck createSlideDeck(@RequestBody SlideDeck slideDeck) {
        return slideDeckService.createSlideDeck(slideDeck);
    }

    // 更新SlideDeck
    @PutMapping("/{deckId}")
    public SlideDeck updateSlideDeck(@PathVariable Long deckId, @RequestBody SlideDeck slideDeck) {
        return slideDeckService.updateSlideDeck(deckId, slideDeck);
    }

    // 删除SlideDeck
    @DeleteMapping("/{deckId}")
    public void deleteSlideDeck(@PathVariable Long deckId) {
        slideDeckService.deleteSlideDeck(deckId);
    }

    // 添加新幻灯片到SlideDeck
    @PostMapping("/{deckId}/slides")
    public SlideDeck addSlideToDeck(@PathVariable Long deckId, @RequestBody Slide slide) {
        return slideDeckService.addSlideToDeck(deckId, slide);
    }

    // 移动幻灯片顺序
    @PostMapping("/{deckId}/slides/reorder")
    public SlideDeck reorderSlides(@PathVariable Long deckId, @RequestBody List<Long> newOrder) {
        return slideDeckService.reorderSlides(deckId, newOrder);
    }

    // 删除幻灯片
    @DeleteMapping("/{deckId}/slides/{slideId}")
    public SlideDeck removeSlideFromDeck(@PathVariable Long deckId, @PathVariable Long slideId) {
        return slideDeckService.removeSlideFromDeck(deckId, slideId);
    }
} 
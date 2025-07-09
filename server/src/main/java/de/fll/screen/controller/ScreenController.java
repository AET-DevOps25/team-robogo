package de.fll.screen.controller;

import de.fll.screen.model.Screen;
import de.fll.screen.service.ScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screens")
public class ScreenController {

    @Autowired
    private ScreenService screenService;

    @GetMapping
    public List<Screen> getAllScreens() {
        return screenService.getAllScreens();
    }

    @GetMapping("/{id}")
    public Screen getScreenById(@PathVariable Long id) {
        return screenService.getScreenById(id).orElseThrow();
    }

    @PostMapping
    public Screen createScreen(@RequestBody Screen screen) {
        return screenService.createScreen(screen);
    }

    @PutMapping("/{id}")
    public Screen updateScreen(@PathVariable Long id, @RequestBody Screen screen) {
        return screenService.updateScreen(id, screen);
    }

    @DeleteMapping("/{id}")
    public void deleteScreen(@PathVariable Long id) {
        screenService.deleteScreen(id);
    }

    @PostMapping("/{id}/assign-slide-deck/{slideDeckId}")
    public Screen assignSlideDeck(@PathVariable Long id, @PathVariable Long slideDeckId) {
        return screenService.assignSlideDeck(id, slideDeckId);
    }
}

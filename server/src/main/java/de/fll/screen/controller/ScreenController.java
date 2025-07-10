package de.fll.screen.controller;

import de.fll.core.dto.ScreenContentDTO;
import de.fll.core.dto.SlideImageMetaDTO;
import de.fll.screen.model.*;
import de.fll.screen.service.ScreenService;
import de.fll.screen.service.ScoreService;
import de.fll.screen.assembler.ScreenContentAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.*;

@RestController
@RequestMapping("/screens")
public class ScreenController {

    @Autowired
    private ScreenService screenService;

    @Autowired
    private ScreenContentAssembler screenContentAssembler;

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

    @GetMapping("/{id}/content")
    public ResponseEntity<ScreenContentDTO> getScreenContent(@PathVariable Long id) {
        Optional<Screen> screenOpt = screenService.getScreenById(id);
        if (screenOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ScreenContentDTO dto = screenContentAssembler.toDTO(screenOpt.get());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/status")
    public Screen updateScreenStatus(@PathVariable Long id, @RequestParam ScreenStatus status) {
        return screenService.updateScreenStatus(id, status);
    }
}

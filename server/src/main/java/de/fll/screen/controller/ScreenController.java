package de.fll.screen.controller;

import de.fll.core.dto.ScreenContentDTO;
import de.fll.screen.model.*;
import de.fll.screen.service.ScreenService;
import de.fll.screen.assembler.ScreenContentAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/screens")
public class ScreenController {

    @Autowired
    private ScreenService screenService;

    @Autowired
    private ScreenContentAssembler screenContentAssembler;

    @GetMapping
    public List<ScreenContentDTO> getAllScreens() {
        List<Screen> screens = screenService.getAllScreens();
        List<ScreenContentDTO> dtos = new ArrayList<>();
        for (Screen screen : screens) {
            dtos.add(screenContentAssembler.toDTO(screen));
        }
        return dtos;
    }

    @GetMapping("/{id}")
    public ScreenContentDTO getScreenById(@PathVariable Long id) {
        Screen screen = screenService.getScreenById(id).orElseThrow();
        return screenContentAssembler.toDTO(screen);
    }

    @PostMapping
    public ScreenContentDTO createScreen(@RequestBody ScreenContentDTO screenDTO) {
        Screen screen = screenContentAssembler.fromDTO(screenDTO);
        Screen saved = screenService.createScreen(screen);
        return screenContentAssembler.toDTO(saved);
    }

    @PutMapping("/{id}")
    public ScreenContentDTO updateScreen(@PathVariable Long id, @RequestBody ScreenContentDTO screenDTO) {
        Screen screen = screenContentAssembler.fromDTO(screenDTO);
        Screen updated = screenService.updateScreen(id, screen);
        return screenContentAssembler.toDTO(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteScreen(@PathVariable Long id) {
        screenService.deleteScreen(id);
    }

    @PostMapping("/{id}/assign-slide-deck/{slideDeckId}")
    public ScreenContentDTO assignSlideDeck(@PathVariable Long id, @PathVariable Long slideDeckId) {
        Screen screen = screenService.assignSlideDeck(id, slideDeckId);
        return screenContentAssembler.toDTO(screen);
    }

    @GetMapping("/{id}/content")
    public ScreenContentDTO getScreenContent(@PathVariable Long id) {
        Screen screen = screenService.getScreenById(id).orElseThrow();
        return screenContentAssembler.toDTO(screen);
    }

    @PutMapping("/{id}/status")
    public ScreenContentDTO updateScreenStatus(@PathVariable Long id, @RequestParam ScreenStatus status) {
        Screen screen = screenService.updateScreenStatus(id, status);
        return screenContentAssembler.toDTO(screen);
    }
}

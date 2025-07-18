package de.fll.screen.controller;

import de.fll.core.dto.ScreenContentDTO;
import de.fll.screen.model.*;
import de.fll.screen.service.ScreenService;
import de.fll.screen.assembler.ScreenContentAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/screens")
@Tag(name = "Screen Management", description = "Display screen management and content control related APIs")
public class ScreenController {

    @Autowired
    private ScreenService screenService;

    @Autowired
    private ScreenContentAssembler screenContentAssembler;

    @GetMapping
    @Operation(summary = "Get All Screens", description = "Returns all display screens in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Retrieved successfully", 
                    content = @Content(schema = @Schema(implementation = ScreenContentDTO.class)))
    })
    public List<ScreenContentDTO> getAllScreens() {
        List<Screen> screens = screenService.getAllScreens();
        return screens.stream().map(screenContentAssembler::toDTO).collect(Collectors.toList());
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

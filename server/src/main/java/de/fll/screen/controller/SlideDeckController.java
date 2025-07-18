package de.fll.screen.controller;

import de.fll.core.dto.SlideDeckDTO;
import de.fll.core.dto.SlideDeckSyncDTO;
import de.fll.core.dto.SlideDeckSyncRequestDTO;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.model.Slide;
import de.fll.screen.service.SlideDeckService;
import de.fll.screen.service.SlideDeckSyncService;
import de.fll.screen.assembler.SlideDeckAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.time.LocalDateTime;
import de.fll.core.dto.SlideDTO;
import de.fll.screen.assembler.SlideAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/slidedecks")
@Tag(name = "Slide Deck Management", description = "Slide deck creation, updates, deletion and synchronization APIs")
public class SlideDeckController {

    @Autowired
    private SlideDeckService slideDeckService;

    @Autowired
    private SlideDeckSyncService slideDeckSyncService;

    @Autowired
    private SlideDeckAssembler slideDeckAssembler;

    @Autowired
    private SlideAssembler slideAssembler;

    @GetMapping
    @Operation(summary = "Get All Slide Decks", description = "Returns all slide decks in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Retrieved successfully", 
                    content = @Content(schema = @Schema(implementation = SlideDeckDTO.class)))
    })
    public List<SlideDeckDTO> getAllSlideDecks() {
        List<SlideDeck> decks = slideDeckService.getAllSlideDecks();
        List<SlideDeckDTO> dtos = new ArrayList<>();
        for (SlideDeck deck : decks) {
            dtos.add(slideDeckAssembler.toDTO(deck));
        }
        return dtos;
    }

    @GetMapping("/{deckId}")
    @Operation(summary = "Get Slide Deck by ID", description = "Gets detailed information of slide deck by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Retrieved successfully", 
                    content = @Content(schema = @Schema(implementation = SlideDeckDTO.class))),
        @ApiResponse(responseCode = "404", description = "Slide deck not found")
    })
    public SlideDeckDTO getSlideDeckById(
            @Parameter(description = "Slide deck ID", required = true)
            @PathVariable Long deckId) {
        SlideDeck deck = slideDeckService.getSlideDeckById(deckId)
                .orElseThrow(() -> new IllegalArgumentException("SlideDeck not found"));
        return slideDeckAssembler.toDTO(deck);
    }

    @PostMapping
    @Operation(summary = "Create Slide Deck", description = "Creates a new slide deck")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Created successfully", 
                    content = @Content(schema = @Schema(implementation = SlideDeckDTO.class))),
        @ApiResponse(responseCode = "400", description = "Bad request parameters")
    })
    public SlideDeckDTO createSlideDeck(
            @Parameter(description = "Slide deck information", required = true)
            @RequestBody SlideDeckDTO deckDTO) {
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
    public SlideDeckDTO addSlideToDeck(@PathVariable Long deckId, @RequestBody SlideDTO slideDTO) {
        Slide slide = slideAssembler.fromDTO(slideDTO);
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

    @GetMapping("/{deckId}/slides/{slideId}")
    public SlideDTO getSlideFromDeck(
            @PathVariable Long deckId,
            @PathVariable Long slideId
    ) {
        SlideDeck deck = slideDeckService.getSlideDeckById(deckId)
                .orElseThrow(() -> new IllegalArgumentException("SlideDeck not found"));
        Slide slide = deck.getSlides().stream()
                .filter(s -> Long.valueOf(s.getId()).equals(slideId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Slide not found in deck"));
        return slideAssembler.toDTO(slide);
    }

    @PutMapping("/{deckId}/speed")
    public SlideDeckDTO updateSlideDeckSpeed(
            @PathVariable Long deckId,
            @RequestBody SpeedUpdateRequest request
    ) {
        SlideDeck deck = slideDeckService.updateSlideDeckSpeed(deckId, request.getTransitionTime());
        return slideDeckAssembler.toDTO(deck);
    }

    // 内部类用于接收速度更新请求
    public static class SpeedUpdateRequest {
        private Double transitionTime;

        public Double getTransitionTime() {
            return transitionTime;
        }

        public void setTransitionTime(Double transitionTime) {
            this.transitionTime = transitionTime;
        }
    }

    // 同步API - 获取当前同步状态
    @GetMapping("/{deckId}/sync")
    public SlideDeckSyncDTO getSyncStatus(@PathVariable Long deckId) {
        return slideDeckSyncService.getSyncStatus(deckId);
    }

    // 同步API - 更新同步状态
    @PostMapping("/{deckId}/sync")
    public SlideDeckSyncDTO updateSyncStatus(
            @PathVariable Long deckId,
            @RequestBody SlideDeckSyncRequestDTO request
    ) {
        return slideDeckSyncService.updateSyncStatus(deckId, request);
    }

    // 同步API - 强制同步更新（用于内容变更时）
    @PostMapping("/{deckId}/sync/force")
    public SlideDeckSyncDTO forceSyncUpdate(@PathVariable Long deckId) {
        return slideDeckSyncService.forceSyncUpdate(deckId);
    }
} 
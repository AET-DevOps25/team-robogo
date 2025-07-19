package de.fll.screen.service;

import de.fll.core.dto.SlideDeckSyncDTO;
import de.fll.core.dto.SlideDeckSyncRequestDTO;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.repository.SlideDeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for handling slide deck synchronization across multiple screens
 */
@Service
@Transactional
public class SlideDeckSyncService {

    @Autowired
    private SlideDeckRepository slideDeckRepository;

    /**
     * Get current sync status for a slide deck
     * @param deckId Slide deck ID
     * @return Sync status DTO
     */
    public SlideDeckSyncDTO getSyncStatus(Long deckId) {
        Optional<SlideDeck> deckOpt = slideDeckRepository.findById(deckId);
        if (deckOpt.isEmpty()) {
            return SlideDeckSyncDTO.builder()
                .deckId(deckId)
                .errorMessage("Slide deck not found")
                .syncActive(false)
                .build();
        }

        SlideDeck deck = deckOpt.get();
        return SlideDeckSyncDTO.builder()
            .deckId(deckId)
            .currentSlideIndex(0) // Default to first slide
            .slideCount(deck.getSlides().size())
            .transitionTime(deck.getTransitionTime())
            .lastUpdate(deck.getLastUpdate())
            .version(deck.getVersion())
            .isMaster(false)
            .syncActive(true)
            .build();
    }

    /**
     * Update sync status for a slide deck
     * @param deckId Slide deck ID
     * @param request Sync request
     * @return Updated sync status
     */
    public SlideDeckSyncDTO updateSyncStatus(Long deckId, SlideDeckSyncRequestDTO request) {
        Optional<SlideDeck> deckOpt = slideDeckRepository.findById(deckId);
        if (deckOpt.isEmpty()) {
            return SlideDeckSyncDTO.builder()
                .deckId(deckId)
                .errorMessage("Slide deck not found")
                .syncActive(false)
                .build();
        }

        SlideDeck deck = deckOpt.get();
        
        // Update lastUpdate timestamp
        LocalDateTime newTimestamp = request.getCustomTimestamp() != null 
            ? request.getCustomTimestamp() 
            : LocalDateTime.now();
        
        deck.setLastUpdate(newTimestamp);
        deck.setVersion(deck.getVersion() + 1);
        slideDeckRepository.save(deck);

        return SlideDeckSyncDTO.builder()
            .deckId(deckId)
            .currentSlideIndex(request.getCurrentSlideIndex())
            .slideCount(deck.getSlides().size())
            .transitionTime(deck.getTransitionTime())
            .lastUpdate(deck.getLastUpdate())
            .version(deck.getVersion())
            .isMaster(request.getSetAsMaster() != null && request.getSetAsMaster())
            .lastUpdateScreenId(request.getScreenId())
            .syncActive(true)
            .build();
    }

    /**
     * Force sync update (used when content changes)
     * @param deckId Slide deck ID
     * @return Updated sync status
     */
    public SlideDeckSyncDTO forceSyncUpdate(Long deckId) {
        Optional<SlideDeck> deckOpt = slideDeckRepository.findById(deckId);
        if (deckOpt.isEmpty()) {
            return SlideDeckSyncDTO.builder()
                .deckId(deckId)
                .errorMessage("Slide deck not found")
                .syncActive(false)
                .build();
        }

        SlideDeck deck = deckOpt.get();
        deck.setLastUpdate(LocalDateTime.now());
        deck.setVersion(deck.getVersion() + 1);
        slideDeckRepository.save(deck);

        return SlideDeckSyncDTO.builder()
            .deckId(deckId)
            .currentSlideIndex(0)
            .slideCount(deck.getSlides().size())
            .transitionTime(deck.getTransitionTime())
            .lastUpdate(deck.getLastUpdate())
            .version(deck.getVersion())
            .isMaster(false)
            .syncActive(true)
            .build();
    }

    /**
     * Check if sync is needed based on lastUpdate timestamp
     * @param deckId Slide deck ID
     * @param lastKnownUpdate Last known update timestamp
     * @return true if sync is needed
     */
    public boolean isSyncNeeded(Long deckId, LocalDateTime lastKnownUpdate) {
        Optional<SlideDeck> deckOpt = slideDeckRepository.findById(deckId);
        if (deckOpt.isEmpty()) {
            return false;
        }

        SlideDeck deck = deckOpt.get();
        return deck.getLastUpdate().isAfter(lastKnownUpdate);
    }
} 
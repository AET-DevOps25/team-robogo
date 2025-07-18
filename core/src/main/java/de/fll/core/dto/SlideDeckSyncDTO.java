package de.fll.core.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * DTO for slide deck synchronization
 * Used to maintain sync state across multiple screens showing the same slide deck
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlideDeckSyncDTO {
    
    /**
     * Slide deck ID
     */
    private Long deckId;
    
    /**
     * Current slide index (0-based)
     */
    private Integer currentSlideIndex;
    
    /**
     * Total number of slides in the deck
     */
    private Integer slideCount;
    
    /**
     * Transition time in milliseconds
     */
    private Integer transitionTime;
    
    /**
     * Last update timestamp when slide transition occurred
     * Used to detect changes and synchronize across screens
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime lastUpdate;
    
    /**
     * Current slide deck version
     * Used to detect content changes
     */
    private Integer version;
    
    /**
     * Whether this is the master screen (controls the sync)
     */
    private Boolean isMaster;
    
    /**
     * Screen ID that triggered the last update
     */
    private String lastUpdateScreenId;
    
    /**
     * Sync status - whether synchronization is active
     */
    private Boolean syncActive;
    
    /**
     * Error message if sync failed
     */
    private String errorMessage;
} 
package de.fll.core.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * DTO for slide deck sync requests
 * Used when a screen wants to update the sync state
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlideDeckSyncRequestDTO {
    
    /**
     * Current slide index to set
     */
    private Integer currentSlideIndex;
    
    /**
     * Screen ID making the request
     */
    private String screenId;
    
    /**
     * Whether this screen should become the master
     */
    private Boolean setAsMaster;
    
    /**
     * Force sync update (ignore version conflicts)
     */
    private Boolean forceUpdate;
    
    /**
     * Custom timestamp for the update
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime customTimestamp;
} 
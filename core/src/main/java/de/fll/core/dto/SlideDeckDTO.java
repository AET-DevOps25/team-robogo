package de.fll.core.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SlideDeckDTO {
    private Long id;
    private String name;
    private int transitionTime;
    private int version;
    private Long competitionId;
    private List<SlideDTO> slides;
    
    /**
     * Last update timestamp for synchronization
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime lastUpdate;
} 
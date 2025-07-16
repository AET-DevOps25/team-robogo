package de.fll.core.dto;

import lombok.Data;
import java.util.List;

@Data
public class SlideDeckDTO {
    private Long id;
    private String name;
    private int transitionTime;
    private int version;
    private Long competitionId;
    private List<SlideDTO> slides;
} 
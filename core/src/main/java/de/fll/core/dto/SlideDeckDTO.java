package de.fll.core.dto;

import lombok.Data;
import java.util.List;

@Data
public class SlideDeckDTO {
    private Long id;
    private String name;
    private int version;
    private List<SlideDTO> slides;
} 
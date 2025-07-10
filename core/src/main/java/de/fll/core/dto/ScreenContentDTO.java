package de.fll.core.dto;

import lombok.Data;

@Data
public class ScreenContentDTO {
    private Long id;
    private String name;
    private String status;
    private SlideDeckDTO slideDeck;
} 
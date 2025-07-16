package de.fll.core.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScoreSlideDTO extends SlideDTO {
    private List<ScoreDTO> scores;
    private CategoryDTO category;
} 
package de.fll.core.dto;

import java.util.List;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SlideDisplayDTO {
    private Long id;
    private String name;
    private String type; // image, score
    private SlideImageMetaDTO imageMeta; // only for image slides
    private List<ScoreDTO> scores; // only for score slides
}

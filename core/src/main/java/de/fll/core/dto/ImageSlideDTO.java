package de.fll.core.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ImageSlideDTO extends SlideDTO {
    private ImageSlideMetaDTO imageMeta;
} 
package de.fll.screen.assembler;

import de.fll.core.dto.SlideDTO;
import de.fll.screen.model.Slide;
import de.fll.screen.model.SlideType;
import de.fll.core.dto.ImageSlideDTO;
import de.fll.core.dto.ScoreSlideDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SlideAssembler implements AbstractDTOAssembler<Slide, SlideDTO> {
    @Autowired
    private ImageSlideAssembler imageSlideAssembler;
    @Autowired
    private ScoreSlideAssembler scoreSlideAssembler;

    @Override
    public SlideDTO toDTO(Slide slide) {
        if (slide == null) return null;
        SlideDTO dto = new SlideDTO();
        dto.setId(slide.getId());
        dto.setName(slide.getName());
        dto.setIndex(slide.getIndex());
        dto.setType(slide.getType().name());
        return dto;
    }

    @Override
    public Slide fromDTO(SlideDTO dto) {
        if (dto == null) return null;
        String typeStr = dto.getType();
        if (typeStr == null) return null;
        SlideType type = SlideType.valueOf(typeStr);
        switch (type) {
            case IMAGE:
                if (dto instanceof ImageSlideDTO) {
                    return imageSlideAssembler.fromDTO((ImageSlideDTO) dto);
                } else {
                    throw new IllegalArgumentException("ImageSlideDTO required for IMAGE type");
                }
            case SCORE:
                if (dto instanceof ScoreSlideDTO) {
                    return scoreSlideAssembler.fromDTO((ScoreSlideDTO) dto);
                } else {
                    throw new IllegalArgumentException("ScoreSlideDTO required for SCORE type");
                }
            default:
                // Impossible to reach this case
                Slide slide = new Slide() {};
                slide.setName(dto.getName());
                slide.setIndex(dto.getIndex());
                return slide;
        }
    }
}

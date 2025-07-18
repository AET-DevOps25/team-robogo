package de.fll.screen.assembler;

import de.fll.core.dto.ImageSlideDTO;
import de.fll.core.dto.ImageSlideMetaDTO;
import de.fll.screen.model.ImageSlide;
import de.fll.screen.model.SlideImageMeta;
import de.fll.screen.repository.SlideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImageSlideAssembler implements AbstractDTOAssembler<ImageSlide, ImageSlideDTO> {

    @Autowired
    private SlideRepository slideRepository;

    @Autowired
    private ImageSlideMetaAssembler imageSlideMetaAssembler;

    @Override
    public ImageSlideDTO toDTO(ImageSlide slide) {
        if (slide == null) return null;
        ImageSlideDTO dto = new ImageSlideDTO();
        dto.setId(slide.getId());
        dto.setName(slide.getName());
        // 安全地设置 index，避免 NullPointerException
        Integer index = slide.getIndex();
        dto.setIndex(index != null ? index : 0);
        dto.setType(slide.getType().name());
        SlideImageMeta meta = slide.getImageMeta();
        ImageSlideMetaDTO metaDTO = imageSlideMetaAssembler.toDTO(meta);
        dto.setImageMeta(metaDTO);
        return dto;
    }

    @Override
    public ImageSlide fromDTO(ImageSlideDTO dto) {
        if (dto == null) return null;
        ImageSlide slide = (ImageSlide) slideRepository.findById(dto.getId())
                .orElseGet(() -> {
                    ImageSlide s = new ImageSlide();
                    s.setName(dto.getName());
                    s.setIndex(dto.getIndex());
                    slideRepository.save(s);
                    return s;
                });
        slide.setImageMeta(imageSlideMetaAssembler.fromDTO(dto.getImageMeta()));
        return slide;
    }


}

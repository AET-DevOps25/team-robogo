package de.fll.screen.assembler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.fll.core.dto.SlideImageMetaDTO;
import de.fll.screen.model.SlideImageMeta;
import de.fll.screen.repository.SlideImageMetaRepository;

@Component
public class ImageSlideMetaAssembler implements AbstractDTOAssembler<SlideImageMeta, SlideImageMetaDTO> {
    @Autowired
    private SlideImageMetaRepository slideImageMetaRepository;

    @Override
    public SlideImageMetaDTO toDTO(SlideImageMeta meta) {
        if (meta == null)
            return null;
        SlideImageMetaDTO dto = new SlideImageMetaDTO();
        dto.setId(meta.getId());
        dto.setName(meta.getName());
        dto.setContentType(meta.getContentType());
        return dto;
    }

    @Override
    public SlideImageMeta fromDTO(SlideImageMetaDTO dto) {
        if (dto == null)
            return null;
        SlideImageMeta meta = slideImageMetaRepository.findById(dto.getId())
                .orElseGet(() -> {
                    SlideImageMeta s = new SlideImageMeta();
                    s.setName(dto.getName());
                    s.setContentType(dto.getContentType());
                    slideImageMetaRepository.save(s);
                    return s;
                });
        return meta;
    }
}

package de.fll.screen.service;

import de.fll.core.dto.ImageSlideMetaDTO;
import de.fll.screen.assembler.ImageSlideMetaAssembler;
import de.fll.screen.model.SlideImageContent;
import de.fll.screen.model.SlideImageMeta;
import de.fll.screen.repository.SlideImageContentRepository;
import de.fll.screen.repository.SlideImageMetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SlideImageService {
    @Autowired
    private SlideImageMetaRepository metaRepository;
    @Autowired
    private SlideImageContentRepository contentRepository;

    
    @Autowired
    private ImageSlideMetaAssembler imageSlideMetaAssembler;

    public List<ImageSlideMetaDTO> getAllImages() {
        return metaRepository.findAll().stream()
                .map(imageSlideMetaAssembler::toDTO)
                .collect(Collectors.toList());
    }

    public ImageSlideMetaDTO uploadImage(MultipartFile file) throws IOException {
        SlideImageMeta meta = new SlideImageMeta();
        meta.setName(file.getOriginalFilename());
        meta.setContentType(file.getContentType());
        SlideImageMeta savedMeta = metaRepository.save(meta);

        SlideImageContent content = new SlideImageContent();
        content.setContent(file.getBytes());
        content.setMeta(savedMeta);
        contentRepository.save(content);

        return imageSlideMetaAssembler.toDTO(savedMeta);
    }

    public Optional<SlideImageMeta> getMetaById(Long id) {
        return metaRepository.findById(id);
    }

    public SlideImageContent getContentByMetaId(Long id) {
        return contentRepository.findByMetaId(id);
    }
} 
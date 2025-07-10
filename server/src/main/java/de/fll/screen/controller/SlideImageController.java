package de.fll.screen.controller;

import de.fll.core.dto.SlideImageMetaDTO;
import de.fll.screen.model.SlideImageContent;
import de.fll.screen.model.SlideImageMeta;
import de.fll.screen.service.SlideImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/slide-images")
public class SlideImageController {

    @Autowired
    private SlideImageService slideImageService;

    /**
     * Get all image metadata (without binary content).
     * Only returns id, name, contentType.
     */
    @GetMapping
    public List<SlideImageMetaDTO> getAllImages() {
        return slideImageService.getAllImages();
    }

    /**
     * Get image binary content by meta id.
     * Can be used directly in <img> tag.
     */
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImageById(@PathVariable Long id) {
        Optional<SlideImageMeta> metaOpt = slideImageService.getMetaById(id);
        if (metaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        SlideImageContent content = slideImageService.getContentByMetaId(id);
        if (content == null) {
            return ResponseEntity.notFound().build();
        }
        SlideImageMeta meta = metaOpt.get();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + meta.getName() + "\"")
                .contentType(MediaType.parseMediaType(meta.getContentType()))
                .body(content.getContent());
    }

    /**
     * Upload a new image via multipart/form-data.
     * Saves both meta and content, returns created meta.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SlideImageMetaDTO> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            SlideImageMetaDTO dto = slideImageService.uploadImage(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 
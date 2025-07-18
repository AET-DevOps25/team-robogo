package de.fll.screen.controller;

import de.fll.core.dto.ImageSlideMetaDTO;
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
    public List<ImageSlideMetaDTO> getAllImages() {
        return slideImageService.getAllImages();
    }

    /**
     * Get image binary content by meta id.
     * Can be used directly in <img> tag.
     * Uses Redis cache for improved performance.
     */
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImageById(@PathVariable Long id) {
        Optional<SlideImageMeta> metaOpt = slideImageService.getMetaById(id);
        if (metaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // 使用缓存方法获取图片内容
        byte[] imageContent = slideImageService.getImageContentById(id);
        if (imageContent == null) {
            return ResponseEntity.notFound().build();
        }
        
        SlideImageMeta meta = metaOpt.get();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + meta.getName() + "\"")
                .contentType(MediaType.parseMediaType(meta.getContentType()))
                .body(imageContent);
    }

    /**
     * Upload a new image via multipart/form-data.
     * Saves both meta and content, returns created meta.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageSlideMetaDTO> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            ImageSlideMetaDTO dto = slideImageService.uploadImage(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Clear cache for a specific image
     */
    @DeleteMapping("/{id}/cache")
    public ResponseEntity<Void> clearImageCache(@PathVariable Long id) {
        slideImageService.clearImageCache(id);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Clear all image caches
     */
    @DeleteMapping("/cache")
    public ResponseEntity<Void> clearAllImageCache() {
        slideImageService.clearAllImageCache();
        return ResponseEntity.ok().build();
    }
} 
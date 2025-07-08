package de.fll.screen.repository;

import de.fll.screen.model.SlideImageContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlideImageContentRepository extends JpaRepository<SlideImageContent, Long> {
    SlideImageContent findByMetaId(Long metaId);
} 
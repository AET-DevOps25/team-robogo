package de.fll.screen.init;

import de.fll.screen.model.SlideImageMeta;
import de.fll.screen.model.SlideImageContent;
import de.fll.screen.repository.SlideImageMetaRepository;
import de.fll.screen.repository.SlideImageContentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Order(1)
public class SlideImageLoader implements CommandLineRunner {

    @Autowired
    private SlideImageMetaRepository metaRepository;

    @Autowired
    private SlideImageContentRepository contentRepository;

    @Value("${slides.path:/data/slides}")
    private String slidesPath;

    private static final Logger logger = LoggerFactory.getLogger(SlideImageLoader.class);

    @Override
    public void run(String... args) throws Exception {
        File slidesDir = new File(slidesPath);
        if (!slidesDir.exists() || !slidesDir.isDirectory()) {
            logger.warn("Slides directory not found: {}", slidesPath);
            return;
        }
        File[] files = slidesDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    try {
                        String contentType = Files.probeContentType(file.toPath());
                        byte[] content = Files.readAllBytes(file.toPath());

                        // Save meta first
                        SlideImageMeta meta = new SlideImageMeta();
                        meta.setName(file.getName());
                        meta.setContentType(contentType);
                        SlideImageMeta savedMeta = metaRepository.save(meta);

                        // Save content and link to meta
                        SlideImageContent imageContent = new SlideImageContent();
                        imageContent.setContent(content);
                        imageContent.setMeta(savedMeta);
                        contentRepository.save(imageContent);

                        logger.info("Loaded image: {}", file.getName());
                    } catch (IOException e) {
                        logger.error("Failed to load image: {}", file.getName(), e);
                    }
                }
            }
        }
    }
} 
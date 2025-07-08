package de.fll.screen.init;

import de.fll.screen.model.SlideImageMeta;
import de.fll.screen.model.SlideImageContent;
import de.fll.screen.repository.SlideImageMetaRepository;
import de.fll.screen.repository.SlideImageContentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

@Component
public class SlideImageLoader implements CommandLineRunner {

    @Autowired
    private SlideImageMetaRepository metaRepository;

    @Autowired
    private SlideImageContentRepository contentRepository;

    @Override
    public void run(String... args) throws Exception {
        String slidesPath = "slides";
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(slidesPath);
        if (resource == null) {
            System.out.println("File Directory not found");
            return;
        }
        File slidesDir = new File(resource.toURI());
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

                        System.out.println("Loaded image: " + file.getName());
                    } catch (IOException e) {
                        System.err.println("Failed to load image: " + file.getName());
                    }
                }
            }
        }
    }
} 
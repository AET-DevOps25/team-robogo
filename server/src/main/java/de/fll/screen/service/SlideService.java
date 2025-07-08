package de.fll.screen.service;

import de.fll.screen.model.Slide;
import de.fll.screen.model.SlideImageMeta;
import de.fll.screen.model.Score;
import de.fll.core.dto.SlideDisplayDTO;
import de.fll.core.dto.SlideImageMetaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import de.fll.screen.model.ImageSlide;
import de.fll.screen.model.ScoreSlide;

@Service
public class SlideService {

    @Autowired
    private ScoreService scoreService;

    // assemble a single Slide into a SlideDisplayDTO
    public SlideDisplayDTO assembleSlideDisplay(Slide slide, List<Score> allScores) {
        SlideDisplayDTO.SlideDisplayDTOBuilder builder = SlideDisplayDTO.builder()
                .id(slide.getId())
                .name(slide.getName());

        if (slide instanceof ImageSlide imageSlide) {
            builder.type("image");
            SlideImageMeta meta = imageSlide.getImageMeta();
            if (meta != null) {
                builder.imageMeta(SlideImageMetaDTO.builder()
                        .id(meta.getId())
                        .name(meta.getName())
                        .contentType(meta.getContentType())
                        .build());
            }
        } else if (slide instanceof ScoreSlide) {
            builder.type("score");
            if (allScores != null) {
                builder.scores(scoreService.getAllTeamsScoreDTOsWithHighlight(allScores));
            }
        }
        return builder.build();
    }
} 
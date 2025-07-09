package de.fll.screen.service;

import de.fll.screen.model.Slide;
import de.fll.screen.model.SlideImageMeta;
import de.fll.screen.model.Score;
import de.fll.core.dto.SlideImageDisplayDTO;
import de.fll.core.dto.SlideImageMetaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import de.fll.screen.model.ImageSlide;
import de.fll.screen.model.ScoreSlide;
import de.fll.screen.model.Team;
import de.fll.screen.model.Category;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SlideService {

    @Autowired
    private ScoreService scoreService;

    // assemble a single Slide into a SlideDisplayDTO
    public SlideImageDisplayDTO assembleSlideDisplay(Slide slide, List<Score> allScores) {
        SlideImageDisplayDTO.SlideDisplayDTOBuilder builder = SlideImageDisplayDTO.builder()
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
            if (allScores != null && !allScores.isEmpty()) {
                List<Team> teams = allScores.stream()
                        .map(Score::getTeam)
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList();
                Set<Category> categories = teams.stream()
                        .map(Team::getCategory)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                if (categories.size() == 1) {
                    Category category = categories.iterator().next();
                    builder.scores(scoreService.getAllTeamsScoreDTOsWithHighlight(teams, category));
                } else {
                    throw new IllegalArgumentException("All scores must belong to teams of the same category!");
                }
            }
        }
        return builder.build();
    }
} 
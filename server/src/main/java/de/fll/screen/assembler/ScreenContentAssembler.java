package de.fll.screen.assembler;

import de.fll.core.dto.ScreenContentDTO;
import de.fll.core.dto.SlideDTO;
import de.fll.core.dto.SlideDeckDTO;
import de.fll.core.dto.SlideImageMetaDTO;
import de.fll.core.dto.ImageSlideDTO;
import de.fll.core.dto.ScoreSlideDTO;
import de.fll.core.dto.ScoreDTO;
import de.fll.screen.model.*;
import de.fll.screen.model.SlideType;
import de.fll.screen.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class ScreenContentAssembler {
    @Autowired(required = false)
    private ScoreService scoreService;

    public ScreenContentDTO toDTO(Screen screen) {
        ScreenContentDTO dto = new ScreenContentDTO();
        dto.setId(screen.getId());
        dto.setName(screen.getName());
        dto.setStatus(screen.getStatus() != null ? screen.getStatus().name() : null);
        SlideDeck deck = screen.getSlideDeck();
        if (deck != null) {
            SlideDeckDTO deckDTO = new SlideDeckDTO();
            deckDTO.setId(deck.getId());
            deckDTO.setName(deck.getName());
            deckDTO.setVersion(deck.getVersion());
            List<SlideDTO> slideDTOs = new ArrayList<>();
            for (Slide slide : deck.getSlides()) {
                SlideDTO slideDTO;
                SlideType type = slide.getType();
                switch (type) {
                    case IMAGE: {
                        ImageSlideDTO imgDTO = new ImageSlideDTO();
                        imgDTO.setId(slide.getId());
                        imgDTO.setName(slide.getName());
                        imgDTO.setIndex(slide.getIndex());
                        imgDTO.setType(type.name());
                        ImageSlide img = (ImageSlide) slide;
                        SlideImageMeta meta = img.getImageMeta();
                        if (meta != null) {
                            SlideImageMetaDTO metaDTO = new SlideImageMetaDTO();
                            metaDTO.setId(meta.getId());
                            metaDTO.setName(meta.getName());
                            metaDTO.setContentType(meta.getContentType());
                            imgDTO.setImageMeta(metaDTO);
                        }
                        slideDTO = imgDTO;
                        break;
                    }
                    case SCORE: {
                        ScoreSlideDTO scoreDTO = new ScoreSlideDTO();
                        scoreDTO.setId(slide.getId());
                        scoreDTO.setName(slide.getName());
                        scoreDTO.setIndex(slide.getIndex());
                        scoreDTO.setType(type.name());
                        Competition competition = deck.getCompetition();
                        if (competition != null && scoreService != null) {
                            List<ScoreDTO> allScores = new ArrayList<>();
                            for (Category category : competition.getCategories()) {
                                List<Team> teams = new ArrayList<>(category.getTeams());
                                allScores.addAll(scoreService.getAllTeamsScoreDTOsWithHighlight(teams, category));
                            }
                            scoreDTO.setScores(allScores);
                        }
                        slideDTO = scoreDTO;
                        break;
                    }
                    default: {
                        slideDTO = new SlideDTO();
                        slideDTO.setId(slide.getId());
                        slideDTO.setName(slide.getName());
                        slideDTO.setIndex(slide.getIndex());
                        slideDTO.setType(type != null ? type.name() : null);
                        break;
                    }
                }
                slideDTOs.add(slideDTO);
            }
            deckDTO.setSlides(slideDTOs);
            dto.setSlideDeck(deckDTO);
        }
        return dto;
    }
} 
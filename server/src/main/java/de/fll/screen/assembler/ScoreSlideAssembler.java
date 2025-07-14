package de.fll.screen.assembler;

import java.util.List;

import de.fll.core.dto.ScoreDTO;
import de.fll.core.dto.ScoreSlideDTO;
import de.fll.screen.model.ScoreSlide;
import de.fll.screen.service.ScoreService;
import de.fll.screen.repository.SlideRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScoreSlideAssembler implements AbstractDTOAssembler<ScoreSlide, ScoreSlideDTO> {

    @Autowired
    private SlideRepository slideRepository;

    @Autowired
    private ScoreService scoreService;

    @Override
    public ScoreSlideDTO toDTO(ScoreSlide slide) {
        if (slide == null)
            return null;
        ScoreSlideDTO dto = new ScoreSlideDTO();
        dto.setId(slide.getId());
        dto.setName(slide.getName());
        dto.setIndex(slide.getIndex());
        dto.setType(slide.getType().name());
        dto.setCategoryId(slide.getCategory().getId());
        List<ScoreDTO> scores = scoreService.getAllTeamsScoreDTOsWithHighlight(
                slide.getCategory().getTeams().stream().toList());
        dto.setScores(scores);
        return dto;
    }

    @Override
    public ScoreSlide fromDTO(ScoreSlideDTO dto) {
        if (dto == null)
            return null;
        return (ScoreSlide) slideRepository.findById(dto.getId())
                .orElseGet(() -> scoreService.createScoreSlideFromDTO(dto));
    }
}

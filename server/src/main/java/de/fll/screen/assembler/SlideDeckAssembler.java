package de.fll.screen.assembler;

import de.fll.core.dto.*;
import de.fll.screen.model.*;
import de.fll.screen.repository.CompetitionRepository;
import org.springframework.stereotype.Component;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class SlideDeckAssembler implements AbstractDTOAssembler<SlideDeck, SlideDeckDTO> {

    @Autowired
    private SlideAssembler slideAssembler;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Override
    public SlideDeckDTO toDTO(SlideDeck deck) {
        if (deck == null)
            return null;
        SlideDeckDTO dto = new SlideDeckDTO();
        dto.setId(deck.getId());
        dto.setName(deck.getName());
        dto.setVersion(deck.getVersion());
        dto.setTransitionTime(deck.getTransitionTime());
        dto.setCompetitionId(deck.getCompetition().getId());
        List<SlideDTO> slideDTOs = new ArrayList<>();
        for (Slide slide : deck.getSlides()) {
            System.out.println("slide: " + slide);
            SlideDTO slideDTO = slideAssembler.toDTO(slide);
            slideDTOs.add(slideDTO);
        }
        dto.setSlides(slideDTOs);
        return dto;
    }

    @Override
    public SlideDeck fromDTO(SlideDeckDTO dto) {
        if (dto == null)
            return null;
        SlideDeck deck = new SlideDeck();
        deck.setName(dto.getName());
        deck.setTransitionTime(dto.getTransitionTime());
        deck.setVersion(dto.getVersion());
        // For now, we temporarily use the competitionId to set the competition
        Competition competition = competitionRepository.findById(dto.getCompetitionId()).orElse(null);
        if (competition == null) {
            throw new IllegalArgumentException("Competition not found for id: " + dto.getCompetitionId());
        }
        deck.setCompetition(competition);
        if (dto.getSlides() != null) {
            List<Slide> slides = new ArrayList<>();
            for (SlideDTO slideDTO : dto.getSlides()) {
                Slide slide = slideAssembler.fromDTO(slideDTO);
                if (slide != null) {
                    slide.setSlidedeck(deck);
                    slides.add(slide);
                }
            }
            deck.getSlides().addAll(slides);
        }
        return deck;
    }
}

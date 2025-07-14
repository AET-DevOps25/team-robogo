package de.fll.screen.service;

import de.fll.screen.model.Screen;
import de.fll.screen.model.ScreenStatus;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.repository.ScreenRepository;
import de.fll.screen.repository.SlideDeckRepository;
import de.fll.core.dto.ScreenContentDTO;
import de.fll.screen.repository.CompetitionRepository;
import de.fll.screen.assembler.SlideDeckAssembler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScreenService {

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private SlideDeckRepository slideDeckRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private SlideDeckAssembler slideDeckAssembler;

    public List<Screen> getAllScreens() {
        return screenRepository.findAll();
    }

    public Optional<Screen> getScreenById(Long id) {
        return screenRepository.findById(id);
    }

    public Screen createScreen(Screen screen) {
        return screenRepository.save(screen);
    }

    public Screen updateScreen(Long id, Screen screenDetails) {
        Screen screen = screenRepository.findById(id).orElseThrow();
        screen.setName(screenDetails.getName());
        screen.setStatus(screenDetails.getStatus());
        return screenRepository.save(screen);
    }

    public void deleteScreen(Long id) {
        screenRepository.deleteById(id);
    }

    public Screen assignSlideDeck(Long screenId, Long slideDeckId) {
        Screen screen = screenRepository.findById(screenId).orElseThrow();
        SlideDeck slideDeck = slideDeckRepository.findById(slideDeckId).orElseThrow();
        screen.setSlideDeck(slideDeck);
        return screenRepository.save(screen);
    }

    public Screen updateScreenStatus(Long id, ScreenStatus status) {
        Screen screen = screenRepository.findById(id).orElseThrow();
        screen.setStatus(status);
        return screenRepository.save(screen);
    }

    public Screen createScreenFromDTO(ScreenContentDTO dto) {
        if (dto == null) return null;
        Screen screen = new Screen();
        screen.setName(dto.getName());
        if (dto.getStatus() != null) {
            screen.setStatus(ScreenStatus.valueOf(dto.getStatus()));
        }
        if (dto.getSlideDeck() != null) {
            screen.setSlideDeck(slideDeckAssembler.fromDTO(dto.getSlideDeck()));
        }
        return screenRepository.save(screen);
    }
}

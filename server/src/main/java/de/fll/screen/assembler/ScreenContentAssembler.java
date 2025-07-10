package de.fll.screen.assembler;

import de.fll.core.dto.ScreenContentDTO;
import de.fll.screen.model.Screen;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.service.ScreenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScreenContentAssembler implements AbstractDTOAssembler<Screen, ScreenContentDTO> {
    
    @Autowired
    private SlideDeckAssembler slideDeckAssembler;
    
    @Autowired
    private ScreenService screenService;

    @Override
    public ScreenContentDTO toDTO(Screen screen) {
        if (screen == null) return null;
        ScreenContentDTO dto = new ScreenContentDTO();
        dto.setId(screen.getId());
        dto.setName(screen.getName());
        dto.setStatus(screen.getStatus() != null ? screen.getStatus().name() : null);
        SlideDeck deck = screen.getSlideDeck();
        if (deck != null) {
            dto.setSlideDeck(slideDeckAssembler.toDTO(deck));
        }
        return dto;
    }

    @Override
    public Screen fromDTO(ScreenContentDTO dto) {
        if (dto == null) return null;
        return screenService.createScreenFromDTO(dto);
    }
}

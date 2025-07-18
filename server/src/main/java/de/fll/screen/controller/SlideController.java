package de.fll.screen.controller;

import de.fll.core.dto.SlideDTO;
import de.fll.screen.assembler.SlideAssembler;
import de.fll.screen.model.Slide;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.repository.SlideRepository;
import de.fll.screen.repository.SlideDeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/slides")
public class SlideController {
    @Autowired
    private SlideRepository slideRepository;
    @Autowired
    private SlideDeckRepository slideDeckRepository;
    @Autowired
    private SlideAssembler slideAssembler;

    // 获取所有slide，可选按deckId过滤
    @GetMapping
    public List<SlideDTO> getAllSlides(@RequestParam(value = "deckId", required = false) Long deckId) {
        List<Slide> slides;
        if (deckId != null) {
            slides = slideRepository.findBySlidedeck_Id(deckId);
        } else {
            slides = slideRepository.findAll();
        }
        return slides.stream().map(slideAssembler::toDTO).collect(Collectors.toList());
    }

    // 获取单个slide
    @GetMapping("/{id}")
    public SlideDTO getSlideById(@PathVariable Long id) {
        Slide slide = slideRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Slide not found"));
        return slideAssembler.toDTO(slide);
    }

    // 新增slide（需指定deckId）
    @PostMapping
    public SlideDTO createSlide(@RequestParam Long deckId, @RequestBody SlideDTO slideDTO) {
        SlideDeck deck = slideDeckRepository.findById(deckId).orElseThrow(() -> new IllegalArgumentException("SlideDeck not found"));
        Slide slide = slideAssembler.fromDTO(slideDTO);
        slide.setSlidedeck(deck);
        Slide saved = slideRepository.save(slide);
        return slideAssembler.toDTO(saved);
    }

    // 更新slide
    @PutMapping("/{id}")
    public SlideDTO updateSlide(@PathVariable Long id, @RequestBody SlideDTO slideDTO) {
        Slide existing = slideRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Slide not found"));
        Slide updated = slideAssembler.fromDTO(slideDTO);
        updated.setSlidedeck(existing.getSlidedeck()); // 保持原deck
        updated.setName(slideDTO.getName());
        updated.setIndex(slideDTO.getIndex());
        updated = slideRepository.save(updated);
        return slideAssembler.toDTO(updated);
    }

    // 删除slide
    @DeleteMapping("/{id}")
    public void deleteSlide(@PathVariable Long id) {
        slideRepository.deleteById(id);
    }
} 
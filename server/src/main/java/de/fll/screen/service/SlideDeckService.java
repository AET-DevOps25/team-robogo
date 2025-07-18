package de.fll.screen.service;

import de.fll.screen.model.SlideDeck;
import de.fll.screen.repository.SlideDeckRepository;
import de.fll.screen.repository.SlideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import de.fll.screen.model.Slide;
import java.util.ArrayList;
import de.fll.screen.model.ScoreSlide;
import de.fll.screen.model.ImageSlide;

@Service
@Transactional
public class SlideDeckService {

    @Autowired
    private SlideDeckRepository slideDeckRepository;

    @Autowired
    private SlideRepository slideRepository;


    public List<SlideDeck> getAllSlideDecks() {
        return slideDeckRepository.findAll();
    }

    public Optional<SlideDeck> getSlideDeckById(Long id) {
        return slideDeckRepository.findById(id);
    }

    public void deleteSlideDeck(Long id) {
        slideDeckRepository.deleteById(id);
    }

    public SlideDeck createSlideDeck(SlideDeck slideDeck) {
        return slideDeckRepository.save(slideDeck);
    }

    @Transactional
    public SlideDeck addSlideToDeck(Long deckId, Slide slide) {
        SlideDeck deck = slideDeckRepository.findById(deckId)
            .orElseThrow(() -> new IllegalArgumentException("SlideDeck not found"));
        
        // 验证 slide 是否存在
        Slide existingSlide = slideRepository.findById(slide.getId())
            .orElseThrow(() -> new IllegalArgumentException("Slide not found"));
        
        // 获取当前deck中的所有slides，确保获取最新状态
        List<Slide> currentSlides = slideRepository.findBySlidedeck_Id(deckId);
        
        // 设置新slide的索引为当前最大索引+1
        int newIndex = currentSlides.size();
        
        // 更新 slide 的关联关系和索引
        existingSlide.setSlidedeck(deck);
        existingSlide.setIndex(newIndex);
        slideRepository.save(existingSlide);
        
        // 更新deck的版本
        deck.setVersion(incrementVersion(deck.getVersion()));
        return slideDeckRepository.save(deck);
    }

    public SlideDeck reorderSlides(Long deckId, List<Long> newOrder) {
        // 第一步：全部设为负数，避免唯一约束冲突
        slideDeckRepository.setSlidesIndexNegative(deckId);

        // 第二步：按新顺序批量赋值
        for (int i = 0; i < newOrder.size(); i++) {
            slideDeckRepository.updateSlideIndexById(newOrder.get(i), i);
        }

        SlideDeck deck = slideDeckRepository.findById(deckId)
            .orElseThrow(() -> new IllegalArgumentException("SlideDeck not found"));
        deck.setVersion(incrementVersion(deck.getVersion()));
        return slideDeckRepository.save(deck);
    }

    @Transactional
    public SlideDeck removeSlideFromDeck(Long deckId, Long slideId) {
        // 验证 slide 是否存在且属于该 deck
        Slide slide = slideRepository.findById(slideId)
            .orElseThrow(() -> new IllegalArgumentException("Slide not found"));
        
        if (slide.getSlidedeck() == null) {
            throw new IllegalArgumentException("Slide does not belong to any deck");
        }
        
        // 将 slide 的 slidedeck 设置为 null，而不是删除 slide
        slide.setSlidedeck(null);
        slide.setIndex(null);
        slideRepository.save(slide);
        
        // 重新从数据库获取slides列表，确保数据一致性
        List<Slide> remainingSlides = slideRepository.findBySlidedeck_Id(deckId);
        if (!remainingSlides.isEmpty()) {
            // 第一步：全部设为负数，避免唯一约束冲突
            slideDeckRepository.setSlidesIndexNegative(deckId);
            
            // 第二步：按新顺序批量赋值
            for (int i = 0; i < remainingSlides.size(); i++) {
                Slide remainingSlide = remainingSlides.get(i);
                if (remainingSlide != null && remainingSlide.getId() != null) {
                    slideDeckRepository.updateSlideIndexById(remainingSlide.getId(), i);
                }
            }
        }
        
        SlideDeck deck = slideDeckRepository.findById(deckId)
            .orElseThrow(() -> new IllegalArgumentException("SlideDeck not found"));
        deck.setVersion(incrementVersion(deck.getVersion()));
        return slideDeckRepository.save(deck);
    }

    public SlideDeck updateSlideDeck(Long deckId, SlideDeck updateData) {
        SlideDeck existing = slideDeckRepository.findById(deckId)
            .orElseThrow(() -> new IllegalArgumentException("SlideDeck not found"));
        existing.setName(updateData.getName());
        existing.setTransitionTime(updateData.getTransitionTime());
        existing.setCompetition(updateData.getCompetition());
        // version is incremented to indicate that the slide deck has changed
        existing.setVersion(incrementVersion(existing.getVersion()));
        return slideDeckRepository.save(existing);
    }

    public SlideDeck updateSlideToDeck(Long deckId, Slide updatedSlide) {
        SlideDeck deck = slideDeckRepository.findById(deckId)
            .orElseThrow(() -> new IllegalArgumentException("SlideDeck not found"));
        List<Slide> slides = deck.getSlides();
        for (int i = 0; i < slides.size(); i++) {
            Slide s = slides.get(i);
            if (s.getId() == updatedSlide.getId()) {
                updatedSlide.setSlidedeck(deck);
                slides.set(i, updatedSlide);
                break;
            }
        }
        deck.setVersion(incrementVersion(deck.getVersion()));
        return slideDeckRepository.save(deck);
    }

    /**
     * 更新SlideDeck的播放速度
     * @param deckId SlideDeck的ID
     * @param transitionTime 新的过渡时间（秒）
     * @return 更新后的SlideDeck
     */
    public SlideDeck updateSlideDeckSpeed(Long deckId, Double transitionTime) {
        SlideDeck deck = slideDeckRepository.findById(deckId)
            .orElseThrow(() -> new IllegalArgumentException("SlideDeck not found"));
        
        // 验证速度值
        if (transitionTime != null && (transitionTime < 0.1 || transitionTime > 10.0)) {
            throw new IllegalArgumentException("Transition time must be between 0.1 and 10.0 seconds");
        }
        
        // 将秒转换为毫秒（因为数据库中存储的是毫秒）
        int transitionTimeMs = transitionTime != null ? (int)(transitionTime * 1000) : 1000;
        deck.setTransitionTime(transitionTimeMs);
        deck.setVersion(incrementVersion(deck.getVersion()));
        return slideDeckRepository.save(deck);
    }

    /**
     * 更新SlideDeck的最后更新时间
     * @param deckId SlideDeck的ID
     * @return 更新后的SlideDeck
     */
    public SlideDeck updateLastUpdate(Long deckId) {
        SlideDeck deck = slideDeckRepository.findById(deckId)
            .orElseThrow(() -> new IllegalArgumentException("SlideDeck not found"));
        deck.setLastUpdate(LocalDateTime.now());
        deck.setVersion(incrementVersion(deck.getVersion()));
        return slideDeckRepository.save(deck);
    }

    /**
     * 安全地递增版本号，处理溢出问题
     * 当版本号接近最大值时，重置为1
     */
    private int incrementVersion(int currentVersion) {
        // 当版本号达到 2,000,000,000 时重置为 1，避免溢出
        if (currentVersion >= 2000000000) {
            return 1;
        }
        return currentVersion + 1;
    }
} 
package de.fll.screen.service;

import de.fll.screen.model.SlideDeck;
import de.fll.screen.repository.SlideDeckRepository;
import de.fll.screen.repository.SlideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
        Slide newSlide;
        if (slide instanceof ScoreSlide) {
            ScoreSlide s = (ScoreSlide) slide;
            ScoreSlide scoreSlide = new ScoreSlide();
            scoreSlide.setName(s.getName());
            scoreSlide.setCategory(s.getCategory());
            newSlide = scoreSlide;
        } else if (slide instanceof ImageSlide) {
            ImageSlide s = (ImageSlide) slide;
            ImageSlide imageSlide = new ImageSlide();
            imageSlide.setName(s.getName());
            imageSlide.setImageMeta(s.getImageMeta());
            newSlide = imageSlide;
        } else {
            // 其他slide类型
            Slide generic = new Slide() {
                // 匿名子类实现抽象类
            };
            generic.setName(slide.getName());
            newSlide = generic;
        }
        newSlide.setSlidedeck(deck);
        newSlide.setIndex(deck.getSlides().size());
        deck.getSlides().add(newSlide);
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
        // 使用原生SQL删除slide，避免JPA级联问题
        slideRepository.deleteSlideFromDeck(deckId, slideId);
        
        // 重新从数据库获取slides列表，确保数据一致性
        List<Slide> remainingSlides = slideRepository.findBySlidedeck_Id(deckId);
        if (!remainingSlides.isEmpty()) {
            // 第一步：全部设为负数，避免唯一约束冲突
            slideDeckRepository.setSlidesIndexNegative(deckId);
            
            // 第二步：按新顺序批量赋值
            for (int i = 0; i < remainingSlides.size(); i++) {
                Slide slide = remainingSlides.get(i);
                if (slide != null && slide.getId() != null) {
                    slideDeckRepository.updateSlideIndexById(slide.getId(), i);
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
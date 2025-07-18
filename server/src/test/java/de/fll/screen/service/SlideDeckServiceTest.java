package de.fll.screen.service;

import de.fll.screen.model.SlideDeck;
import de.fll.screen.model.Slide;
import de.fll.screen.model.ScoreSlide;
import de.fll.screen.model.ImageSlide;
import de.fll.screen.repository.SlideDeckRepository;
import de.fll.screen.repository.SlideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SlideDeckServiceTest {
    @InjectMocks
    private SlideDeckService slideDeckService;

    @Mock
    private SlideDeckRepository slideDeckRepository;

    @Mock
    private SlideRepository slideRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddSlideToDeck() {
        // 创建测试数据
        SlideDeck deck = new SlideDeck();
        deck.setVersion(1);
        
        Slide existingSlide = mock(Slide.class);
        when(existingSlide.getId()).thenReturn(2L);
        when(existingSlide.getName()).thenReturn("Test Slide");
        
        when(slideDeckRepository.findById(1L)).thenReturn(Optional.of(deck));
        when(slideRepository.findById(2L)).thenReturn(Optional.of(existingSlide));
        when(slideRepository.findBySlidedeck_Id(1L)).thenReturn(new ArrayList<>());
        when(slideRepository.save(any(Slide.class))).thenAnswer(i -> i.getArgument(0));
        when(slideDeckRepository.save(any(SlideDeck.class))).thenAnswer(i -> i.getArgument(0));
        
        // 执行添加操作
        SlideDeck result = slideDeckService.addSlideToDeck(1L, existingSlide);
        
        // 验证结果
        assertEquals(2, result.getVersion());
        verify(slideRepository).save(existingSlide);
        verify(existingSlide).setSlidedeck(deck);
        verify(existingSlide).setIndex(0);
    }

    @Test
    void testRemoveScoreSlideFromDeck() {
        // 创建测试数据
        SlideDeck deck = new SlideDeck();
        deck.setVersion(1);
        
        ScoreSlide mockedScoreSlide = mock(ScoreSlide.class);
        when(mockedScoreSlide.getId()).thenReturn(2L);
        when(mockedScoreSlide.getName()).thenReturn("Test Score Slide");
        when(mockedScoreSlide.getSlidedeck()).thenReturn(deck);
        
        when(slideDeckRepository.findById(1L)).thenReturn(Optional.of(deck));
        when(slideRepository.findById(2L)).thenReturn(Optional.of(mockedScoreSlide));
        when(slideRepository.save(any(Slide.class))).thenAnswer(i -> i.getArgument(0));
        when(slideDeckRepository.save(any(SlideDeck.class))).thenAnswer(i -> i.getArgument(0));
        
        // 模拟 SlideRepository 的方法
        when(slideRepository.findBySlidedeck_Id(1L)).thenReturn(new ArrayList<>());
        doNothing().when(slideDeckRepository).setSlidesIndexNegative(1L);
        doNothing().when(slideDeckRepository).updateSlideIndexById(anyLong(), anyInt());
        
        // 执行移除操作
        SlideDeck result = slideDeckService.removeSlideFromDeck(1L, 2L);
        
        // 验证结果
        assertEquals(2, result.getVersion());
        
        // 验证 repository 调用
        verify(slideDeckRepository, times(1)).findById(1L);
        verify(slideDeckRepository, times(1)).save(any(SlideDeck.class));
        verify(slideRepository, times(1)).save(mockedScoreSlide);
        verify(mockedScoreSlide).setSlidedeck(null);
        verify(mockedScoreSlide).setIndex(null);
    }

    @Test
    void testRemoveImageSlideFromDeck() {
        // 创建测试数据
        SlideDeck deck = new SlideDeck();
        deck.setVersion(1);
        
        // 创建一个 ImageSlide 并模拟其 ID
        ImageSlide mockedImageSlide = mock(ImageSlide.class);
        when(mockedImageSlide.getId()).thenReturn(3L);
        when(mockedImageSlide.getName()).thenReturn("Test Image Slide");
        when(mockedImageSlide.getSlidedeck()).thenReturn(deck);
        
        when(slideDeckRepository.findById(1L)).thenReturn(Optional.of(deck));
        when(slideRepository.findById(3L)).thenReturn(Optional.of(mockedImageSlide));
        when(slideRepository.save(any(Slide.class))).thenAnswer(i -> i.getArgument(0));
        when(slideDeckRepository.save(any(SlideDeck.class))).thenAnswer(i -> i.getArgument(0));
        
        // 模拟 SlideRepository 的方法
        when(slideRepository.findBySlidedeck_Id(1L)).thenReturn(new ArrayList<>());
        doNothing().when(slideDeckRepository).setSlidesIndexNegative(1L);
        doNothing().when(slideDeckRepository).updateSlideIndexById(anyLong(), anyInt());
        
        // 执行移除操作
        SlideDeck result = slideDeckService.removeSlideFromDeck(1L, 3L);
        
        // 验证结果
        assertEquals(2, result.getVersion());
    }

    @Test
    void testRemoveSlideFromDeckWithMultipleSlides() {
        // 创建测试数据
        SlideDeck deck = new SlideDeck();
        deck.setVersion(1);
        
        // 创建多个 slides
        ScoreSlide mockedScoreSlide = mock(ScoreSlide.class);
        when(mockedScoreSlide.getId()).thenReturn(2L);
        when(mockedScoreSlide.getName()).thenReturn("Score Slide");
        when(mockedScoreSlide.getSlidedeck()).thenReturn(deck);
        
        ImageSlide mockedImageSlide = mock(ImageSlide.class);
        when(mockedImageSlide.getId()).thenReturn(3L);
        when(mockedImageSlide.getName()).thenReturn("Image Slide");
        when(mockedImageSlide.getSlidedeck()).thenReturn(deck);
        
        when(slideDeckRepository.findById(1L)).thenReturn(Optional.of(deck));
        when(slideRepository.findById(2L)).thenReturn(Optional.of(mockedScoreSlide));
        when(slideRepository.save(any(Slide.class))).thenAnswer(i -> i.getArgument(0));
        when(slideDeckRepository.save(any(SlideDeck.class))).thenAnswer(i -> i.getArgument(0));
        
        // 模拟 SlideRepository 的方法
        List<Slide> remainingSlides = Arrays.asList(mockedImageSlide);
        when(slideRepository.findBySlidedeck_Id(1L)).thenReturn(remainingSlides);
        doNothing().when(slideDeckRepository).setSlidesIndexNegative(1L);
        doNothing().when(slideDeckRepository).updateSlideIndexById(anyLong(), anyInt());
        
        // 执行移除操作 - 移除 ScoreSlide
        SlideDeck result = slideDeckService.removeSlideFromDeck(1L, 2L);
        
        // 验证结果
        assertEquals(2, result.getVersion());
    }

    @Test
    void testRemoveSlideFromDeckWithNonExistentSlide() {
        // 创建测试数据
        SlideDeck deck = new SlideDeck();
        deck.setVersion(1);
        
        when(slideDeckRepository.findById(1L)).thenReturn(Optional.of(deck));
        when(slideRepository.findById(999L)).thenReturn(Optional.empty());
        
        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            slideDeckService.removeSlideFromDeck(1L, 999L);
        });
        
        // 验证没有调用 save 方法
        verify(slideDeckRepository, never()).save(any(SlideDeck.class));
    }

    @Test
    void testRemoveSlideFromDeckWithEmptyDeck() {
        // 创建空的 deck
        SlideDeck deck = new SlideDeck();
        deck.setVersion(1);
        
        Slide slide = mock(Slide.class);
        when(slide.getId()).thenReturn(2L);
        when(slide.getSlidedeck()).thenReturn(deck);
        
        when(slideDeckRepository.findById(1L)).thenReturn(Optional.of(deck));
        when(slideRepository.findById(2L)).thenReturn(Optional.of(slide));
        when(slideRepository.save(any(Slide.class))).thenAnswer(i -> i.getArgument(0));
        when(slideDeckRepository.save(any(SlideDeck.class))).thenAnswer(i -> i.getArgument(0));
        
        // 模拟 SlideRepository 的方法
        when(slideRepository.findBySlidedeck_Id(1L)).thenReturn(new ArrayList<>());
        doNothing().when(slideDeckRepository).setSlidesIndexNegative(1L);
        doNothing().when(slideDeckRepository).updateSlideIndexById(anyLong(), anyInt());
        
        // 执行移除操作
        SlideDeck result = slideDeckService.removeSlideFromDeck(1L, 2L);
        
        // 验证结果
        assertEquals(2, result.getVersion()); // 版本仍然会递增
    }

    @Test
    void testRemoveSlideFromDeckDeckNotFound() {
        when(slideDeckRepository.findById(1L)).thenReturn(Optional.empty());
        
        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            slideDeckService.removeSlideFromDeck(1L, 2L);
        });
        
        // 验证没有调用 save 方法
        verify(slideDeckRepository, never()).save(any(SlideDeck.class));
    }

    @Test
    void testRemoveSlideFromDeckVersionIncrement() {
        // 测试版本号递增逻辑
        SlideDeck deck = new SlideDeck();
        deck.setVersion(1);
        
        ScoreSlide mockedScoreSlide = mock(ScoreSlide.class);
        when(mockedScoreSlide.getId()).thenReturn(2L);
        when(mockedScoreSlide.getSlidedeck()).thenReturn(deck);
        
        when(slideDeckRepository.findById(1L)).thenReturn(Optional.of(deck));
        when(slideRepository.findById(2L)).thenReturn(Optional.of(mockedScoreSlide));
        when(slideRepository.save(any(Slide.class))).thenAnswer(i -> i.getArgument(0));
        when(slideDeckRepository.save(any(SlideDeck.class))).thenAnswer(i -> i.getArgument(0));
        
        // 模拟 SlideRepository 的方法
        when(slideRepository.findBySlidedeck_Id(1L)).thenReturn(new ArrayList<>());
        doNothing().when(slideDeckRepository).setSlidesIndexNegative(1L);
        doNothing().when(slideDeckRepository).updateSlideIndexById(anyLong(), anyInt());
        
        SlideDeck result = slideDeckService.removeSlideFromDeck(1L, 2L);
        
        // 验证版本号递增
        assertEquals(2, result.getVersion());
    }

    @Test
    void testRemoveSlideFromDeckWithNullSlideId() {
        // 创建测试数据
        SlideDeck deck = new SlideDeck();
        deck.setVersion(1);
        
        when(slideDeckRepository.findById(1L)).thenReturn(Optional.of(deck));
        when(slideRepository.findById(null)).thenReturn(Optional.empty());
        
        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            slideDeckService.removeSlideFromDeck(1L, null);
        });
    }

    @Test
    void testRemoveSlideFromDeckWithWrongDeck() {
        // 模拟 slide 不属于指定 deck 的情况
        SlideDeck deck1 = new SlideDeck();
        deck1.setVersion(1);
        
        SlideDeck deck2 = new SlideDeck();
        deck2.setVersion(1);
        
        Slide slide = mock(Slide.class);
        when(slide.getId()).thenReturn(2L);
        when(slide.getSlidedeck()).thenReturn(deck2); // slide 属于 deck2
        
        when(slideDeckRepository.findById(1L)).thenReturn(Optional.of(deck1));
        when(slideRepository.findById(2L)).thenReturn(Optional.of(slide));
        
        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            slideDeckService.removeSlideFromDeck(1L, 2L);
        });
    }

    @Test
    void testRemoveSlideFromDeckWithForeignKeyConstraintFailure() {
        // 模拟外键约束失败的情况
        SlideDeck deck = new SlideDeck();
        deck.setVersion(1);
        
        // 创建 ScoreSlide
        ScoreSlide mockedScoreSlide = mock(ScoreSlide.class);
        when(mockedScoreSlide.getId()).thenReturn(2L);
        when(mockedScoreSlide.getName()).thenReturn("Score Slide with FK Constraint");
        when(mockedScoreSlide.getSlidedeck()).thenReturn(deck);
        
        when(slideDeckRepository.findById(1L)).thenReturn(Optional.of(deck));
        when(slideRepository.findById(2L)).thenReturn(Optional.of(mockedScoreSlide));
        
        // 模拟保存时抛出外键约束异常
        when(slideRepository.save(any(Slide.class))).thenThrow(
            new RuntimeException("Foreign key constraint violation")
        );
        
        // 模拟 SlideRepository 的方法
        when(slideRepository.findBySlidedeck_Id(1L)).thenReturn(new ArrayList<>());
        doNothing().when(slideDeckRepository).setSlidesIndexNegative(1L);
        doNothing().when(slideDeckRepository).updateSlideIndexById(anyLong(), anyInt());
        
        // 验证抛出异常
        assertThrows(RuntimeException.class, () -> {
            slideDeckService.removeSlideFromDeck(1L, 2L);
        });
        
        // 验证 repository 调用
        verify(slideDeckRepository, times(1)).findById(1L);
        verify(slideRepository, times(1)).save(any(Slide.class));
    }
} 
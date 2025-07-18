package de.fll.screen.service;

import de.fll.screen.model.SlideImageContent;
import de.fll.screen.model.SlideImageMeta;
import de.fll.screen.repository.SlideImageContentRepository;
import de.fll.screen.repository.SlideImageMetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlideImageServiceTest {

    @Mock
    private SlideImageMetaRepository metaRepository;

    @Mock
    private SlideImageContentRepository contentRepository;

    @Mock
    private RedisTemplate<String, byte[]> redisTemplate;

    @Mock
    private ValueOperations<String, byte[]> valueOperations;

    @InjectMocks
    private SlideImageService slideImageService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void getImageContentById_ShouldReturnCachedContent_WhenCacheHit() {
        // Arrange
        Long imageId = 1L;
        byte[] cachedContent = "test image content".getBytes();
        when(valueOperations.get("image:content:1")).thenReturn(cachedContent);

        // Act
        byte[] result = slideImageService.getImageContentById(imageId);

        // Assert
        assertNotNull(result);
        assertArrayEquals(cachedContent, result);
        verify(contentRepository, never()).findByMetaId(any());
    }

    @Test
    void getImageContentById_ShouldLoadFromDatabaseAndCache_WhenCacheMiss() {
        // Arrange
        Long imageId = 1L;
        byte[] dbContent = "test image content".getBytes();
        
        SlideImageContent content = new SlideImageContent();
        content.setContent(dbContent);
        
        when(valueOperations.get("image:content:1")).thenReturn(null);
        when(contentRepository.findByMetaId(imageId)).thenReturn(content);

        // Act
        byte[] result = slideImageService.getImageContentById(imageId);

        // Assert
        assertNotNull(result);
        assertArrayEquals(dbContent, result);
        verify(valueOperations).set(eq("image:content:1"), eq(dbContent), eq(24L), eq(java.util.concurrent.TimeUnit.HOURS));
    }

    @Test
    void getImageContentById_ShouldReturnNull_WhenImageNotFound() {
        // Arrange
        Long imageId = 999L;
        when(valueOperations.get("image:content:999")).thenReturn(null);
        when(contentRepository.findByMetaId(imageId)).thenReturn(null);

        // Act
        byte[] result = slideImageService.getImageContentById(imageId);

        // Assert
        assertNull(result);
        verify(valueOperations, never()).set(any(), any(), anyLong(), any());
    }

    @Test
    void clearImageCache_ShouldDeleteCacheKey() {
        // Arrange
        Long imageId = 1L;

        // Act
        slideImageService.clearImageCache(imageId);

        // Assert
        verify(redisTemplate).delete("image:content:1");
    }

    @Test
    void clearAllImageCache_ShouldDeleteAllImageCacheKeys() {
        // Arrange
        java.util.Set<String> keys = java.util.Set.of("image:content:1", "image:content:2");
        when(redisTemplate.keys("image:content:*")).thenReturn(keys);

        // Act
        slideImageService.clearAllImageCache();

        // Assert
        verify(redisTemplate).delete(keys);
    }
} 
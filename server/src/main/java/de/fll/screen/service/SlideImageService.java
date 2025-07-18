package de.fll.screen.service;

import de.fll.core.dto.ImageSlideMetaDTO;
import de.fll.screen.assembler.ImageSlideMetaAssembler;
import de.fll.screen.model.SlideImageContent;
import de.fll.screen.model.SlideImageMeta;
import de.fll.screen.repository.SlideImageContentRepository;
import de.fll.screen.repository.SlideImageMetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SlideImageService {
    @Autowired
    private SlideImageMetaRepository metaRepository;
    @Autowired
    private SlideImageContentRepository contentRepository;
    @Autowired
    private RedisTemplate<String, byte[]> redisTemplate;
    
    @Autowired
    private ImageSlideMetaAssembler imageSlideMetaAssembler;
    
    // 缓存配置
    private static final String IMAGE_CACHE_PREFIX = "image:content:";
    private static final long CACHE_TTL_HOURS = 24; // 缓存24小时

    public List<ImageSlideMetaDTO> getAllImages() {
        return metaRepository.findAll().stream()
                .map(imageSlideMetaAssembler::toDTO)
                .collect(Collectors.toList());
    }

    public ImageSlideMetaDTO uploadImage(MultipartFile file) throws IOException {
        SlideImageMeta meta = new SlideImageMeta();
        meta.setName(file.getOriginalFilename());
        meta.setContentType(file.getContentType());
        SlideImageMeta savedMeta = metaRepository.save(meta);

        SlideImageContent content = new SlideImageContent();
        content.setContent(file.getBytes());
        content.setMeta(savedMeta);
        contentRepository.save(content);
        
        // 清除所有图片缓存，因为可能有新的图片被添加
        clearAllImageCache();

        return imageSlideMetaAssembler.toDTO(savedMeta);
    }

    public Optional<SlideImageMeta> getMetaById(Long id) {
        return metaRepository.findById(id);
    }

    public SlideImageContent getContentByMetaId(Long id) {
        return contentRepository.findByMetaId(id);
    }
    
    /**
     * 获取图片内容，优先从缓存获取，缓存未命中则从数据库加载并缓存
     * @param id 图片元数据ID
     * @return 图片二进制内容
     */
    public byte[] getImageContentById(Long id) {
        String cacheKey = IMAGE_CACHE_PREFIX + id;
        
        // 尝试从缓存获取
        byte[] cachedContent = redisTemplate.opsForValue().get(cacheKey);
        if (cachedContent != null) {
            return cachedContent;
        }
        
        // 缓存未命中，从数据库加载
        SlideImageContent content = contentRepository.findByMetaId(id);
        if (content != null && content.getContent() != null) {
            // 缓存到Redis，设置24小时过期
            redisTemplate.opsForValue().set(cacheKey, content.getContent(), CACHE_TTL_HOURS, TimeUnit.HOURS);
            return content.getContent();
        }
        
        return null;
    }
    
    /**
     * 清除指定图片的缓存
     * @param id 图片元数据ID
     */
    public void clearImageCache(Long id) {
        String cacheKey = IMAGE_CACHE_PREFIX + id;
        redisTemplate.delete(cacheKey);
    }
    
    /**
     * 清除所有图片缓存
     */
    public void clearAllImageCache() {
        // 获取所有以 IMAGE_CACHE_PREFIX 开头的键
        Set<String> keys = redisTemplate.keys(IMAGE_CACHE_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
} 
package de.fll.screen.repository;

import de.fll.screen.model.Slide;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface SlideRepository extends JpaRepository<Slide, Long> {
    List<Slide> findBySlidedeck(SlideDeck slidedeck);
    List<Slide> findBySlidedeck_Id(Long slidedeckId);
    
    @Query("SELECT s FROM Score s JOIN s.team t WHERE t.category.id = :categoryId")
    List<Score> findScoresByScoreSlideCategory(@Param("categoryId") Long categoryId);
    
    /**
     * 使用原生SQL删除指定deck中的指定slide
     * 避免JPA级联问题，直接操作数据库
     * @param deckId SlideDeck的ID
     * @param slideId Slide的ID
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM slide WHERE id = :slideId AND slidedeck_id = :deckId", nativeQuery = true)
    void deleteSlideFromDeck(@Param("deckId") Long deckId, @Param("slideId") Long slideId);
} 
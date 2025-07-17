package de.fll.screen.repository;

import de.fll.screen.model.SlideDeck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface SlideDeckRepository extends JpaRepository<SlideDeck, Long> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE slide SET index = -index - 1000 WHERE slidedeck_id = :deckId", nativeQuery = true)
    void setSlidesIndexNegative(@Param("deckId") Long deckId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE slide SET index = :newIndex WHERE id = :slideId", nativeQuery = true)
    void updateSlideIndexById(@Param("slideId") Long slideId, @Param("newIndex") int newIndex);

    /**
     * 查找包含特定Score的SlideDeck
     * 通过Score的teamId和categoryId来查找包含该Score的ScoreSlide所在的SlideDeck
     */
    @Query(value = """
        SELECT DISTINCT sd.* FROM slide_deck sd
        INNER JOIN slide s ON sd.id = s.slidedeck_id
        WHERE s.type = 'SCORE' 
        AND s.category_id = :categoryId
        AND EXISTS (
            SELECT 1 FROM score sc 
            INNER JOIN team t ON sc.team_id = t.id 
            WHERE t.category_id = :categoryId 
            AND sc.id = :scoreId
        )
        """, nativeQuery = true)
    List<SlideDeck> findSlideDecksByScore(@Param("scoreId") Long scoreId, @Param("categoryId") Long categoryId);
} 
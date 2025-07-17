package de.fll.screen.repository;

import de.fll.screen.model.SlideDeck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface SlideDeckRepository extends JpaRepository<SlideDeck, Long> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE slide SET index = -index - 1000 WHERE slidedeck_id = :deckId", nativeQuery = true)
    void setSlidesIndexNegative(@Param("deckId") Long deckId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE slide SET index = :newIndex WHERE id = :slideId", nativeQuery = true)
    void updateSlideIndexById(@Param("slideId") Long slideId, @Param("newIndex") int newIndex);
} 
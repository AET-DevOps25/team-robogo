package de.fll.screen.repository;

import de.fll.screen.model.Slide;
import de.fll.screen.model.SlideDeck;
import de.fll.screen.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SlideRepository extends JpaRepository<Slide, Long> {
    List<Slide> findBySlidedeck(SlideDeck slidedeck);
    List<Slide> findBySlidedeck_Id(Long slidedeckId);
    
    @Query("SELECT s FROM Score s JOIN s.team t WHERE t.category.id = :categoryId")
    List<Score> findScoresByScoreSlideCategory(@Param("categoryId") Long categoryId);
} 
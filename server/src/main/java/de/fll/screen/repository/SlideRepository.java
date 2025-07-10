package de.fll.screen.repository;

import de.fll.screen.model.Slide;
import de.fll.screen.model.SlideDeck;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SlideRepository extends JpaRepository<Slide, Long> {
    List<Slide> findBySlidedeck(SlideDeck slidedeck);
    List<Slide> findBySlidedeck_Id(Long slidedeckId);
} 
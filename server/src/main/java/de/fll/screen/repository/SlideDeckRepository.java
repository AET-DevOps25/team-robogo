package de.fll.screen.repository;

import de.fll.screen.model.SlideDeck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlideDeckRepository extends JpaRepository<SlideDeck, Long> {
} 
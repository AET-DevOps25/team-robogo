package de.fll.screen.repository;

import de.fll.screen.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    Optional<Score> findByTeam_Id(Long teamId);
    List<Score> findByTeam_Category_Id(Long categoryId);
} 
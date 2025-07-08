package de.fll.screen.repository;

import de.fll.screen.model.Score;
import de.fll.screen.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    List<Score> findByTeam(Team team);
} 
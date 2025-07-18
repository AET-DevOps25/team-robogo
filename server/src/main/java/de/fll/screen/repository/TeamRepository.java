package de.fll.screen.repository;

import de.fll.screen.model.Team;
import de.fll.screen.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByCategory(Category category);
} 
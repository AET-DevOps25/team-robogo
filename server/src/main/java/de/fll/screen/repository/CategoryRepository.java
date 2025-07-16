package de.fll.screen.repository;

import de.fll.screen.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import de.fll.screen.model.Team;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c.teams FROM Category c WHERE c.id = :categoryId")
    Set<Team> findTeamsByCategoryId(@Param("categoryId") Long categoryId);
} 
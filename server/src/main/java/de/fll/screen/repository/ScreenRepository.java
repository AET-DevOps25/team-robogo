package de.fll.screen.repository;

import de.fll.screen.model.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreenRepository extends JpaRepository<Screen, Long> {
    // 可扩展自定义查询
}

package de.fll.screen.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Gauge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import de.fll.screen.repository.ScreenRepository;
import de.fll.screen.repository.SlideDeckRepository;
import de.fll.screen.repository.ScoreRepository;
import de.fll.screen.repository.TeamRepository;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter slideDeckUpdateCounter(MeterRegistry meterRegistry) {
        return Counter.builder("slide_deck_updates_total")
                .description("Total number of slide deck updates")
                .register(meterRegistry);
    }

    @Bean
    public Counter screenStatusChangeCounter(MeterRegistry meterRegistry) {
        return Counter.builder("screen_status_changes_total")
                .description("Total number of screen status changes")
                .register(meterRegistry);
    }

    @Bean
    public Counter scoreUpdateCounter(MeterRegistry meterRegistry) {
        return Counter.builder("score_updates_total")
                .description("Total number of score updates")
                .register(meterRegistry);
    }

    @Bean
    public Gauge activeScreensGauge(MeterRegistry meterRegistry, ScreenRepository screenRepository) {
        return Gauge.builder("active_screens_count", screenRepository, 
            repo -> repo.findAll().stream()
                .filter(screen -> "ONLINE".equals(screen.getStatus().name()))
                .count())
                .description("Number of currently active screens")
                .register(meterRegistry);
    }

    @Bean
    public Gauge totalScreensGauge(MeterRegistry meterRegistry, ScreenRepository screenRepository) {
        return Gauge.builder("total_screens_count", screenRepository, repo -> repo.count())
                .description("Total number of screens in the system")
                .register(meterRegistry);
    }

    @Bean
    public Gauge totalSlideDecksGauge(MeterRegistry meterRegistry, SlideDeckRepository slideDeckRepository) {
        return Gauge.builder("total_slide_decks_count", slideDeckRepository, repo -> repo.count())
                .description("Total number of slide decks in the system")
                .register(meterRegistry);
    }

    @Bean
    public Gauge totalTeamsGauge(MeterRegistry meterRegistry, TeamRepository teamRepository) {
        return Gauge.builder("total_teams_count", teamRepository, repo -> repo.count())
                .description("Total number of teams in the system")
                .register(meterRegistry);
    }

    @Bean
    public Gauge totalScoresGauge(MeterRegistry meterRegistry, ScoreRepository scoreRepository) {
        return Gauge.builder("total_scores_count", scoreRepository, repo -> repo.count())
                .description("Total number of scores in the system")
                .register(meterRegistry);
    }
} 
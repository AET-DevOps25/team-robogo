package de.fll.screen.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("SCORE")
public class ScoreSlide extends Slide {

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "scoreSlide", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Score> scores;

    public Category getCategory() { return category; }

    public void setCategory(Category category) { this.category = category; }

    public List<Score> getScores() { return scores; }

    public void setScores(List<Score> scores) { this.scores = scores; }
} 
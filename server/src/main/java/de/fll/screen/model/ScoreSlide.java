package de.fll.screen.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("SCORE")
public class ScoreSlide extends Slide {

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @OneToMany(mappedBy = "scoreSlide", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Score> scores;

    public Category getCategory() { return category; }

    public void setCategory(Category category) { this.category = category; }

    public List<Score> getScores() { return scores; }

    public void setScores(List<Score> scores) { this.scores = scores; }

    @Override
    public String toString() {
        return "ScoreSlide{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", index=" + getIndex() +
                ", category=" + (category != null ? category.getId() : null) +
                '}';
    }
} 
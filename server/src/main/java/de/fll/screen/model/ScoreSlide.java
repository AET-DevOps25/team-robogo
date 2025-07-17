package de.fll.screen.model;


import jakarta.persistence.*;

@Entity
@DiscriminatorValue("SCORE")
public class ScoreSlide extends Slide {

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    public Category getCategory() { return category; }

    public void setCategory(Category category) { this.category = category; }

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
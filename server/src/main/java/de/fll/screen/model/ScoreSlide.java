package de.fll.screen.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("SCORE")
public class ScoreSlide extends Slide {
} 
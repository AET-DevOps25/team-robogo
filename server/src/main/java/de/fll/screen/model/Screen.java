package de.fll.screen.model;

import jakarta.persistence.*;

@Entity
@Table(name = "screen")
public class Screen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ScreenStatus status;

    @ManyToOne
    @JoinColumn(name = "slide_deck_id")
    private SlideDeck slideDeck;

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ScreenStatus getStatus() { return status; }
    public void setStatus(ScreenStatus status) { this.status = status; }
    public SlideDeck getSlideDeck() { return slideDeck; }
    public void setSlideDeck(SlideDeck slideDeck) { this.slideDeck = slideDeck; }
}

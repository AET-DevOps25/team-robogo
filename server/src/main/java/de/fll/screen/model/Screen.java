package de.fll.screen.model;

import jakarta.persistence.*;

@Entity
@Table(name = "screen")
public class Screen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String status; // online/offline

    @ManyToOne
    @JoinColumn(name = "slide_deck_id")
    private SlideDeck slideDeck;

    // getter/setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public SlideDeck getSlideDeck() { return slideDeck; }
    public void setSlideDeck(SlideDeck slideDeck) { this.slideDeck = slideDeck; }
}

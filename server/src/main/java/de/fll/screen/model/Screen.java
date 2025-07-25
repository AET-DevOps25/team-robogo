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

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "slide_deck_id", nullable = true)
    private SlideDeck slideDeck;

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ScreenStatus getStatus() { return status; }
    public void setStatus(ScreenStatus status) { this.status = status; }
    public SlideDeck getSlideDeck() { return slideDeck; }
    public void setSlideDeck(SlideDeck slideDeck) { this.slideDeck = slideDeck; }

    @Override
    public String toString() {
        return "Screen{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", slideDeck=" + (slideDeck != null ? slideDeck.getId() : null) +
                '}';
    }
}

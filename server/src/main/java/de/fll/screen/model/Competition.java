package de.fll.screen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Entity
@Table(name = "competition")
public final class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "internal_id", nullable = false)
    private long internalId;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "competition", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SlideDeck> slideDecks = new HashSet<>();

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getInternalId() {
        return internalId;
    }

    public void setInternalId(long internalId) {
        this.internalId = internalId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<SlideDeck> getSlideDecks() {
        return slideDecks;
    }
}

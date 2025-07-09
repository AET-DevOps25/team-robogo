package de.fll.screen.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Entity
public class SlideDeck {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "transition_time", nullable = false)
	private int transitionTime;

	// version is used to track changes to the slide deck
	// it is incremented whenever the slide deck is changed
	// it is used to determine if the slide deck has changed since the last time it was loaded
	// if it has, the client should reload the slide deck
	// this is used to prevent race conditions when multiple clients are loading the same slide deck
	// and to prevent the client from displaying an outdated slide deck
	@Column(name = "version", nullable = false)
	private int version = 0;

	@OneToMany(mappedBy = "slidedeck", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderColumn(name = "index")
	private final List<Slide> slides = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "competition_id", nullable = false)
	@JsonIgnore
	private Competition competition;

	public SlideDeck() {
		this("", 0, new ArrayList<>(), null);
	}

	public SlideDeck(String name, int transitionTime, List<Slide> slides, Competition competition) {
		this.name = name;
		this.transitionTime = transitionTime;
		this.slides.addAll(slides);
		this.competition = competition;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTransitionTime() {
		return transitionTime;
	}

	public void setTransitionTime(int transitionTime) {
		this.transitionTime = transitionTime;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public List<Slide> getSlides() {
		return slides;
	}

	public Competition getCompetition() {
		return competition;
	}

	public void setCompetition(Competition competition) {
		this.competition = competition;
	}
}

package de.fll.screen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "slide")
public abstract class Slide {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "index", nullable = false)
	private int index;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "slidedeck_id", nullable = false)
	private SlideDeck slidedeck;

	public long getId() { return id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public int getIndex() { return index; }
	public void setIndex(int index) { this.index = index; }
	public SlideDeck getSlidedeck() { return slidedeck; }
	public void setSlidedeck(SlideDeck slidedeck) { this.slidedeck = slidedeck; }
}

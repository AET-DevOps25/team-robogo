package de.fll.screen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class Slide {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name")
	private String name;

	@ManyToOne
	@JoinColumn(name = "slidedeck_id", nullable = false)
	private SlideDeck slidedeck;

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public SlideDeck getSlidedeck() { return slidedeck; }
	public void setSlidedeck(SlideDeck slidedeck) { this.slidedeck = slidedeck; }
}

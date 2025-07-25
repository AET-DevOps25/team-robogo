package de.fll.screen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Table(
    name = "slide",
    uniqueConstraints = @UniqueConstraint(columnNames = {"slidedeck_id", "index"}),
    indexes = @Index(name = "idx_slide_deck_index", columnList = "slidedeck_id, index")
)
public abstract class Slide {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "index", nullable = false)
	private Integer index;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "slidedeck_id", nullable = false)
	private SlideDeck slidedeck;

	public Long getId() { return id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public Integer getIndex() { return index; }
	public void setIndex(Integer index) { this.index = index; }
	public SlideDeck getSlidedeck() { return slidedeck; }
	public void setSlidedeck(SlideDeck slidedeck) { this.slidedeck = slidedeck; }

	public SlideType getType() {
		String typeStr = this.getClass().getSimpleName().replace("Slide", "").toUpperCase();
		try {
			return SlideType.valueOf(typeStr);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

    @Override
    public String toString() {
        return "Slide{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", index=" + index +
                ", slidedeck=" + (slidedeck != null ? slidedeck.getId() : null) +
                '}';
    }
}

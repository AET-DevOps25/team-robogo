package de.fll.screen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Entity
// TODO add subclasses
public class Slide {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@Column(name = "name", nullable = true)
	private String name;

	@ManyToOne
	@JoinColumn(name = "slidedeck_id", nullable = false)
	private SlideDeck slidedeck;

	@ManyToOne
	@JoinColumn(name = "image_id", nullable = true)
	private SlideImageMeta imageMeta;

	public Slide() {

	}

	public Slide(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SlideImageMeta getImageMeta() {
		return imageMeta;
	}

	public void setImageMeta(SlideImageMeta imageMeta) {
		this.imageMeta = imageMeta;
	}
}

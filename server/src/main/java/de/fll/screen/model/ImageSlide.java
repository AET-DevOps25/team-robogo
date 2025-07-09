package de.fll.screen.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("IMAGE")
public class ImageSlide extends Slide {
    @ManyToOne
    @JoinColumn(name = "image_id", nullable = true)
    private SlideImageMeta imageMeta;

    public SlideImageMeta getImageMeta() { return imageMeta; }
    public void setImageMeta(SlideImageMeta imageMeta) { this.imageMeta = imageMeta; }
}
package de.fll.screen.model;

import jakarta.persistence.*;

@Entity
@Table(name = "slide_image_content")
public class SlideImageContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private byte[] content;

    @OneToOne
    @JoinColumn(name = "image_id", nullable = false, unique = true)
    private SlideImageMeta meta;

    public Long getId() { return id; }
    public byte[] getContent() { return content; }
    public void setContent(byte[] content) { this.content = content; }
    public SlideImageMeta getMeta() { return meta; }
    public void setMeta(SlideImageMeta meta) { this.meta = meta; }

    @Override
    public String toString() {
        return "SlideImageContent{" +
                "id=" + id +
                ", meta=" + (meta != null ? meta.getId() : null) +
                '}';
    }
} 
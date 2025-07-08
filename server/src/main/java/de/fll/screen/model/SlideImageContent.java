package de.fll.screen.model;

import jakarta.persistence.*;

@Entity
public class SlideImageContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private byte[] content;

    @OneToOne
    @JoinColumn(name = "image_id", nullable = false)
    private SlideImageMeta meta;

    public Long getId() { return id; }
    public byte[] getContent() { return content; }
    public void setContent(byte[] content) { this.content = content; }
    public SlideImageMeta getMeta() { return meta; }
    public void setMeta(SlideImageMeta meta) { this.meta = meta; }
} 
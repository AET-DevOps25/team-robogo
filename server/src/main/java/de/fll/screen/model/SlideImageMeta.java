package de.fll.screen.model;

import jakarta.persistence.*;

@Entity
@Table(name = "slide_image_meta")
public class SlideImageMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @OneToOne(mappedBy = "meta", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private SlideImageContent content;

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public SlideImageContent getContent() { return content; }
    public void setContent(SlideImageContent content) { this.content = content; }
} 
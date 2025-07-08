package de.fll.core.dto;

public class SlideImageMetaDTO {
    private Long id;
    private String name;
    private String contentType;

    public SlideImageMetaDTO(Long id, String name, String contentType) {
        this.id = id;
        this.name = name;
        this.contentType = contentType;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getContentType() { return contentType; }
} 
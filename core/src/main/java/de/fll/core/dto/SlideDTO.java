package de.fll.core.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ScoreSlideDTO.class, name = "SCORE"),
    @JsonSubTypes.Type(value = ImageSlideDTO.class, name = "IMAGE")
})
@Data
public class SlideDTO {
    private Long id;
    private String name;
    private int index;
    private String type;
} 
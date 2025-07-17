package de.fll.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreDTO {
    private Long id;
    private Double points;
    private Integer time;
    private Boolean highlight;
    private TeamDTO team;
    private Integer rank;
}
package de.fll.screen.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

import de.fll.core.proto.TeamOuterClass;
import de.fll.core.proto.ScoreOuterClass;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Entity
public final class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "round")
    private final List<Score> scores;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;

    public Team() {
        this("");
    }

    public Team(String name) {
        this(name, 5);
    }

    public Team(int rounds) {
        this("", rounds);
    }

    public Team(String name, int rounds) {
        this.name = name;
        this.scores = new ArrayList<>(rounds);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Score> getScores() {
        return scores;
    }

    @JsonIgnore
    @Nullable
    public Score getScoreForRound(int index) {
        if (index < 0 || index >= scores.size()) {
            return null;
        }
        return scores.get(index);
    }

    @Override
    public String toString() {
        return "Team{" + "name='" + name + '\'' + ", scores=" + scores + '}';
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public static Team fromProto(TeamOuterClass.Team proto) {
        Team team = new Team();
        team.setId(proto.getId());
        team.setName(proto.getName());
        for (ScoreOuterClass.Score scoreProto : proto.getScoresList()) {
            Score score = new Score(scoreProto.getPoints(), scoreProto.getTime());
            team.getScores().add(score);
        }
        return team;
    }

    public TeamOuterClass.Team toProto() {
        TeamOuterClass.Team.Builder builder = TeamOuterClass.Team.newBuilder()
            .setId(this.getId())
            .setName(this.getName());
        for (Score score : this.getScores()) {
            builder.addScores(
                ScoreOuterClass.Score.newBuilder()
                    .setPoints(score.getPoints())
                    .setTime(score.getTime())
                    .build()
            );
        }
        return builder.build();
    }
}

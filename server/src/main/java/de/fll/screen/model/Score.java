package de.fll.screen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Entity
@Table(name = "score")
public final class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "points", nullable = false)
    private double points;

    @Column(name = "time", nullable = false)
    private int time;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", unique = true, nullable = false)
    private Team team;

    public Score() {
        this(-1, -1);
    }

    public Score(double points, int time) {
        this.points = points;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Score score)) {
            return false;
        }
        return Double.compare(points, score.points) == 0 && time == score.time;
    }

    @Override
    public String toString() {
        return "Score{" +
                "id=" + id +
                ", points=" + points +
                ", time=" + time +
                ", team=" + (team != null ? team.getId() : null) +
                '}';
    }

    public int compareToWithTime(Score o) {
        int cmpPoints = Double.compare(points, o.points);
        if (cmpPoints == 0) {
            return -Integer.compare(time, o.time);
        }
        return cmpPoints;
    }

    /**
     * Adds the points and time of the given score to this score.
     * This is useful as a tie-breaker in case of equal points.
     *
     * @param score the score to add
     * @return a new combined Score instance
     */
    public Score add(Score score) {
        return new Score(points + score.points, time + score.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(points, time);
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public double getPoints() {
        return points;
    }

    public int getTime() {
        return time;
    }

    public Team getTeam() {
        return team;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

}

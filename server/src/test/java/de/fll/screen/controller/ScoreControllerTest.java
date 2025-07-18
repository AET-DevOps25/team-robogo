package de.fll.screen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fll.core.dto.ScoreDTO;
import de.fll.core.dto.ScoreSlideDTO;
import de.fll.core.dto.TeamDTO;
import de.fll.screen.assembler.ScoreSlideAssembler;
import de.fll.screen.model.Score;
import de.fll.screen.model.ScoreSlide;
import de.fll.screen.model.Team;
import de.fll.screen.service.ScoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(
    controllers = {ScoreController.class, GlobalExceptionHandler.class},
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
    }
)
public class ScoreControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScoreService scoreService;

    @MockBean
    private ScoreSlideAssembler scoreSlideAssembler;

    @Autowired
    private ObjectMapper objectMapper;

    private Score score1;
    private Score score2;
    private ScoreDTO scoreDTO1;
    private ScoreDTO scoreDTO2;
    private Team team1;
    private TeamDTO teamDTO1;

    @BeforeEach
    void setUp() {
        team1 = new Team();
        setId(team1, 1L);
        team1.setName("Test Team 1");

        teamDTO1 = new TeamDTO();
        teamDTO1.setId(1L);
        teamDTO1.setName("Test Team 1");

        score1 = new Score();
        setId(score1, 1L);
        score1.setPoints(100.0);
        score1.setTime(120);
        score1.setTeam(team1);

        score2 = new Score();
        setId(score2, 2L);
        score2.setPoints(200.0);
        score2.setTime(180);
        score2.setTeam(team1);

        scoreDTO1 = new ScoreDTO();
        scoreDTO1.setId(1L);
        scoreDTO1.setPoints(100.0);
        scoreDTO1.setTime(120);
        scoreDTO1.setTeam(teamDTO1);

        scoreDTO2 = new ScoreDTO();
        scoreDTO2.setId(2L);
        scoreDTO2.setPoints(200.0);
        scoreDTO2.setTime(180);
        scoreDTO2.setTeam(teamDTO1);
    }

    private void setId(Object entity, Long id) {
        try {
            // 首先尝试在当前类中查找 id 字段
            java.lang.reflect.Field field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            setFieldValue(field, entity, id);
        } catch (NoSuchFieldException e) {
            // 如果当前类没有 id 字段，尝试在父类中查找
            try {
                java.lang.reflect.Field field = entity.getClass().getSuperclass().getDeclaredField("id");
                field.setAccessible(true);
                setFieldValue(field, entity, id);
            } catch (Exception ex) {
                throw new RuntimeException("Cannot set id field on " + entity.getClass().getName(), ex);
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot set id field on " + entity.getClass().getName(), e);
        }
    }

    private void setFieldValue(java.lang.reflect.Field field, Object entity, Long id) throws IllegalAccessException {
        if (field.getType() == long.class) {
            field.setLong(entity, id);
        } else if (field.getType() == Long.class) {
            field.set(entity, id);
        } else if (field.getType() == int.class) {
            field.setInt(entity, id.intValue());
        } else if (field.getType() == Integer.class) {
            field.set(entity, id.intValue());
        } else {
            field.set(entity, id);
        }
    }

    @Test
    void testGetScoresByCategory() throws Exception {
        List<ScoreDTO> scores = Arrays.asList(scoreDTO1, scoreDTO2);
        when(scoreService.getScoreDTOsWithHighlight(1L)).thenReturn(scores);

        mockMvc.perform(get("/scores/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].points").value(100.0))
                .andExpect(jsonPath("$[0].time").value(120))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].points").value(200.0))
                .andExpect(jsonPath("$[1].time").value(180));
    }

    @Test
    void testAddScore() throws Exception {
        Score newScore = new Score();
        setId(newScore, 3L);
        newScore.setPoints(150.0);
        newScore.setTime(150);
        newScore.setTeam(team1);

        ScoreDTO newScoreDTO = new ScoreDTO();
        newScoreDTO.setId(3L);
        newScoreDTO.setPoints(150.0);
        newScoreDTO.setTime(150);
        newScoreDTO.setTeam(teamDTO1);

        when(scoreService.addScore(eq(1L), eq(150.0), eq(150))).thenReturn(newScore);

        mockMvc.perform(post("/scores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newScoreDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.points").value(150.0))
                .andExpect(jsonPath("$.time").value(150))
                .andExpect(jsonPath("$.team.id").value(1))
                .andExpect(jsonPath("$.team.name").value("Test Team 1"));
    }

    @Test
    void testAddScoreWithoutTeam() throws Exception {
        ScoreDTO scoreWithoutTeam = new ScoreDTO();
        scoreWithoutTeam.setPoints(150.0);
        scoreWithoutTeam.setTime(150);
        // 不设置 team

        mockMvc.perform(post("/scores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scoreWithoutTeam)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testUpdateScore() throws Exception {
        Score updatedScore = new Score();
        setId(updatedScore, 1L);
        updatedScore.setPoints(300.0);
        updatedScore.setTime(200);
        updatedScore.setTeam(team1);

        ScoreDTO updatedScoreDTO = new ScoreDTO();
        updatedScoreDTO.setId(1L);
        updatedScoreDTO.setPoints(300.0);
        updatedScoreDTO.setTime(200);
        updatedScoreDTO.setTeam(teamDTO1);

        when(scoreService.updateScore(eq(1L), eq(1L), eq(300.0), eq(200))).thenReturn(updatedScore);

        mockMvc.perform(put("/scores/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedScoreDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.points").value(300.0))
                .andExpect(jsonPath("$.time").value(200))
                .andExpect(jsonPath("$.team.id").value(1))
                .andExpect(jsonPath("$.team.name").value("Test Team 1"));
    }

    @Test
    void testUpdateScoreWithoutTeam() throws Exception {
        ScoreDTO scoreWithoutTeam = new ScoreDTO();
        scoreWithoutTeam.setPoints(300.0);
        scoreWithoutTeam.setTime(200);
        // 不设置 team

        mockMvc.perform(put("/scores/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scoreWithoutTeam)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testDeleteScore() throws Exception {
        mockMvc.perform(delete("/scores/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateScoreSlide() throws Exception {
        // 创建真实的 ScoreSlide 对象而不是 mock
        ScoreSlide scoreSlide = new ScoreSlide();
        setId(scoreSlide, 1L);
        scoreSlide.setName("Test Score Slide");
        scoreSlide.setIndex(0);

        ScoreSlideDTO scoreSlideDTO = new ScoreSlideDTO();
        scoreSlideDTO.setId(1L);
        scoreSlideDTO.setName("Test Score Slide");
        scoreSlideDTO.setIndex(0);
        scoreSlideDTO.setType("SCORE");

        when(scoreService.createScoreSlideFromDTO(any(ScoreSlideDTO.class))).thenReturn(scoreSlide);
        when(scoreSlideAssembler.toDTO(scoreSlide)).thenReturn(scoreSlideDTO);

        mockMvc.perform(post("/scores/slide")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scoreSlideDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Score Slide"));
    }

    @Test
    void testGetScoresByCategoryNotFound() throws Exception {
        when(scoreService.getScoreDTOsWithHighlight(999L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/scores/category/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testAddScoreWithNegativePoints() throws Exception {
        ScoreDTO scoreWithNegativePoints = new ScoreDTO();
        scoreWithNegativePoints.setPoints(-50.0);
        scoreWithNegativePoints.setTime(100);
        scoreWithNegativePoints.setTeam(teamDTO1);

        Score newScore = new Score();
        setId(newScore, 3L);
        newScore.setPoints(-50.0);
        newScore.setTime(100);
        newScore.setTeam(team1);

        when(scoreService.addScore(eq(1L), eq(-50.0), eq(100))).thenReturn(newScore);

        mockMvc.perform(post("/scores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scoreWithNegativePoints)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points").value(-50.0));
    }

    @Test
    void testUpdateScoreWithZeroTime() throws Exception {
        ScoreDTO scoreWithZeroTime = new ScoreDTO();
        scoreWithZeroTime.setPoints(100.0);
        scoreWithZeroTime.setTime(0);
        scoreWithZeroTime.setTeam(teamDTO1);

        Score updatedScore = new Score();
        setId(updatedScore, 1L);
        updatedScore.setPoints(100.0);
        updatedScore.setTime(0);
        updatedScore.setTeam(team1);

        when(scoreService.updateScore(eq(1L), eq(1L), eq(100.0), eq(0))).thenReturn(updatedScore);

        mockMvc.perform(put("/scores/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scoreWithZeroTime)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.time").value(0));
    }
} 
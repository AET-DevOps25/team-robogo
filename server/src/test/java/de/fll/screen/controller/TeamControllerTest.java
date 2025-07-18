package de.fll.screen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fll.core.dto.TeamDTO;
import de.fll.screen.assembler.TeamAssembler;
import de.fll.screen.model.Team;
import de.fll.screen.service.TeamService;
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

@WebMvcTest(
    controllers = {TeamController.class, GlobalExceptionHandler.class},
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
    }
)
public class TeamControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamService teamService;

    @MockBean
    private TeamAssembler teamAssembler;

    @Autowired
    private ObjectMapper objectMapper;

    private Team team1;
    private Team team2;
    private TeamDTO teamDTO1;
    private TeamDTO teamDTO2;

    @BeforeEach
    void setUp() {
        team1 = new Team();
        setId(team1, 1L);
        team1.setName("Test Team 1");

        team2 = new Team();
        setId(team2, 2L);
        team2.setName("Test Team 2");

        teamDTO1 = new TeamDTO();
        teamDTO1.setId(1L);
        teamDTO1.setName("Test Team 1");

        teamDTO2 = new TeamDTO();
        teamDTO2.setId(2L);
        teamDTO2.setName("Test Team 2");
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
    void testGetAllTeams() throws Exception {
        List<Team> teams = Arrays.asList(team1, team2);
        when(teamService.getAllTeams()).thenReturn(teams);
        when(teamAssembler.toDTO(team1)).thenReturn(teamDTO1);
        when(teamAssembler.toDTO(team2)).thenReturn(teamDTO2);

        mockMvc.perform(get("/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Team 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Test Team 2"));
    }

    @Test
    void testGetTeamById() throws Exception {
        when(teamService.getTeamById(1L)).thenReturn(team1);
        when(teamAssembler.toDTO(team1)).thenReturn(teamDTO1);

        mockMvc.perform(get("/teams/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Team 1"));
    }

    @Test
    void testCreateTeam() throws Exception {
        Team newTeam = new Team();
        newTeam.setName("New Team");
        setId(newTeam, 3L);

        TeamDTO newTeamDTO = new TeamDTO();
        newTeamDTO.setId(3L);
        newTeamDTO.setName("New Team");

        when(teamService.createTeam(any(Team.class))).thenReturn(newTeam);
        when(teamAssembler.toDTO(newTeam)).thenReturn(newTeamDTO);

        mockMvc.perform(post("/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTeamDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("New Team"));
    }

    @Test
    void testUpdateTeam() throws Exception {
        Team updatedTeam = new Team();
        updatedTeam.setName("Updated Team");
        setId(updatedTeam, 1L);

        TeamDTO updatedTeamDTO = new TeamDTO();
        updatedTeamDTO.setId(1L);
        updatedTeamDTO.setName("Updated Team");

        when(teamService.updateTeam(eq(1L), any(Team.class))).thenReturn(updatedTeam);
        when(teamAssembler.toDTO(updatedTeam)).thenReturn(updatedTeamDTO);

        mockMvc.perform(put("/teams/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTeamDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Team"));
    }

    @Test
    void testUpdateTeamCategory() throws Exception {
        Team teamWithCategory = new Team();
        teamWithCategory.setName("Team with Category");
        setId(teamWithCategory, 1L);

        TeamDTO teamWithCategoryDTO = new TeamDTO();
        teamWithCategoryDTO.setId(1L);
        teamWithCategoryDTO.setName("Team with Category");

        when(teamService.updateTeamCategory(eq(1L), eq(2L))).thenReturn(teamWithCategory);
        when(teamAssembler.toDTO(teamWithCategory)).thenReturn(teamWithCategoryDTO);

        mockMvc.perform(put("/teams/1/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content("2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Team with Category"));
    }

    @Test
    void testDeleteTeam() throws Exception {
        mockMvc.perform(delete("/teams/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTeamByIdNotFound() throws Exception {
        when(teamService.getTeamById(999L)).thenThrow(new RuntimeException("Team not found"));

        mockMvc.perform(get("/teams/999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testCreateTeamWithEmptyName() throws Exception {
        TeamDTO emptyNameDTO = new TeamDTO();
        emptyNameDTO.setName("");

        mockMvc.perform(post("/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyNameDTO)))
                .andExpect(status().isOk()); // 根据实际业务逻辑调整期望状态
    }

    @Test
    void testUpdateTeamWithEmptyName() throws Exception {
        TeamDTO emptyNameDTO = new TeamDTO();
        emptyNameDTO.setName("");

        Team updatedTeam = new Team();
        updatedTeam.setName("");
        setId(updatedTeam, 1L);

        when(teamService.updateTeam(eq(1L), any(Team.class))).thenReturn(updatedTeam);
        when(teamAssembler.toDTO(updatedTeam)).thenReturn(emptyNameDTO);

        mockMvc.perform(put("/teams/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyNameDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateTeamCategoryWithInvalidId() throws Exception {
        when(teamService.updateTeamCategory(eq(1L), eq(999L))).thenThrow(new RuntimeException("Category not found"));

        mockMvc.perform(put("/teams/1/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content("999"))
                .andExpect(status().isInternalServerError());
    }
} 
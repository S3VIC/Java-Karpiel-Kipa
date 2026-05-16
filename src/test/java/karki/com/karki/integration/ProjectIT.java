package karki.com.karki.integration;

import karki.com.karki.entity.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectIT extends IntegrationTestBase {

    @Test
    @DisplayName("POST /api/projects creates and persists a project")
    void createProject_persistsProject() throws Exception {
        String body = "{\"name\":\"Apollo\",\"description\":\"moon mission\"}";

        String response = mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Apollo"))
                .andExpect(jsonPath("$.description").value("moon mission"))
                .andReturn().getResponse().getContentAsString();

        Project created = objectMapper.readValue(response, Project.class);
        assertEquals("Apollo", projectRepository.findById(created.getId()).orElseThrow().getName());
    }

    @Test
    @DisplayName("GET /api/projects returns every persisted project")
    void getAllProjects_returnsAll() throws Exception {
        persistProject("Mercury", "first");
        persistProject("Gemini", "second");

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET /api/projects/{id} returns the matching project")
    void getProjectById_returnsProject() throws Exception {
        Project saturn = persistProject("Saturn", "rockets");

        mockMvc.perform(get("/api/projects/{id}", saturn.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saturn.getId()))
                .andExpect(jsonPath("$.name").value("Saturn"));
    }

    @Test
    @DisplayName("GET /api/projects/{id} surfaces an error for unknown id")
    void getProjectById_unknownId_errors() {
        Exception ex = assertThrows(Exception.class,
                () -> mockMvc.perform(get("/api/projects/{id}", 9999L)));
        assertTrue(rootCauseMessage(ex).contains("Projekt nie został znaleziony"));
    }

    @Test
    @DisplayName("PUT /api/projects/{id} updates name and description")
    void updateProject_changesFields() throws Exception {
        Project p = persistProject("OldName", "old-desc");
        String body = "{\"name\":\"NewName\",\"description\":\"new-desc\"}";

        mockMvc.perform(put("/api/projects/{id}", p.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewName"))
                .andExpect(jsonPath("$.description").value("new-desc"));

        Project reloaded = projectRepository.findById(p.getId()).orElseThrow();
        assertEquals("NewName", reloaded.getName());
        assertEquals("new-desc", reloaded.getDescription());
    }

    @Test
    @DisplayName("PUT /api/projects/{id} errors for unknown id")
    void updateProject_unknownId_errors() {
        Exception ex = assertThrows(Exception.class, () ->
                mockMvc.perform(put("/api/projects/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"x\",\"description\":\"y\"}")));
        assertTrue(rootCauseMessage(ex).contains("Projekt nie został znaleziony"));
    }

    @Test
    @DisplayName("DELETE /api/projects/{id} removes the project")
    void deleteProject_removesProject() throws Exception {
        Project p = persistProject("ToDelete", "bye");

        mockMvc.perform(delete("/api/projects/{id}", p.getId()))
                .andExpect(status().isOk());

        assertFalse(projectRepository.existsById(p.getId()));
    }

    @Test
    @DisplayName("DELETE /api/projects/{id} errors for unknown id")
    void deleteProject_unknownId_errors() {
        Exception ex = assertThrows(Exception.class,
                () -> mockMvc.perform(delete("/api/projects/{id}", 9999L)));
        assertTrue(rootCauseMessage(ex).contains("Projekt nie został znaleziony"));
    }

    private static String rootCauseMessage(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        return cur.getMessage() == null ? "" : cur.getMessage();
    }

    private Project persistProject(String name, String description) {
        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        return projectRepository.save(project);
    }
}

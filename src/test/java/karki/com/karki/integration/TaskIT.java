package karki.com.karki.integration;

import karki.com.karki.entity.Task;
import karki.com.karki.entity.TaskType;
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

class TaskIT extends IntegrationTestBase {

    @Test
    @DisplayName("POST /api/tasks creates and persists a task")
    void createTask_persistsTask() throws Exception {
        String body = "{\"title\":\"Wire login\",\"description\":\"add auth\",\"taskType\":\"FEATURE\"}";

        String response = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Wire login"))
                .andExpect(jsonPath("$.taskType").value("FEATURE"))
                .andReturn().getResponse().getContentAsString();

        Task created = objectMapper.readValue(response, Task.class);
        Task reloaded = taskRepository.findById(created.getId()).orElseThrow();
        assertEquals("Wire login", reloaded.getTitle());
        assertEquals(TaskType.FEATURE, reloaded.getTaskType());
    }

    @Test
    @DisplayName("GET /api/tasks returns every persisted task")
    void getAllTasks_returnsAll() throws Exception {
        persistTask("a", TaskType.BUG);
        persistTask("b", TaskType.IMPROVEMENT);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} returns the matching task")
    void getTaskById_returnsTask() throws Exception {
        Task t = persistTask("Investigate flake", TaskType.BUG);

        mockMvc.perform(get("/api/tasks/{id}", t.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(t.getId()))
                .andExpect(jsonPath("$.title").value("Investigate flake"))
                .andExpect(jsonPath("$.taskType").value("BUG"));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} surfaces an error for unknown id")
    void getTaskById_unknownId_errors() {
        Exception ex = assertThrows(Exception.class,
                () -> mockMvc.perform(get("/api/tasks/{id}", 9999L)));
        assertTrue(rootCauseMessage(ex).contains("Zadanie nie zostało znalezione"));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} updates fields")
    void updateTask_changesFields() throws Exception {
        Task t = persistTask("Old title", TaskType.BUG);
        String body = "{\"title\":\"New title\",\"description\":\"new\",\"taskType\":\"IMPROVEMENT\"}";

        mockMvc.perform(put("/api/tasks/{id}", t.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New title"))
                .andExpect(jsonPath("$.taskType").value("IMPROVEMENT"));

        Task reloaded = taskRepository.findById(t.getId()).orElseThrow();
        assertEquals("New title", reloaded.getTitle());
        assertEquals(TaskType.IMPROVEMENT, reloaded.getTaskType());
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} errors for unknown id")
    void updateTask_unknownId_errors() {
        Exception ex = assertThrows(Exception.class, () ->
                mockMvc.perform(put("/api/tasks/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"x\",\"description\":\"y\",\"taskType\":\"BUG\"}")));
        assertTrue(rootCauseMessage(ex).contains("Zadanie nie zostało znalezione"));
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} removes the task")
    void deleteTask_removesTask() throws Exception {
        Task t = persistTask("to-delete", TaskType.BUG);

        mockMvc.perform(delete("/api/tasks/{id}", t.getId()))
                .andExpect(status().isOk());

        assertFalse(taskRepository.existsById(t.getId()));
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} errors for unknown id")
    void deleteTask_unknownId_errors() {
        Exception ex = assertThrows(Exception.class,
                () -> mockMvc.perform(delete("/api/tasks/{id}", 9999L)));
        assertTrue(rootCauseMessage(ex).contains("Zadanie nie zostało znalezione"));
    }

    private static String rootCauseMessage(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        return cur.getMessage() == null ? "" : cur.getMessage();
    }

    private Task persistTask(String title, TaskType type) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription("desc");
        task.setTaskType(type);
        return taskRepository.save(task);
    }
}

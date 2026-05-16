package karki.com.karki.integration;

import karki.com.karki.entity.Project;
import karki.com.karki.entity.Task;
import karki.com.karki.entity.TaskType;
import karki.com.karki.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RelationsIT extends IntegrationTestBase {

    @Test
    @DisplayName("Project↔User M2M: assigning persists in both directions")
    @Transactional
    void projectUserRelation_isPersistedBothWays() {
        User alice = new User();
        alice.setUsername("alice");
        alice = userRepository.save(alice);

        Project apollo = new Project();
        apollo.setName("Apollo");
        apollo.setDescription("moon");
        apollo.getUsers().add(alice);
        apollo = projectRepository.saveAndFlush(apollo);
        Long apolloId = apollo.getId();
        Long aliceId = alice.getId();
        entityManager.clear();

        Project reloadedProject = projectRepository.findById(apolloId).orElseThrow();
        assertEquals(1, reloadedProject.getUsers().size());
        assertEquals("alice", reloadedProject.getUsers().iterator().next().getUsername());

        User reloadedUser = userRepository.findById(aliceId).orElseThrow();
        assertEquals(1, reloadedUser.getProjects().size());
        assertEquals("Apollo", reloadedUser.getProjects().iterator().next().getName());
    }

    @Test
    @DisplayName("Task→Project + Task→User: assigning via POST persists FKs")
    void task_canReferenceProjectAndUser() throws Exception {
        User bob = new User();
        bob.setUsername("bob");
        bob = userRepository.save(bob);

        Project mercury = new Project();
        mercury.setName("Mercury");
        mercury.setDescription("first");
        mercury = projectRepository.save(mercury);

        String body = "{"
                + "\"title\":\"Linked task\","
                + "\"description\":\"refs project+user\","
                + "\"taskType\":\"FEATURE\","
                + "\"project\":{\"id\":" + mercury.getId() + "},"
                + "\"user\":{\"id\":" + bob.getId() + "}"
                + "}";

        String response = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn().getResponse().getContentAsString();

        Task created = objectMapper.readValue(response, Task.class);
        Task reloaded = taskRepository.findById(created.getId()).orElseThrow();
        assertNotNull(reloaded.getProject());
        assertNotNull(reloaded.getUser());
        assertEquals(mercury.getId(), reloaded.getProject().getId());
        assertEquals(bob.getId(), reloaded.getUser().getId());
    }

    @Test
    @DisplayName("Deleting a project does not cascade to its users")
    @Transactional
    void deletingProject_doesNotCascadeToUsers() {
        User carol = new User();
        carol.setUsername("carol");
        carol = userRepository.save(carol);
        Long userId = carol.getId();

        Project gemini = new Project();
        gemini.setName("Gemini");
        gemini.setDescription("second");
        gemini.getUsers().add(carol);
        gemini = projectRepository.saveAndFlush(gemini);
        Long projectId = gemini.getId();
        entityManager.clear();

        Project managed = projectRepository.findById(projectId).orElseThrow();
        managed.getUsers().clear();
        projectRepository.saveAndFlush(managed);
        projectRepository.deleteById(projectId);
        entityManager.flush();

        assertTrue(userRepository.existsById(userId), "user should remain after project deletion");
    }

    @Test
    @DisplayName("Removing a user from a project clears the association without deleting the user")
    @Transactional
    void removingUserFromProject_clearsAssociationOnly() {
        User dave = new User();
        dave.setUsername("dave");
        dave = userRepository.save(dave);
        Long daveId = dave.getId();

        Project saturn = new Project();
        saturn.setName("Saturn");
        saturn.setDescription("rockets");
        saturn.getUsers().add(dave);
        saturn = projectRepository.saveAndFlush(saturn);
        Long saturnId = saturn.getId();
        entityManager.clear();

        Project managed = projectRepository.findById(saturnId).orElseThrow();
        managed.getUsers().clear();
        projectRepository.saveAndFlush(managed);
        entityManager.clear();

        Project reloaded = projectRepository.findById(saturnId).orElseThrow();
        assertEquals(0, reloaded.getUsers().size());
        assertTrue(userRepository.existsById(daveId));
    }

    @Test
    @DisplayName("Deleting a task does not delete its referenced project or user")
    void deletingTask_keepsRelatedRowsIntact() throws Exception {
        User eve = new User();
        eve.setUsername("eve");
        eve = userRepository.save(eve);

        Project gemini = new Project();
        gemini.setName("Gemini");
        gemini.setDescription("second");
        gemini = projectRepository.save(gemini);

        Task task = new Task();
        task.setTitle("temp");
        task.setDescription("d");
        task.setTaskType(TaskType.BUG);
        task.setProject(gemini);
        task.setUser(eve);
        task = taskRepository.save(task);

        mockMvc.perform(delete("/api/tasks/{id}", task.getId()))
                .andExpect(status().isOk());

        assertTrue(projectRepository.existsById(gemini.getId()));
        assertTrue(userRepository.existsById(eve.getId()));
    }
}

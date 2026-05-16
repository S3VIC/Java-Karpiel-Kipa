package karki.com.karki.integration;

import karki.com.karki.entity.User;
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

class UserIT extends IntegrationTestBase {

    @Test
    @DisplayName("POST /api/users creates a user and persists it")
    void createUser_persistsUser() throws Exception {
        String body = "{\"username\":\"alice\"}";

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("alice"))
                .andReturn().getResponse().getContentAsString();

        User created = objectMapper.readValue(response, User.class);
        assertEquals("alice", userRepository.findById(created.getId()).orElseThrow().getUsername());
    }

    @Test
    @DisplayName("GET /api/users returns every persisted user")
    void getAllUsers_returnsAll() throws Exception {
        persistUser("bob");
        persistUser("carol");

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET /api/users/{id} returns the matching user")
    void getUserById_returnsUser() throws Exception {
        User dave = persistUser("dave");

        mockMvc.perform(get("/api/users/{id}", dave.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dave.getId()))
                .andExpect(jsonPath("$.username").value("dave"));
    }

    @Test
    @DisplayName("GET /api/users/{id} surfaces an error for unknown id")
    void getUserById_unknownId_errors() {
        Exception ex = assertThrows(Exception.class,
                () -> mockMvc.perform(get("/api/users/{id}", 9999L)));
        assertTrue(rootCauseMessage(ex).contains("Użytkownik nie został znaleziony"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} updates the username")
    void updateUser_changesUsername() throws Exception {
        User eve = persistUser("eve");
        String body = "{\"username\":\"eve-renamed\"}";

        mockMvc.perform(put("/api/users/{id}", eve.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("eve-renamed"));

        assertEquals("eve-renamed", userRepository.findById(eve.getId()).orElseThrow().getUsername());
    }

    @Test
    @DisplayName("PUT /api/users/{id} errors for unknown id")
    void updateUser_unknownId_errors() {
        Exception ex = assertThrows(Exception.class, () ->
                mockMvc.perform(put("/api/users/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"ghost\"}")));
        assertTrue(rootCauseMessage(ex).contains("Użytkownik nie został znaleziony"));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} removes the user")
    void deleteUser_removesUser() throws Exception {
        User frank = persistUser("frank");

        mockMvc.perform(delete("/api/users/{id}", frank.getId()))
                .andExpect(status().isOk());

        assertFalse(userRepository.existsById(frank.getId()));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} errors for unknown id")
    void deleteUser_unknownId_errors() {
        Exception ex = assertThrows(Exception.class,
                () -> mockMvc.perform(delete("/api/users/{id}", 9999L)));
        assertTrue(rootCauseMessage(ex).contains("Użytkownik nie został znaleziony"));
    }

    private static String rootCauseMessage(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        return cur.getMessage() == null ? "" : cur.getMessage();
    }

    private User persistUser(String username) {
        User user = new User();
        user.setUsername(username);
        return userRepository.save(user);
    }
}

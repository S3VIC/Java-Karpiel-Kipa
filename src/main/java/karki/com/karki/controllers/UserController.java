package karki.com.karki.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import karki.com.karki.entity.User;
import karki.com.karki.repository.UserRepository;
import karki.com.karki.services.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Zarządzanie użytkownikami")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Pobierz wszystkich użytkowników", description = "Zwraca listę wszystkich użytkowników")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pobierz użytkownika o podanym id", description = "Zwraca użytkownika o podanym id lub tworzy " +
            "wyjątek jeśli nie został znaleziony")
    public User getUserById(@Parameter(description = "ID użytkownika do wyszukania") @PathVariable Long id) {
        return userService.findUserById(id);
    }

    @PostMapping
    @Operation(summary = "Utwórz nowego użytkownika", description = "Dodaje nowego użytkownika do bazy danych")
    public User createUser(@Parameter(description = "Dane nowego użytkownika") @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Zaktualizuj użytkownika", description = "Aktualizuje istniejącego użytkownika na podstawie jego ID")
    public User updateUser(
            @Parameter(description = "ID użytkownika do aktualizacji") @PathVariable Long id,
            @Parameter(description = "Nowe dane użytkownika") @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Usuń użytkownika", description = "Usuwa użytkownika na podstawie jego ID")
    public void deleteUser(@Parameter(description = "ID użytkownika do usunięcia") @PathVariable Long id) {
        userService.deleteUser(id);
    }
}

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

    @PostMapping
    @Operation(summary = "Utwórz nowego użytkownika", description = "Dodaje nowego użytkownika do bazy danych")
    public User createUser(@Parameter(description = "Dane nowego użytkownika") @RequestBody User user) {
        return userService.createUser(user);
    }
}

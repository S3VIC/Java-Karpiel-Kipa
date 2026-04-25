package karki.com.karki;

import karki.com.karki.entity.User;
import karki.com.karki.repository.UserRepository;
import karki.com.karki.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserServiceTests {
    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    @DisplayName("Should return all users")
    void testGetAllUsers() {
        User user1 = new User();
        user1.setUsername("TestUser1");
        User user2 = new User();
        user2.setUsername("TestUser2");

        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getAllUsers();

        Assertions.assertEquals(2, users.size());
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Should create a new user")
    void testCreateUser() {
        User user = new User();
        user.setUsername("NewUser");

        Mockito.when(userRepository.save(user)).thenReturn(user);

        User created = userService.createUser(user);

        Assertions.assertEquals("NewUser", created.getUsername());
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    @DisplayName("Should return user by id")
    void testFindUserById() {
        Long id = 1L;
        User user = new User();
        user.setId(id);
        user.setUsername("TestUser");

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User found = userService.findUserById(id);

        Assertions.assertEquals(user, found);
        Mockito.verify(userRepository, Mockito.times(1)).findById(id);
    }

    @Test
    @DisplayName("Should throw when user not found by id")
    void testFindUserByIdNotFound() {
        Long id = 99L;
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> userService.findUserById(id));
        Mockito.verify(userRepository, Mockito.times(1)).findById(id);
    }

    @Test
    @DisplayName("Should update existing user")
    void testUpdateUser() {
        Long id = 1L;
        User existing = new User();
        existing.setId(id);
        existing.setUsername("OldName");

        User update = new User();
        update.setUsername("NewName");

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(userRepository.save(existing)).thenReturn(existing);

        User result = userService.updateUser(id, update);

        Assertions.assertEquals("NewName", result.getUsername());
        Mockito.verify(userRepository, Mockito.times(1)).findById(id);
        Mockito.verify(userRepository, Mockito.times(1)).save(existing);
    }

    @Test
    @DisplayName("Should throw when updating non-existing user")
    void testUpdateUserNotFound() {
        Long id = 99L;
        User update = new User();
        update.setUsername("NewName");

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> userService.updateUser(id, update));
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Should delete existing user")
    void testDeleteUser() {
        Long id = 1L;
        Mockito.when(userRepository.existsById(id)).thenReturn(true);

        userService.deleteUser(id);

        Mockito.verify(userRepository, Mockito.times(1)).existsById(id);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Should throw when deleting non-existing user")
    void testDeleteUserNotFound() {
        Long id = 99L;
        Mockito.when(userRepository.existsById(id)).thenReturn(false);

        Assertions.assertThrows(RuntimeException.class, () -> userService.deleteUser(id));
        Mockito.verify(userRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }
}
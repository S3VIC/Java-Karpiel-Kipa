package karki.com.karki.services;

import karki.com.karki.entity.User;
import karki.com.karki.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, User user) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie został znaleziony: " + id));
        existing.setUsername(user.getUsername());
        return userRepository.save(existing);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Użytkownik nie został znaleziony: " + id);
        }
        userRepository.deleteById(id);
    }
}

package ru.cinemania.privileges.user;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserRepo userRepo;

    public UserController(UserRepo userRepository) {
        this.userRepo = userRepository;
    }

    @GetMapping("/watched-films")
    public List<String> getWatchedFilms(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        User user = userRepo.findById(Long.parseLong(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getWatchedFilms();
    }

}

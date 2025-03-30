package ru.cinemania.privileges.subscription;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.cinemania.privileges.user.User;
import ru.cinemania.privileges.user.UserRepo;

@RestController
@RequestMapping("api/v1/subscriptions")
public class SubscriptionController {

    private final UserRepo userRepo;

    public SubscriptionController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @PostMapping("/subscribe")
    public String subscribe(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        User user = userRepo.findById(Long.parseLong(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.extendSubscription(1); // Extend by 1 month
        userRepo.save(user);
        return "Subscription extended until " + user.getSubscriptionEndDate();
    }
}

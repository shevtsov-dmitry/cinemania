package ru.cinemania.privileges.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String googleId; // Google's unique user ID (sub)

    @Column(nullable = false)
    private String email;

    private String name;

    @ElementCollection
    @CollectionTable(name = "user_watched_shows", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "film_id")
    private List<String> watchedFilms = new ArrayList<>();

    private LocalDate subscriptionEndDate;

    public boolean isSubscriptionActive() {
        return subscriptionEndDate != null && LocalDate.now().isBefore(subscriptionEndDate);
    }

    public void extendSubscription(int months) {
        if (subscriptionEndDate == null || LocalDate.now().isAfter(subscriptionEndDate)) {
            subscriptionEndDate = LocalDate.now().plusMonths(months);
        } else {
            subscriptionEndDate = subscriptionEndDate.plusMonths(months);
        }
    }
}

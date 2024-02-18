package ru.filling_assistant.genre;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.filling_assistant.common.Nameable;

@Data
@Entity
@NoArgsConstructor
public class Genre implements Nameable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    public Genre(String name) {
        this.id = null;
        this.name = name;
    }

}

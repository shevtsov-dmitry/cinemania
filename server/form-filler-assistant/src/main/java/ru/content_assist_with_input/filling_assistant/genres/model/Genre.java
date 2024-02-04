package ru.content_assist_with_input.filling_assistant.genres.model;

import ru.content_assist_with_input.filling_assistant.common.AbstractEntity;
import ru.content_assist_with_input.filling_assistant.common.Nameable;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;

@Entity
public class Genre extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @NonNull
    @Column(unique = true)
    private String name;

    public Genre(@NonNull String name) {
        this.name = name;
    }

    public Genre() {
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Override
    public void setId() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}

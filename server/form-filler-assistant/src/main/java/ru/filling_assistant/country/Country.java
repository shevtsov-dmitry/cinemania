package ru.filling_assistant.country;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.filling_assistant.common.Nameable;

@Entity
@Data
@NoArgsConstructor
public class Country implements Nameable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    public Country(String name) {
        this.name = name;
    }

}

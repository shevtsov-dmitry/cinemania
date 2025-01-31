package ru.storage.content_creators;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.storage.content.ContentDetails;
import ru.storage.person.PersonCategory;
import ru.storage.userpic.UserPic;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class ContentCreator {
    @Id
    private String id;
    private String fullname;
    private String fullnameLatin;
    private String bornPlace;
    private Double heightMeters;
    private Integer age;
    private UserPic userPic;
    private PersonCategory personCategory;
    private List<ContentDetails> filmsParticipated;
    private Boolean isDead;
    @JsonFormat(pattern = "dd.MM.yyyy", locale = "RU_ru")
    private LocalDate birthDate;
    @JsonFormat(pattern = "dd.MM.yyyy", locale = "RU_ru")
    private LocalDate deathDate;
}

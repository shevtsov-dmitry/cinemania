package ru.storage.person.content_creator;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.storage.person.PersonCategory;
import ru.storage.person.userpic.UserPic;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class ContentCreator {
  @Id private String id;
  private String name;
  private String surname;
  private String nameLatin;
  private String surnameLatin;
  private String bornPlace;
  private Integer heightCm;
  private Integer age;
  private PersonCategory personCategory;
  @DBRef private UserPic userPic;
  private Boolean isDead;

  @JsonFormat(pattern = "dd.MM.yyyy")
  private LocalDate birthDate;

  @JsonFormat(pattern = "dd.MM.yyyy")
  private LocalDate deathDate;
}

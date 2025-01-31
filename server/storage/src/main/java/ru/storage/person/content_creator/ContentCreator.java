package ru.storage.person.content_creator;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.storage.person.userpic.UserPic;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class ContentCreator {
  @Id private String id;
  private String fullname;
  private String fullnameLatin;
  private String bornPlace;
  private Integer heightCm;
  private Integer age;
  private UserPic userPic;
  private Boolean isDead;

  @JsonFormat(pattern = "dd.MM.yyyy", locale = "RU_ru")
  private LocalDate birthDate;

  @JsonFormat(pattern = "dd.MM.yyyy", locale = "RU_ru")
  private LocalDate deathDate;
}

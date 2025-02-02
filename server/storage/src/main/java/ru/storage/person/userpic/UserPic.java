package ru.storage.person.userpic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.storage.person.PersonCategory;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPic {
  @Id private String id;
  private String contentType;
  private String filename;
  private Long size;
  private PersonCategory personCategory;

  public UserPic(String contentType, String filename, Long size, PersonCategory personCategory) {
    this.contentType = contentType;
    this.filename = filename;
    this.size = size; 
    this.personCategory = personCategory;
  }
}

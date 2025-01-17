package ru.storage.content_creators;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.storage.content.ContentDetails;
import ru.storage.userpic.UserPic;

@Data
@NoArgsConstructor
@Document
@AllArgsConstructor
public class ContentCreator {
    private String fullname;
    private String fullnameEng;
    private String bornPlace;
    private Double heightMeters;
    private UserPic userPic;
    private List<ContentDetails> filmsParticipated;
}

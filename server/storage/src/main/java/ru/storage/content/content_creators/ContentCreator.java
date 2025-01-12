package ru.storage.content.content_creators;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public abstract class ContentCreator {

    protected String fullname;
    protected String fullnameEng;
    protected String bornPlace;
    protected Double heightMeters;
    protected ContentCreatorKind contentCreatorKind;

}

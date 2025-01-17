package ru.storage.userpic;

/**
 * Enum defining the user type folder where user pictures are stored in S3 bucket (e.g. "director", "actor").
 */
public enum PicCategory {

    DIRECTOR("director"), 
    ACTOR("actor"),
    USER("user");

    public final String stringValue;

    PicCategory(String stringValue)  {
        this.stringValue = stringValue;
    }


}
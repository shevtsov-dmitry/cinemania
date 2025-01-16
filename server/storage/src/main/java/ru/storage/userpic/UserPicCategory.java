package ru.storage.userpic;

/**
 * Enum defining the user type folder where user pictures are stored in S3 bucket.
 */
public enum UserPicCategory {

    DIRECTOR("DIRECTOR"), 
    ACTOR("ACTOR"),
    USER("USER");

    public final String stringValue;

    UserPicCategory(String stringValue)  {
        this.stringValue = stringValue;
    }


}
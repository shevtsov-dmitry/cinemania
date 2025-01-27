package ru.storage.person;

/**
 * Enum defining the user type folder where user pictures are stored in S3 bucket (e.g. "director", "actor").
 */
public enum PersonCategory {

    DIRECTOR("director"), 
    ACTOR("actor"),
    USER("user");

    public final String stringValue;

    PersonCategory(String stringValue)  {
        this.stringValue = stringValue.toLowerCase();
    }

}
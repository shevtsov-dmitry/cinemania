package ru.content_assist_with_input.filling_assistant.common;

public abstract class AbstractEntity {
    public  abstract String getName();
    public abstract Long getId();

    public abstract void setName(String name);
    public abstract void setId();
}

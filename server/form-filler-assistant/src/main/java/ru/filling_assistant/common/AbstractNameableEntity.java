package ru.filling_assistant.common;

public abstract class AbstractNameableEntity {
    public  abstract String getName();
    public abstract Long getId();

    public abstract void setName(String name);
}

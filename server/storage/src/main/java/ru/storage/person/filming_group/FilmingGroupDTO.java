package ru.storage.person.filming_group;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class FilmingGroupDTO {

    @NotNull(message = "Идентификатор режиссера не может быть пустым")
    private String directorId;

    @NotNull(message = "Идентификаторы актеров не могут быть пустыми")
    @Size(min = 1, message = "Должен быть хотя бы один актер")
    private List<String> actorsIds;
}

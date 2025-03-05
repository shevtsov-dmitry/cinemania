package ru.storage.content_metadata;

import java.util.List;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.storage.content_metadata.common.MediaFileInfo;
import ru.storage.content_metadata.video.episode.Episode;
import ru.storage.person.filming_group.FilmingGroupDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentMetadataDTO {

    @Nullable
    private String id;
    @NotNull(message = "Название не может быть пустым")
    @Size(min = 1, max = 100, message = "Название должно быть от 1 до 100 символов")
    private String title;

    @NotNull(message = "Дата выпуска не может быть пустой")
    private String releaseDate;

    @NotNull(message = "Название страны не может быть пустым")
    private String countryName;

    @NotNull(message = "Основной жанр не может быть пустым")
    private String mainGenreName;

    private List<String> subGenresNames;

    @NotNull(message = "Описание не может быть пустым")
    private String description;

    @Min(value = 0, message = "Возраст не может быть отрицательным")
    private int age;

    @DecimalMin(value = "0.0", message = "Рейтинг не может быть отрицательным")
    @DecimalMax(value = "10.0", message = "Рейтинг не может быть больше 10")
    private double rating;

    private MediaFileInfo poster;

    private MediaFileInfo trailer;

    private MediaFileInfo standaloneVideoShow;

    private List<Episode> episodes;

    @NotNull(message = "Группа съемок не может быть пустой")
    private FilmingGroupDTO filmingGroupDTO;

}

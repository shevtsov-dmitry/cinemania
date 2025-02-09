package ru.storage.content_metadata.video;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document
public class TvSeries {

    @Id private String id;
    private List<Episode> episodes = new ArrayList<>();
}

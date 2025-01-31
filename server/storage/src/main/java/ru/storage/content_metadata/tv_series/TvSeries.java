package ru.storage.content_metadata.tv_series;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.storage.content_metadata.video.Video;

import java.util.List;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TvSeries {
  @Id private String id;
  private List<Video> video;
}

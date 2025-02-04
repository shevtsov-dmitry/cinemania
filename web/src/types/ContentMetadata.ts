import PersonCategory from "./PersonCategory";
import PosterMetadata from "./PosterMetadata";
import VideoMetadata from "./VideoMetadata";

type ContentMetadata = {
  id: string;
  title: string;
  releaseDate: LocalDate;
  country: Country;
  mainGenre: Genre;
  subGenres: List<Genre>;
  description: string;
  age: number;
  rating: double;
  poster: Poster;
  filmingGroup: FilmingGroup;
  singleVideoShow: StandaloneVideoShow;
  trailer: Trailer;
  standalone: StandaloneVideoShow;
  tvSeries: List<Episode>;
  createdAt: LocalDateTime;
};

export default ContentMetadata;

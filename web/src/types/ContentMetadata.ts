import FilmingGroup from "./FilmingGroup";
import Genre from "./Genre";
import Poster from "./Poster";
import StandaloneVideoShow from "./StandaloneVideoShow";
import Trailer from "./Trailer";
import TvSeries from "./TvSeries";

type ContentMetadata = {
  id: string;
  title: string;
  releaseDate: string;
  country: string;
  mainGenre: Genre;
  subGenres: Genre[];
  description: string;
  age: number;
  rating: number;
  poster: Poster;
  filmingGroup: FilmingGroup;
  singleVideoShow: StandaloneVideoShow;
  trailer: Trailer;
  standalone: StandaloneVideoShow;
  tvSeries: TvSeries;
};

export default ContentMetadata;

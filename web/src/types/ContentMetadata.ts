import Country from "./Country";
import FilmingGroup from "./FilmingGroup";
import Genre from "./Genre";
import Poster from "./Poster";
import StandaloneVideoShow from "./StandaloneVideoShow";
import Trailer from "./Trailer";
import TvSeries from "./TvSeries";

type ContentMetadata = {
  id?: string;
  title: string;
  releaseDate: string;
  country: Country;
  mainGenre: Genre;
  subGenres?: Genre[];
  description?: string;
  age: number;
  rating: number;
  filmingGroup: FilmingGroup;
  poster?: Poster;
  standaloneVideoShow?: StandaloneVideoShow;
  trailer?: Trailer;
  tvSeries?: TvSeries;
};

export default ContentMetadata;

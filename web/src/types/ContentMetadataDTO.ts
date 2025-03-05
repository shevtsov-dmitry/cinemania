import FilmingGroupDTO from "./FilmingGroupDTO";
import MediaFileInfo from "./MediaFileInfo";
import TvSeriesDTO from "./TvSeriesDTO";

type ContentMetadataDTO = {
  id?: string;
  title: string;
  releaseDate: string;
  countryName: string;
  mainGenreName: string;
  subGenres?: string[];
  description: string;
  age: number;
  rating?: number;
  poster?: MediaFileInfo;
  trailer?: MediaFileInfo;
  standaloneVideoShow?: MediaFileInfo;
  tvSeries?: TvSeriesDTO;
  filmingGroup: FilmingGroupDTO;
};

export default ContentMetadataDTO;

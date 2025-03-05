import FilmingGroupDTO from "./FilmingGroupDTO";
import MediaFileInfo from "./MediaFileInfo";

type ContentMetadataDTO = {
  id: string;
  title: string;
  releaseDate: string;
  countryName: string;
  mainGenreName: string;
  subGenres: string[];
  description: string;
  age: number;
  rating: number;
  poster: MediaFileInfo;
  trailer: MediaFileInfo;
  standaloneVideoShow: MediaFileInfo;
  tvSeries: Object;
  filmingGroup: FilmingGroupDTO;
};

export default ContentMetadataDTO;

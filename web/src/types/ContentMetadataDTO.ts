import Episode from "./Episode";
import FilmingGroupDTO from "./FilmingGroupDTO";
import MediaFileInfo from "./MediaFileInfo";

type ContentMetadataDTO = {
  id?: string;
  title: string;
  releaseDate: string;
  countryName: string;
  mainGenreName: string;
  subGenresNames?: string[];
  description: string;
  age: number;
  rating?: number;
  poster?: MediaFileInfo;
  trailer?: MediaFileInfo;
  standaloneVideoShow?: MediaFileInfo;
  episodes: Episode[];
  filmingGroupDTO: FilmingGroupDTO;
};

export default ContentMetadataDTO;

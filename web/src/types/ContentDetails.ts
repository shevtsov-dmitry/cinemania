import PersonCategory from "./PersonCategory";
import PosterMetadata from "./PosterMetadata";
import VideoMetadata from "./VideoMetadata";

type ContentDetails = {
  id: string;
  title: string;
  releaseDate: Date;
  country: string;
  mainGenre: string;
  subGenres: string[] | null;
  personCategory: PersonCategory;
  age: number;
  rating: number;
  posterMetadata: PosterMetadata | null;
  videoMetadata: VideoMetadata | null;
};

export default ContentDetails;

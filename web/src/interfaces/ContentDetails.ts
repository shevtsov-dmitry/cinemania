interface ContentDetails {
  id: string;
  title: string;
  releaseDate: Date;
  country: string;
  mainGenre: string;
  subGenres: string[];
  age: number;
  rating: number;
  posterMetadata: VideoMetadata;
  videoMetadata: VideoMetadata;
}

export default ContentDetails
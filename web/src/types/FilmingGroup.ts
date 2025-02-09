import ContentCreator from "./ContentCreator";

type FilmingGroup = {
  id: string;
  director: ContentCreator;
  actors: ContentCreator[];
};

export default FilmingGroup;

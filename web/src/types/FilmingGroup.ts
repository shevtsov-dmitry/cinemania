import ContentCreator from './ContentCreator'

type FilmingGroup = {
    id?: string | null
    director: ContentCreator
    actors: ContentCreator[]
}

export default FilmingGroup

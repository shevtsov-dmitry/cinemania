import FilmingGroup from './FilmingGroup'
import Poster from './Poster'
import StandaloneVideoShow from './StandaloneVideoShow'
import Trailer from './Trailer'
import TvSeries from './TvSeries'

type ContentMetadata = {
    id?: string
    title: string
    releaseDate: string
    country: string
    mainGenre: string
    subGenres?: string[]
    description?: string
    age: number
    rating: number
    poster?: Poster
    filmingGroup: FilmingGroup
    standaloneVideoShow?: StandaloneVideoShow
    trailer?: Trailer
    tvSeries?: TvSeries
}

export default ContentMetadata

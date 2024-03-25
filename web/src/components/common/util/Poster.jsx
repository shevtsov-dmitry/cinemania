import { Link, Route, Routes } from 'react-router-dom'
import { setPlayerOpened, setVideoId } from '../../../store/videoPlayerSlice'
import { useDispatch, useSelector } from 'react-redux'

import { Preview } from '../../Main/Preview'
import {useState} from "react";

export class PosterClass {
    constructor(fetchedMap) {
        this._metadataId = fetchedMap.metadataId
        this._title = fetchedMap.title
        this._poster = `data:image/jpeg;base64,${fetchedMap.poster}`
        this._country = fetchedMap.country
        this._releaseDate = fetchedMap.releaseDate
        this._genre = fetchedMap.genre
        this._rating = fetchedMap.rating
        this._videoId = fetchedMap.videoId
        this._age = fetchedMap.age
    }

    get metadataId() {
        return this._metadataId
    }

    get title() {
        return this._title
    }

    get poster() {
        return this._poster
    }

    get country() {
        return this._country
    }

    get age() {
        return this._age
    }

    get releaseDate() {
        return this._releaseDate
    }

    get genre() {
        return this._genre
    }

    get rating() {
        return this._rating
    }

    get videoId() {
        return this._videoId
    }
}

export function Poster(props) {

    const dispatch = useDispatch()
    const metadata = props.posterObject

    const [isPosterHovered, setIsPosterHovered] = useState(false)

    function InfoOnPosterHover() {
        return (
            <div className="absolute h-96 w-64 rounded-3xl bg-black p-4 opacity-70 content-['']">
                <h3 className="bg-inherit text-3xl font-bold text-white">
                    {metadata.title}
                </h3>
                <p className="select-none text-white">{metadata.age}</p>
                <p className="select-none text-white">{metadata.genre}</p>
                <p className="select-none text-white">{metadata.country}</p>
                <p className="select-none text-white">{metadata.releaseDate}</p>
            </div>
        )
    }

    return (
        <>
            <li
                key={metadata.metadataId}
                className={
                    'z-10 h-96 w-64 rounded-3xl bg-indigo-900 bg-cover bg-center hover:scale-105 hover:cursor-pointer'
                }
                style={{ backgroundImage: `url(${metadata.poster})` }}
                onMouseEnter={(ev) => {
                    setIsPosterHovered(true)
                }}
                onMouseLeave={(ev) => {
                    setIsPosterHovered(false)
                }}
            >
                <div className="postersInfo">
                    {isPosterHovered ? <InfoOnPosterHover /> : <div />}
                    <div className="flex w-64 justify-center">
                        <Link
                            className="absolute bottom-0 z-10  mb-10"
                            to="/watch"
                        >
                            <button
                                className="select-none rounded-3xl bg-pink-700 p-4 font-sans text-2xl font-bold text-white opacity-75 hover:opacity-95"
                                onClick={() => {
                                    dispatch(setPlayerOpened(true))
                                    dispatch(setVideoId(metadata.videoId))
                                }}
                            >
                                Смотреть
                            </button>
                        </Link>
                    </div>
                </div>
            </li>
        </>
    )
}

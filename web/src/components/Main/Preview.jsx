import { useEffect, useRef, useState } from 'react'
import { SideScrollArrow } from '../common/util/SideScrollArrow/SideScrollArrow'
import { Link, Route, Routes } from 'react-router-dom'
import { FilmPage } from '../FilmPage'
import { useDispatch, useSelector } from 'react-redux'
import {
    setPlayerOpened,
    setVideoId,
    videoPlayerSlice,
} from '../../store/videoPlayerSlice'
import Home from './Home'

export function Preview() {
    // *** EDGE SCREEN ARROWS
    const scrollableBlockRef = useRef()
    const rightArrowRef = useRef()
    const leftArrowRef = useRef()
    const arrowsHolder = useRef()

    const [isPostersBlockHovered, setIsPostersBlockHovered] = useState(false)

    const Arrow = new SideScrollArrow(scrollableBlockRef)
    const ARROW_SCROLL_DISTANCE = 800
    const scrollLeft = () => Arrow.scrollLeft(ARROW_SCROLL_DISTANCE)
    const scrollRight = () => Arrow.scrollRight(ARROW_SCROLL_DISTANCE)
    const hideArrowsLeaningScreen = () =>
        Arrow.hideArrowsLeaningScreen(leftArrowRef, rightArrowRef)
    const hideShowArrowsOnHover = () =>
        Arrow.hideShowArrowsOnHover(isPostersBlockHovered, arrowsHolder)

    useEffect(() => {
        if (!isPlayerOpened) {
            leftArrowRef.current.style.visibility = 'hidden'
        }
    }, [])

    useEffect(() => {
        hideShowArrowsOnHover()
    }, [isPostersBlockHovered])

    useEffect(() => {
        const blockElement = scrollableBlockRef.current

        if (blockElement) {
            blockElement.addEventListener('scroll', hideArrowsLeaningScreen)
        }

        return () => {
            if (blockElement) {
                blockElement.removeEventListener(
                    'scroll',
                    hideArrowsLeaningScreen
                )
            }
        }
    }, [])

    // *** PREVIEW PANEL
    const videoPlayerState = useSelector((state) => state.videoPlayer)
    let isPlayerOpened = videoPlayerState.isPlayerOpened
    const dispatch = useDispatch()

    const [isPosterHovered, setIsPosterHovered] = useState()
    const [metadataAndPosterBytesList, setMetadataAndPosterBytesList] =
        useState([])
    const [isPostersLoaded, setIsPostersLoaded] = useState(false)

    useEffect(() => {
        const fetchRecentlyAddedPosters = async () => {
            function parseMapsFromBase64(fetchedContentList) {
                const parsedMaps = []
                for (const elementMap of fetchedContentList) {
                    let map = {}
                    for (const k in elementMap) {
                        map = {
                            ...map,
                            k: atob(elementMap[k]),
                        }
                    }
                    parsedMaps.push(map)
                }
                return parsedMaps
            }

            try {
                const postersAmountToDisplay = 1
                const url = `${process.env.REACT_APP_SERVER_URL}:8080/posters/get/recent/${postersAmountToDisplay}`
                const response = await fetch(url)
                const fetchedContentList = await response.json()
                setIsPostersLoaded(true)
                setMetadataAndPosterBytesList(
                    parseMapsFromBase64(fetchedContentList)
                )
            } catch (error) {
                console.error('Error fetching data:', error)
            }
        }
        fetchRecentlyAddedPosters() // Call the function to fetch data on component mount
    }, [])

    function fillBlockWithPosters() {
        const posters = []
        for (const el of metadataAndPosterBytesList) {
            const poster = new Poster(el)
            posters.push(poster.DOM)
        }
        return posters
    }

    // TODO: refactor to mutual component
    class Poster {
        constructor(fetchedMap) {
            this._metadataId = fetchedMap.metadataId
            this._title = fetchedMap.title
            this._poster = fetchedMap.poster
            this._country = fetchedMap.country
            this._releaseDate = fetchedMap.releaseDate
            this._genre = fetchedMap.genre
            this._rating = fetchedMap.rating
            this._videoId = fetchedMap.videoId
            this._age = fetchedMap.age

            this._DOM = this.initDOM()
        }

        InfoOnPosterHover() {
            return (
                <div className="absolute h-96 w-64 rounded-3xl bg-black p-4 opacity-70 content-['']">
                    <h3 className="bg-inherit text-3xl font-bold text-white">
                        {this._title}
                    </h3>
                    <p className="select-none text-white">{this._age}</p>
                    <p className="select-none text-white">{this._genre}</p>
                    <p className="select-none text-white">{this._country}</p>
                    <p className="select-none text-white">
                        {this._releaseDate}
                    </p>
                </div>
            )
        }

        initDOM() {
            return (
                <li
                    key={this._metadataId}
                    className={
                        'z-10 h-96 w-64 rounded-3xl bg-indigo-900 bg-cover bg-center transition-all hover:scale-105 hover:cursor-pointer'
                    }
                    style={{ backgroundImage: `url(${this._poster})` }}
                    onMouseEnter={async (ev) => {
                        setIsPosterHovered(true)
                    }}
                    onMouseLeave={() => setIsPosterHovered(false)}
                >
                    {isPosterHovered ? (
                        <>
                            <div className="flex w-64 justify-center">
                                <Link
                                    className="absolute bottom-0 z-10  mb-10"
                                    to="/watch"
                                >
                                    <button
                                        className="select-none rounded-3xl bg-pink-700 p-4 font-sans text-2xl font-bold text-white opacity-75 hover:opacity-95"
                                        onClick={() => {
                                            dispatch(setPlayerOpened(true))
                                            dispatch(setVideoId())
                                        }}
                                    >
                                        Смотреть
                                    </button>
                                </Link>
                            </div>
                            {<InfoOnPosterHover />}
                        </>
                    ) : (
                        <div />
                    )}
                </li>
            )
        }

        get metadataId() {
            return this._metadataId
        }

        get DOM() {
            return this._DOM
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

    function RecentlyAddedPosters() {
        return (
            <>
                <h3 className={'p-2 text-2xl font-bold text-white'}>Новинки</h3>
                <div
                    id="previews-sequence-block"
                    onMouseEnter={() => setIsPostersBlockHovered(true)}
                    onMouseLeave={() => setIsPostersBlockHovered(false)}
                >
                    <div
                        ref={scrollableBlockRef}
                        className="no-scrollbar relative overflow-x-scroll scroll-smooth p-2"
                    >
                        <ul className="flex w-fit gap-4">
                            {isPostersLoaded ? (
                                fillBlockWithPosters()
                            ) : (
                                <p className="text-1xl font-bold text-white opacity-20">
                                    Постеры загружаются...
                                </p>
                            )}
                        </ul>
                    </div>
                    <div
                        ref={arrowsHolder}
                        className={`arrows-holder absolute -mt-[20.5rem] h-[20rem] w-dvw `}
                    >
                        <div
                            className="centered-arrow left-8"
                            onClick={scrollLeft}
                        >
                            <img
                                ref={leftArrowRef}
                                src={'icons/left-arrow.png'}
                                className={
                                    'arrow max-md:w-0 max-md:bg-transparent'
                                }
                                alt={'scroll left'}
                            />
                        </div>
                        <div
                            className="centered-arrow right-0"
                            onClick={scrollRight}
                        >
                            <img
                                ref={rightArrowRef}
                                src={'icons/right-arrow.png'}
                                className={
                                    'arrow max-md:w-0 max-md:bg-transparent'
                                }
                                alt={'scroll right'}
                            />
                        </div>
                    </div>
                </div>
            </>
        )
    }

    return (
        <>
            {isPlayerOpened ? <div /> : <RecentlyAddedPosters />}
            <Routes>
                <Route path="/watch" element={<FilmPage />} />
                {/*<Route*/}
                {/*    path="/"*/}
                {/*    element={<Home/>}*/}
                {/*/>*/}
            </Routes>
        </>
    )
}

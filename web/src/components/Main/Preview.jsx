import { useEffect, useRef, useState } from 'react'
import { SideScrollArrow } from '../common/util/SideScrollArrow/SideScrollArrow'
import { Link, Route, Routes } from 'react-router-dom'
import { FilmPage } from '../FilmPage'
import { useDispatch, useSelector } from "react-redux";
import { setPlayerOpened, setVideoId, videoPlayerSlice } from "../../store/videoPlayerSlice";

export function Preview() {
    // *** EDGE SCREEN ARROWS
    const scrollableBlockRef = useRef()
    const rightArrowRef = useRef()
    const leftArrowRef = useRef()
    const arrowsHolder = useRef()

    const [isBlockHovered, setIsBlockHovered] = useState(false)

    const Arrow = new SideScrollArrow(scrollableBlockRef)
    const ARROW_SCROLL_DISTANCE = 800
    const scrollLeft = () => Arrow.scrollLeft(ARROW_SCROLL_DISTANCE)
    const scrollRight = () => Arrow.scrollRight(ARROW_SCROLL_DISTANCE)
    const hideArrowsLeaningScreen = () =>
        Arrow.hideArrowsLeaningScreen(leftArrowRef, rightArrowRef)
    const hideShowArrowsOnHover = () =>
        Arrow.hideShowArrowsOnHover(isBlockHovered, arrowsHolder)

    useEffect(() => {
        if (!isPlayerOpened) {
            leftArrowRef.current.style.visibility = 'hidden'
        }
    }, [])

    useEffect(() => {
        hideShowArrowsOnHover()
    }, [isBlockHovered])

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
    const videoPlayerState = useSelector(state => state.videoPlayer)
    let isPlayerOpened = videoPlayerState.isPlayerOpened
    const dispatch = useDispatch()

    const [idAndPosters, setIdAndPosters] = useState([])
    const [isPostersLoaded, setIsPostersLoaded] = useState(false)

    useEffect(() => {
        const fetchRecentlyAddedPosters = async () => {
            try {
                const postersToDisplay = 15
                const url =
                    'http://localhost:8080/posters/get/recent/' + postersToDisplay

                const response = await fetch(url)
                const json = await response.json()
                setIsPostersLoaded(true)
                const idAndPoster = []
                for (const element of json) {
                    const metadataId = element[0]
                    const imageBytes = element[1]
                    const poster = `data:image/jpeg;base64,${imageBytes}`
                    idAndPoster.push([metadataId, poster])
                }
                setIdAndPosters(idAndPoster)
            } catch (error) {
                console.error('Error fetching data:', error)
            }
        }
        fetchRecentlyAddedPosters() // Call the function to fetch data on component mount
    }, [])

    function fillBlockWithPosters() {
        const content = []
        for (const el of idAndPosters) {
            const id = atob(el[0])
            const imageBytes = el[1]
            const poster = new Poster(id, imageBytes)
            content.push(poster.DOM)
        }
        return content
    }

    const [isPosterHovered, setIsPosterHovered] = useState(false)
    const [metadataOnPoster, setMetadataOnPoster] = useState({})

    class Poster {
        constructor(metadataId, poster) {
            this._metadataId = metadataId
            this._poster = poster
            this._DOM = this.initDOM()
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
                        const url = 'http://localhost:8080/posters/get/metadata/byId/' + this._metadataId
                        const res = await fetch(url)
                        const json = await res.json()
                        setMetadataOnPoster(json)
                    }}
                    onMouseLeave={() => setIsPosterHovered(false)}
                >
                    {isPosterHovered &&
                        this._metadataId === metadataOnPoster.id ? (
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
                                        }}>
                                        Смотреть
                                    </button>
                                </Link>
                            </div>
                            {showInfoOnPosterHover()}
                        </>
                    ) : (
                        <div />
                    )}
                </li>
            )
        }

        get metadataId() {
            return this._id
        }

        get DOM() {
            return this._DOM
        }
    }

    function showInfoOnPosterHover() {
        const json = metadataOnPoster
        return (
            <div className="absolute h-96 w-64 rounded-3xl bg-black p-4 opacity-70 content-['']">
                <h3 className="bg-inherit text-3xl font-bold text-white">
                    {json.title}
                </h3>
                <p className="select-none text-white">{json.age}</p>
                <p className="select-none text-white">{json.genre}</p>
                <p className="select-none text-white">{json.country}</p>
                <p className="select-none text-white">{json.releaseDate}</p>
            </div>
        )
    }

    function showPreview() {
        return (
            <>
                <h3 className={'p-2 text-2xl font-bold text-white'}>Новинки</h3>
                <div
                    id="previews-sequence-block"
                    onMouseEnter={() =>
                        setIsBlockHovered(true)
                    }
                    onMouseLeave={() => {
                        setIsBlockHovered(false)
                    }}
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
            {isPlayerOpened ? <div /> : showPreview()}
            <Routes>
                <Route
                    path="/watch"
                    element={<FilmPage videoId={metadataOnPoster.videoId} />}
                ></Route>
            </Routes>
        </>
    )
}

import { useEffect, useRef, useState } from 'react'
import { SideScrollArrow } from '../common/util/SideScrollArrow'
import { Route, Routes } from 'react-router-dom'
import { FilmPage } from '../FilmPage'
import { useSelector } from 'react-redux'
import { Poster, PosterClass } from '../common/util/Poster'
import Home from './Home'

export function Preview() {
    // *** EDGE SCREEN ARROWS
    const scrollableBlockRef = useRef()
    const rightArrowRef = useRef()
    const leftArrowRef = useRef()
    const arrowsHolder = useRef()

    const [isPostersBlockHovered, setIsPostersBlockHovered] = useState(false)
    const [posters, setPosters] = useState([])

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

    const [isPostersLoaded, setIsPostersLoaded] = useState(false)

    useEffect(() => {
        async function constructPosters() {
            const postersAmountToDisplay = 3
            const url = `${process.env.REACT_APP_SERVER_URL}:8080/posters/get/recent/${postersAmountToDisplay}`
            const response = await fetch(url)
            const fetchedMaps = await response.json()

            const posterList = []
            for (const map of fetchedMaps) {
                for (const k in map) {
                    if (k === 'poster') continue
                    map[k] = atob(map[k])
                }
                const poster = new PosterClass(map)
                posterList.push(<Poster posterObject={poster}/>)
            }
            setPosters(posterList)
            setIsPostersLoaded(true)
        }

        constructPosters()
    })

    return (
        <>
            {isPlayerOpened ? (
                <div />
            ) : (
                <>
                    <h3 className={'p-2 text-2xl font-bold text-white'}>
                        Новинки
                    </h3>
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
                                    posters
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
            )}
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

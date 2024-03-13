import {useEffect, useRef, useState} from 'react'
import {SideScrollArrow} from '../common/util/SideScrollArrow/SideScrollArrow'

export function Preview() {
    const OPTIONS = {
        posters_displayed: 15,
    }

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
        leftArrowRef.current.style.visibility = 'hidden'
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

    const [idAndPosters, setIdAndPosters] = useState([])

    useEffect(() => {
        const fetchData = async () => {
            try {
                const url = "http://localhost:8080/posters/get/recent/" + OPTIONS.posters_displayed;
                const response = await fetch(url);
                const json = await response.json();
                const idAndPoster = []
                for (const element of json) {
                    const metadataId = element[0]
                    const imageBytes = element[1]
                    const poster = `data:image/jpeg;base64,${imageBytes}`;
                    idAndPoster.push([metadataId, poster])
                }
                setIdAndPosters(idAndPoster)
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        };
        fetchData(); // Call the function to fetch data on component mount
    }, []);


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
    const hoveredPosterPopup = useRef()

    useEffect(() => {
        console.log(metadataOnPoster)

    }, [metadataOnPoster]);

    class Poster {
        constructor(metadataId, poster) {
            this._metadataId = metadataId;
            this._poster = poster;
            this._DOM = this.initDOM()
        }

        initDOM() {
            return <li
                key={this._metadataId}
                className={
                    "z-10 h-96 w-64 rounded-3xl bg-indigo-900 transition-all hover:scale-105 hover:cursor-pointer bg-cover bg-center"
                }
                style={{backgroundImage: `url(${this._poster})`}}
                onMouseEnter={async (ev) => {
                    setIsPosterHovered(true)
                    const url = "http://localhost:8080/posters/get/metadata/byId/" + this._metadataId
                    const res = await fetch(url)
                    const json = await res.json()
                    setMetadataOnPoster(json)
                }}
                onMouseLeave={() => setIsPosterHovered(false)}
            >
                {isPosterHovered && this._metadataId == metadataOnPoster.id ? showInfoOnPosterHover() : <div/>}
            </li>
        }

        get metadataId() {
            return this._id;
        }

        get DOM() {
            return this._DOM;
        }

    }
    function showInfoOnPosterHover() {
        const json = metadataOnPoster
        return <div className="absolute content-[''] h-96 w-64 rounded-3xl bg-black opacity-70 p-4">
            <h3 className="bg-inherit text-white text-3xl font-bold">{json.title}</h3>
            <p className="text-white select-none">{json.age}</p>
            <p className="text-white select-none">{json.genre}</p>
            <p className="text-white select-none">{json.country}</p>
            <p className="text-white select-none">{json.releaseDate}</p>
        </div>;
    }

    return (
        <>
            <h3 className={'p-2 text-2xl font-bold text-white'}>Новинки</h3>
            <div
                id="previews-sequence-block"
                onMouseEnter={() => {
                    setIsBlockHovered(true)
                }}
                onMouseLeave={() => {
                    setIsBlockHovered(false)
                }}
            >
                <div
                    ref={scrollableBlockRef}
                    className="no-scrollbar relative overflow-x-scroll scroll-smooth p-2"
                >
                    <ul className="flex w-fit gap-4">
                        {fillBlockWithPosters()}
                    </ul>
                </div>
                <div
                    ref={arrowsHolder}
                    className={`arrows-holder w-dvw absolute -mt-[20.5rem] h-[20rem] `}
                >
                    <div className="centered-arrow left-8" onClick={scrollLeft}>
                        <img
                            ref={leftArrowRef}
                            src={'icons/left-arrow.png'}
                            className={'arrow max-md:w-0 max-md:bg-transparent'}
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
                            className={'arrow max-md:w-0 max-md:bg-transparent'}
                            alt={'scroll right'}
                        />
                    </div>
                </div>
            </div>

        </>
    )
}


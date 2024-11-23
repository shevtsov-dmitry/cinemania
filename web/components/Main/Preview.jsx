import { useEffect, useRef, useState } from 'react'
import { useSelector } from 'react-redux'
import { Poster, PosterClass } from './Poster'
import { SideArrows } from '../common/util/SideArrows'

export function Preview() {
    const [posters, setPosters] = useState([])

    // const [isPostersBlockHovered, setIsPostersBlockHovered] = useState(false)
    const scrollableDivRef = useRef()
    const postersLoadingTextRef = useRef()

    // *** PREVIEW PANEL
    const videoPlayerState = useSelector((state) => state.videoPlayer)
    let isPlayerOpened = videoPlayerState.isPlayerOpened // ?

    const [isPostersLoaded, setIsPostersLoaded] = useState(false)
    const nonAsciiPattern = /[^\u0000-\u007F]/

    useEffect(() => {
        async function constructPosters() {
            const postersAmountToDisplay = 20
            const url = `${process.env.REACT_APP_SERVER_URL}:8080/posters/get/recent/${postersAmountToDisplay}`
            const response = await fetch(url).catch(() => {
                console.error(
                    `can't fetch ${process.env.REACT_APP_SERVER_URL}:8080/posters/get/recent/${postersAmountToDisplay}`
                )
                postersLoadingTextRef.current.textContent =
                    'Постерам не удалось загрузиться. Сервер не доступен.'
            })
            if (response === undefined) return
            const fetchedMaps = await response.json().catch(() => {
                console.error(
                    `couldn't parse JSON from /posters/get/recent/ request`
                )
                postersLoadingTextRef.current.textContent =
                    'Постеры не удалось преобразовать.'
                return
            })
            setPosters(collectPosterComponents(fetchedMaps))
            setIsPostersLoaded(true)
        }

        function collectPosterComponents(fetchedMaps) {
            const posterList = new Array(fetchedMaps.length)
            for (const map of fetchedMaps) {
                for (const k in map) {
                    if (k === 'poster') continue
                    let parsedBase64 = atob(map[k])
                    if (nonAsciiPattern.test(parsedBase64)) {
                        parsedBase64 = base64ToUtf8(parsedBase64)
                    }
                    map[k] = parsedBase64
                }
                const poster = new PosterClass(map)
                posterList.push(
                    <Poster key={poster.metadataId} posterObject={poster} />
                )
            }
            return posterList
        }

        function base64ToUtf8(ASCII_parsed_text) {
            const bytes = new Uint8Array(ASCII_parsed_text.length)
            for (let i = 0; i < ASCII_parsed_text.length; i++) {
                bytes[i] = ASCII_parsed_text.charCodeAt(i)
            }
            return new TextDecoder('utf-8').decode(bytes)
        }

        constructPosters()
    }, [])

    return (
        <>
            <div
                id="previews-sequence-block"
                className="flex flex-col justify-center"
                // onMouseEnter={() => setIsPostersBlockHovered(true)}
                // onMouseLeave={() => setIsPostersBlockHovered(false)}
            >
                <div
                    ref={scrollableDivRef}
                    className="no-scrollbar relative overflow-x-scroll scroll-smooth p-2"
                >
                    <ul className="flex w-fit gap-4">
                        {isPostersLoaded ? (
                            posters
                        ) : (
                            <p
                                className="text-1xl font-bold text-white opacity-20"
                                ref={postersLoadingTextRef}
                            >
                                Постеры загружаются...
                            </p>
                        )}
                    </ul>
                </div>

                <SideArrows scrollableDivRef={scrollableDivRef} />
            </div>
        </>
    )
}

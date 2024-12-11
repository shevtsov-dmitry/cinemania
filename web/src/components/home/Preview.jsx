import {useEffect, useRef, useState} from "react";
import {PosterClass} from "@/src/components/common/Poster";
import Poster from "@/src/components/common/Poster";

export default function Preview() {
    const STORAGE_URL = "http://localhost:8080";

    const [posters, setPosters] = useState([]);

    // const [isPostersBlockHovered, setIsPostersBlockHovered] = useState(false)
    const scrollableDivRef = useRef();
    const postersLoadingTextRef = useRef();

    // *** PREVIEW PANEL
    // const videoPlayerState = useSelector((state) => state.videoPlayer);
    // let isPlayerOpened = videoPlayerState.isPlayerOpened; // ?
    let isPlayerOpened = false;

    const [isPostersLoaded, setIsPostersLoaded] = useState(false);
    const nonAsciiPattern = /[^\u0000-\u007F]/;

    // TODO remake posters fetch
    // useEffect(() => {
    //     async function constructPosters() {
    //         const postersAmountToDisplay = 20;
    //         const url = `${STORAGE_URL}/posters/get/recent/${postersAmountToDisplay}`;
    //         const response = await fetch(url).catch(() => {
    //             console.error(`Не удалось получить ${postersAmountToDisplay} последних постеров из сервера для отображения на превью`);
    //             if (postersLoadingTextRef.current)
    //                 postersLoadingTextRef.current.textContent = "Постерам не удалось загрузиться. Сервер не доступен.";
    //         });
    //         if (response === undefined) return;
    //         const fetchedMaps = await response.json().catch((e) => {
    //             console.error("Постеры не удалось запарсить", e)
    //             if (postersLoadingTextRef.current)
    //                 postersLoadingTextRef.current.textContent = "Ошибка обработки постеров.";
    //             return;
    //         });
    //         setPosters(collectPosterComponents(fetchedMaps));
    //         setIsPostersLoaded(true);
    //     }
    //
    //     function collectPosterComponents(fetchedMaps) {
    //         const posterList = new Array(fetchedMaps.length);
    //         for (const map of fetchedMaps) {
    //             for (const k in map) {
    //                 if (k === "poster") continue;
    //                 let parsedBase64 = atob(map[k]);
    //                 if (nonAsciiPattern.test(parsedBase64)) {
    //                     parsedBase64 = base64ToUtf8(parsedBase64);
    //                 }
    //                 map[k] = parsedBase64;
    //             }
    //             const poster = new PosterClass(map);
    //             posterList.push(
    //                 <Poster key={poster.metadataId} posterObject={poster}/>,
    //             );
    //         }
    //         return posterList;
    //     }
    //
    //     function base64ToUtf8(ASCII_parsed_text) {
    //         const bytes = new Uint8Array(ASCII_parsed_text.length);
    //         for (let i = 0; i < ASCII_parsed_text.length; i++) {
    //             bytes[i] = ASCII_parsed_text.charCodeAt(i);
    //         }
    //         return new TextDecoder("utf-8").decode(bytes);
    //     }
    //
    //     constructPosters();
    // }, []);

    return (
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
            {/* TODO use scrollable div for mobile compatible devices */}
            {/* <SideArrows scrollableDivRef={scrollableDivRef} /> */}
        </div>
    );
}

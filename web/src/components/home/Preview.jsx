import {useEffect, useRef, useState} from "react";
import {ScrollView, Text, View} from "react-native-web";

import {PosterClass} from "@/src/components/poster/Poster";
import Poster from "@/src/components/poster/Poster";
import Constants from "@/src/constants/Constants";
import {ContentMetadata} from "@/src/types/ContentMetadata";
import PosterType from "@/src/components/poster/PosterType";
import {parsePathFromExpoGoLink} from "expo-router/build/fork/extractPathFromURL";

export default function Preview() {
    const STORAGE_URL = Constants.STORAGE_URL;
    const NON_ASCII_PATTERN = /[^\u0000-\u007F]/;
    const POSTERS_AMOUNT = 20;

    const [postersLoadingMessage, setPostersLoadingMessage] = useState("Постеры загружаются...");
    const [metadataList, setMetadataList] = useState([])
    const [postersBase64List, setPostersBase64List] = useState([]);

    const scrollableDivRef = useRef();
    // const videoPlayerState = useSelector((state) => state.videoPlayer);
    // let isPlayerOpened = videoPlayerState.isPlayerOpened; // ?


    useEffect(() => {
        fetchRecentlyAdded()

        async function fetchRecentlyAdded() {
            let res
            try {
                const url = `${STORAGE_URL}/api/v0/metadata/recent/${POSTERS_AMOUNT}`;
                res = await fetch(url);
                const fetchedMetadata = await res.json();
                setMetadataList(fetchedMetadata)
            } catch (e) {
                const errmes = decodeURI(res.headers.get("Message")).replaceAll("+", " ")
                console.error(errmes, e.message())
                setPostersLoadingMessage(errmes)
            }
        }

    }, [])

    useEffect(() => {
        fetchPosters()

        async function fetchPosters() {
            const joinedIds = metadataList.map(item => item.posterMetadata.id).join(",")
            let res
            try {
                res = await fetch(`${STORAGE_URL}/api/v0/posters/${joinedIds}`)
                const fetchedBase64Array = await res.json()
                setPostersBase64List(fetchedBase64Array)
            } catch (e) {
                const errmes = decodeURI(res.headers.get("Message")).replaceAll("+", " ")
                console.error(errmes, e.message())
                setPostersLoadingMessage(errmes)
            }
        }
    }, [metadataList]);

    /**
     *
     * @param {string} ASCII_parsed_text
     * @returns
     */
    // TODO figure out if i should use it to parse russian text to display it in posters info on hover
    function base64ToUtf8(ASCII_parsed_text) {
        const bytes = new Uint8Array(ASCII_parsed_text.length);
        for (let i = 0; i < ASCII_parsed_text.length; i++) {
            bytes[i] = ASCII_parsed_text.charCodeAt(i);
        }
        return new TextDecoder("utf-8").decode(bytes);
    }

    return (
        <View
            id="previews-sequence-block"
            className="flex flex-col justify-center"
        >
            <View
                ref={scrollableDivRef} className="no-scrollbar relative overflow-x-scroll scroll-smooth p-2">
                {metadataList.length !== 0 ?
                    (<ScrollView className="flex w-fit gap-4">
                        {metadataList.map((metadata, idx) =>
                            <Poster posterType={PosterType.PREVIEW}
                                    key={metadata.id}
                                    metadata={metadata}
                                    base64={postersBase64List[idx]}
                            />)}
                    </ScrollView>)
                    :
                    (<Text className={"text-1xl font-bold text-white opacity-20"}>
                        {postersLoadingMessage}
                    </Text>)
                }
            </View>
        </View>)
}

import { parseSplittedWithDefaultDelimiter } from '@/src/utils/BinaryContentUtils'
import { ReactElement, useEffect, useState } from 'react'
import { View } from 'react-native'
import ContentMetadata from '@/src/types/ContentMetadata'
import Compilation from '@/src/components/compilations/Compilation'
import Constants from '@/src/constants/Constants'

interface PreviewProps {}

const Preview = ({}: PreviewProps): ReactElement => {
    const STORAGE_URL = Constants.STORAGE_URL
    const NON_ASCII_PATTERN = /[^\u0000-\u007F]/
    const POSTERS_AMOUNT = 20

    const [postersLoadingMessage, setPostersLoadingMessage] =
        useState<string>('')
    const [metadataList, setMetadataList] = useState<ContentMetadata[]>([])
    const [posterImagesUrls, setPosterImagesUrls] = useState<string[]>([])

    // const videoPlayerState = useSelector((state) => state.videoPlayer);
    // let isPlayerOpened = videoPlayerState.isPlayerOpened; // ?

    useEffect(() => {
        fetchRecentlyAddedFilmsMetadata()

        async function fetchRecentlyAddedFilmsMetadata() {
            fetch(`${STORAGE_URL}/api/v0/metadata/recent/${POSTERS_AMOUNT}`)
                .then((res) => {
                    if (res.ok) {
                        return res.json()
                    } else {
                        let errmes =
                            res.headers.get('Message') ??
                            'Ошибка получения метаданных фильмов для превью.'
                        errmes = decodeURI(errmes).replaceAll('+', ' ')
                        console.error(errmes)
                        setPostersLoadingMessage(errmes)
                    }
                })
                .then((fetchedMetadata) => setMetadataList(fetchedMetadata))
                .catch((e) => {
                    setPostersLoadingMessage(
                        'Ошибка установления соединения с сервером для получения постеров.'
                    )
                    console.error(e.message)
                })
        }
    }, [])

    useEffect(() => {
        fetchPosters()

        async function fetchPosters() {
            if (metadataList.length === 0) return

            const joinedIds = metadataList
                .map((item: ContentMetadata) => item.poster?.id)
                .join(',')

            fetch(`${STORAGE_URL}/api/v0/posters/${joinedIds}`)
                .then((res) => {
                    if (res.ok) {
                        return res.blob()
                    } else {
                        const errmes =
                            decodeURI(
                                res.headers.get('Message') as string
                            ).replaceAll('+', ' ') ??
                            'Ошибка получения постеров.'
                        console.error(errmes)
                        setPostersLoadingMessage(errmes)
                    }
                })
                .then((binaryOctetStream: Blob | undefined) => {
                    if (binaryOctetStream) {
                        attemptToSetPostersUrls(binaryOctetStream)
                    }
                })
                .catch((err) => {
                    console.error(err)
                })
        }

        function attemptToSetPostersUrls(binaryOctetStream: Blob) {
            parseSplittedWithDefaultDelimiter(binaryOctetStream)
                .then((parsedPostersUrls) =>
                    setPosterImagesUrls(parsedPostersUrls)
                )
                .catch((err) => console.error(err))
        }
    }, [metadataList])

    // TODO figure out if i should use it to parse russian text to display it in posters info on hover
    function base64ToUtf8(ASCII_parsed_text: string) {
        const bytes = new Uint8Array(ASCII_parsed_text.length)
        for (let i = 0; i < ASCII_parsed_text.length; i++) {
            bytes[i] = ASCII_parsed_text.charCodeAt(i)
        }
        return new TextDecoder('utf-8').decode(bytes)
    }

    return (
        <View
            id="previews-sequence-block"
            className="flex flex-col justify-center"
        >
            <View className="no-scrollbar relative overflow-x-scroll scroll-smooth p-2">
                <Compilation
                    posterImageUrls={posterImagesUrls}
                    metadataList={metadataList}
                    errmes={postersLoadingMessage}
                />
            </View>
        </View>
    )
}

export default Preview

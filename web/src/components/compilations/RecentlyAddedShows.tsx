import LoadMoreBigButton from '@/src/components/common/LoadMoreBigButton'
import Compilation from '@/src/components/compilations/Compilation'
import CompilationKind from '@/src/components/compilations/CompilationKind'
import Constants from '@/src/constants/Constants'
import Base64WithId from '@/src/types/Base64WithId'
import ContentMetadata from '@/src/types/ContentMetadata'
import React, { ReactElement, useEffect, useState } from 'react'
import { View } from 'react-native'

interface RecentlyAddedShowsProps {}

const RecentlyAddedShows = ({}: RecentlyAddedShowsProps): ReactElement => {
    const STORAGE_URL = Constants.STORAGE_URL
    const NON_ASCII_PATTERN = /[^\u0000-\u007F]/
    const POSTERS_AMOUNT = 21

    const [postersLoadingMessage, setPostersLoadingMessage] =
        useState<string>('')
    const [metadataList, setMetadataList] = useState<ContentMetadata[]>([])
    const [posterImagesWithIds, setPosterImagesWithIds] = useState<
        Base64WithId[]
    >([])

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
                        return res.json()
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
                .then((json: Base64WithId[]) => setPosterImagesWithIds(json))
                .catch((err) => {
                    console.error(err)
                })
        }
    }, [metadataList])

    return (
        <View
            id="previews-sequence-block"
            className="flex flex-col justify-center"
        >
            <View
                className="no-scrollbar relative overflow-x-scroll scroll-smooth p-2"
                style={{
                    scrollbarWidth: 'none',
                    msOverflowStyle: 'none',
                }}
            >
                <Compilation
                    compilationKind={CompilationKind.GRID}
                    gridCols={7}
                    postersWithIds={posterImagesWithIds}
                    metadataList={metadataList}
                    errmes={postersLoadingMessage}
                />
                <LoadMoreBigButton />
            </View>
        </View>
    )
}

export default RecentlyAddedShows

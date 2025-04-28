import Compilation from '@/src/components/compilations/Compilation'
import Constants from '@/src/constants/Constants'
import Base64WithId from '@/src/types/Base64WithId'
import ContentMetadata from '@/src/types/ContentMetadata'
import { ReactElement, useEffect, useState } from 'react'
import { ScrollView, View } from 'react-native'
import CompilationKind from './CompilationKind'

interface CompilationByGenreProps {
    name: string
    compilationKind?: CompilationKind
}

const CompilationByGenre = ({
    name,
    compilationKind = CompilationKind.DEFAULT,
}: CompilationByGenreProps): ReactElement => {
    const STORAGE_URL = Constants.STORAGE_URL
    const POSTERS_AMOUNT = 16

    const [postersLoadingMessage, setPostersLoadingMessage] =
        useState<string>('')
    const [metadataList, setMetadataList] = useState<ContentMetadata[]>([])
    const [posterImagesWithIds, setPosterImagesWithIds] = useState<
        Base64WithId[]
    >([])

    useEffect(() => {
        fetchGenreMetadata()

        async function fetchGenreMetadata() {
            fetch(
                `${STORAGE_URL}/api/v0/metadata/genre/${encodeURIComponent(name)}/${POSTERS_AMOUNT}`
            )
                .then((res) => {
                    if (res.ok) {
                        return res.json()
                    } else {
                        let errmes =
                            res.headers.get('Message') ??
                            `Ошибка загрузки постеров "${name}".`
                        errmes = decodeURI(errmes).replaceAll('+', ' ')
                        console.error(errmes)
                        setPostersLoadingMessage(errmes)
                    }
                })
                .then((fetchedMetadata) => setMetadataList(fetchedMetadata))
                .catch((e) => {
                    setPostersLoadingMessage(
                        'Ошибка установления соединения с сервером.'
                    )
                    console.error(e.message)
                })
        }
    }, [name])

    useEffect(() => {
        fetchPosters()

        async function fetchPosters() {
            if (!metadataList || metadataList.length === 0) return

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
            id="genre-compilation-block"
            className="flex flex-col justify-center"
        >
            <ScrollView
                className="no-scrollbar relative overflow-x-scroll scroll-smooth p-2"
                style={{
                    scrollbarWidth: 'none',
                    msOverflowStyle: 'none',
                }}
            >
                <Compilation
                    compilationKind={compilationKind}
                    postersWithIds={posterImagesWithIds}
                    metadataList={metadataList}
                    errmes={postersLoadingMessage}
                />
            </ScrollView>
        </View>
    )
}

export default CompilationByGenre

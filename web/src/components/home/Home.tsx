import Footer from '@/src/components/footer/Footer'
import Colors from '@/src/constants/Colors'
import Constants from '@/src/constants/Constants'
import React, { ReactElement, useState } from 'react'
import { ScrollView, Text, View } from 'react-native'
import AdminPage from '../admin/AdminPage'
import CompilationByGenre from '../compilations/CompilationByGenre'
import CompilationKind from '../compilations/CompilationKind'
import RecentlyAddedShows from '../compilations/RecentlyAddedShows'
import TopPanel from '../top-panel/TopPanel'

let trailerUrl = `${Constants.STREAMING_SERVER_URL}/api/v1/stream/67d47489eeda036a76103a6e/playlist`

trailerUrl = `${Constants.STREAMING_SERVER_URL}/api/v1/stream/123/playlist`

trailerUrl =
    'https://cdn.flowplayer.com/a30bd6bc-f98b-47bc-abf5-97633d4faea0/hls/de3f6ca7-2db3-4689-8160-0f574a5996ad/playlist.m3u8'

const styles = {
    collectionTitle: 'p-2 text-2xl font-bold text-white',
}

const Home = (): ReactElement => {
    const [logMessage, setLogMessage] = useState<string>('empty')

    return (
        <ScrollView
            className="min-h-screen w-screen"
            style={{
                backgroundImage: Colors.TEAL_DARK_GRADIENT_BG_IMAGE,
            }}
        >
            {/*<MobileVideoPlayer url={trailerUrl} setLogMessage={setLogMessage}/>*/}
            {/* <WebVideoPlayer url={trailerUrl} /> */}
            <TopPanel />
            {/* <AuthScreen /> */}
            <AdminPage />
            <Text className={styles.collectionTitle}>В тренде</Text>
            <CompilationByGenre
                name="Популярное"
                compilationKind={CompilationKind.HORIZONTAL}
            />
            <Text className={styles.collectionTitle}>Отечественные</Text>
            <CompilationByGenre name="Отечественные" />
            <Text className={styles.collectionTitle}>Недавно добавленные</Text>
            <RecentlyAddedShows />
            <Text className={styles.collectionTitle}>Триллеры</Text>
            <CompilationByGenre name="Триллеры" />
            <Text className={styles.collectionTitle}>Мультфильмы</Text>
            <CompilationByGenre name="Мультфильмы" />
            <Text className={styles.collectionTitle}>Драмы</Text>
            <CompilationByGenre name="Драмы" />
            <Text className={styles.collectionTitle}>Фантастика</Text>
            <CompilationByGenre name="Фантастика" />
            <View className="my-5" />
            <Footer />
        </ScrollView>
    )
}

export default Home

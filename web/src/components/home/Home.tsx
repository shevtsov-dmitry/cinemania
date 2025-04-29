import Footer from '@/src/components/footer/Footer'
import Colors from '@/src/constants/Colors'
import React, { ReactElement } from 'react'
import { ScrollView, Text, View } from 'react-native'
import AdminPage from '../admin/AdminPage'
import CompilationByGenre from '../compilations/CompilationByGenre'
import CompilationKind from '../compilations/CompilationKind'
import RecentlyAddedShows from '../compilations/RecentlyAddedShows'
import TopPanel from '../top-panel/TopPanel'

const styles = {
    collectionTitle: 'p-2 text-2xl font-bold text-white',
}

const Home = (): ReactElement => {
    return (
        <ScrollView
            className="min-h-screen w-screen"
            style={{
                backgroundImage: Colors.TEAL_DARK_GRADIENT_BG_IMAGE,
            }}
        >
            {/*<MobileVideoPlayer url={trailerUrl} setLogMessage={setLogMessage}/>*/}
            {/* <WebVideoPlayer url={trailerUrl} /> */}
            <View className="my-2" />
            <TopPanel />
            {/* <AuthScreen /> */}
            <AdminPage />
            <View className="my-2" />
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

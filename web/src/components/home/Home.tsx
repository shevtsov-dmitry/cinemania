import Colors from '@/src/constants/Colors'
import React, { ReactElement, useEffect, useState } from 'react'
import { Text, View } from 'react-native'
import AdminPage from '../admin/AdminPage'
import TopPanel from '../top-panel/TopPanel'
import Preview from './Preview'
import { Image } from 'expo-image'
import { VideoView, useVideoPlayer } from 'expo-video'
import MobileVideoPlayer from '../video-player/MobileVideoPlayer'
import Constants from '@/src/constants/Constants'
import WebVideoPlayer from '../video-player/WebVideoPlayer'
import ContentMetadata from '@/src/types/ContentMetadata'
import AuthScreen from '../auth-screen/AuthScreen'

let trailerUrl = `${Constants.STREAMING_SERVER_URL}/api/v1/stream/67d47489eeda036a76103a6e/playlist`

trailerUrl = `${Constants.STREAMING_SERVER_URL}/api/v1/stream/123/playlist`

// trailerUrl = "https://cdn.flowplayer.com/a30bd6bc-f98b-47bc-abf5-97633d4faea0/hls/de3f6ca7-2db3-4689-8160-0f574a5996ad/playlist.m3u8"

const Home = (): ReactElement => {
    const [logMessage, setLogMessage] = useState<string>('empty')

    return (
        <View
            className="min-h-screen w-screen"
            style={{
                backgroundImage: Colors.TEAL_DARK_GRADIENT_BG_IMAGE,
            }}
        >
            {/*<MobileVideoPlayer url={trailerUrl} setLogMessage={setLogMessage}/>*/}
            {/*<Text className={"bg-amber-100 text-blue-900 text-3xl"}>*/}
            {/*    {logMessage}*/}
            {/*</Text>*/}
            {/* <WebVideoPlayer url={trailerUrl} /> */}
            <TopPanel />
            <AuthScreen />
            <AdminPage />
            {/* <Text className={"p-2 text-2xl font-bold text-white"}>Новинки</Text>*/}
            {/* <Preview />*/}
            {/* <Text className={"p-2 text-2xl font-bold text-white"}>*/}
            {/*   Вам может понравится*/}
            {/* </Text>*/}
        </View>
    )
}

export default Home

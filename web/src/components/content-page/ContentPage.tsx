import Colors from '@/src/constants/Colors'
import Constants from '@/src/constants/Constants'
import useContentPageState from '@/src/state/contentPageState'
import { Ionicons } from '@expo/vector-icons'
import { BlurView } from 'expo-blur'
import { useRouter } from 'expo-router'
import { VideoView } from 'expo-video'
import React, { ReactElement, useEffect, useRef, useState } from 'react'
import { Pressable, ScrollView, Text, View } from 'react-native'
import BackSign from '../common/BackSign'
import Poster from '../poster/Poster'
import WebVideoPlayer from '../video-player/WebVideoPlayer'

interface ContentPageProps {}

const ContentPage = ({}: ContentPageProps): ReactElement => {
    const [posterUrl, setPosterUrl] = useState<string>('')
    const { contentPageMetadata, posterBase64Object } = useContentPageState()
    const router = useRouter()

    const videoRef = useRef<VideoView>(null)

    useEffect(() => {
        fetchPoster()

        if (videoRef.current) {
            // VIDEO LOGS
            videoRef.current.setOnPlaybackStatusUpdate((status) => {
                // console.log('Video status:', status)
            })
        }
    }, [])

    const VideoNotSelectedWarningScreen = (): ReactElement => (
        <View
            className="flex h-screen w-screen items-center justify-center gap-5"
            style={{
                backgroundImage: Colors.TEAL_DARK_GRADIENT_BG_IMAGE,
            }}
        >
            <Text className="text-5xl font-bold text-white">
                Выбранный видеофайл не найден.
            </Text>
            <Pressable
                className="w-52 rounded-xl bg-cyan-700 p-5 shadow hover:bg-cyan-500"
                onPress={() => router.back()}
            >
                <Text className="text-center text-3xl font-bold text-white">
                    Вернуться
                </Text>
            </Pressable>
        </View>
    )

    async function fetchPoster() {
        const url =
            Constants.STORAGE_URL +
            `/api/v0/posters/${contentPageMetadata?.poster?.id}`
        const res = await fetch(url)
        const blob = await res.blob()
        setPosterUrl(URL.createObjectURL(blob))
    }

    if (!contentPageMetadata) {
        return <VideoNotSelectedWarningScreen />
    }

    const trailerUrl =
        Constants.STREAMING_SERVER_URL +
        `/api/v1/stream/trailer/${contentPageMetadata.trailer?.id}/playlist`

    const filmUrl =
        Constants.STREAMING_SERVER_URL +
        `/api/v1/stream/standalone/${contentPageMetadata.standaloneVideoShow?.id}/playlist`

    return (
        <View className="relative h-screen w-screen bg-black">
            <View className="absolute left-3 top-3 z-10">
                <BackSign />
            </View>

            <WebVideoPlayer variant="trailer" url={trailerUrl} />

            <BlurView
                intensity={50}
                tint="dark"
                className="absolute inset-0 bottom-0 ml-[2%] mt-[14%] flex h-[50%] w-[37%] items-start rounded-[10px] px-2 py-2"
            >
                <ScrollView className="flex w-full max-w-2xl">
                    <View className="flex-row items-center justify-center space-x-6">
                        <View className={'min-h-52 w-40 min-w-36 shadow-md'}>
                            <Poster
                                id={posterBase64Object?.id}
                                base64Image={posterBase64Object?.image!}
                            />
                        </View>

                        <View className="flex-1 justify-start space-y-3">
                            <Text className="text-5xl font-extrabold leading-tight text-white">
                                {contentPageMetadata.title}
                            </Text>

                            <Text className="text-base text-gray-300">
                                {contentPageMetadata.releaseDate} •{' '}
                                {contentPageMetadata.country.name} •{' '}
                                {contentPageMetadata.mainGenre.name} • Рейтинг:{' '}
                                {contentPageMetadata.rating} •{' '}
                                <Text className="font-bold">
                                    {contentPageMetadata.age !== 0
                                        ? contentPageMetadata.age
                                        : '16'}
                                    +
                                </Text>
                            </Text>

                            <Text className="text-base font-semibold text-gray-300">
                                🎬 Режиссёр:{' '}
                                <Text className="font-semibold text-gray-100">
                                    {
                                        contentPageMetadata.filmingGroup
                                            .director.name
                                    }{' '}
                                    {
                                        contentPageMetadata.filmingGroup
                                            .director.surname
                                    }
                                </Text>
                            </Text>

                            <Text className="text-base font-semibold text-gray-300">
                                ⭐ Актёры:{' '}
                                {contentPageMetadata.filmingGroup.actors
                                    .slice(0, 3)
                                    .map(
                                        (actor) =>
                                            `${actor.name} ${actor.surname}`
                                    )
                                    .join(', ')}
                                {/* {contentPageMetadata.filmingGroup.actors
                                    .length > 3
                                    ? '...'
                                    : ''} */}
                                , ...
                            </Text>
                        </View>
                    </View>

                    <View className="mt-6 space-y-4">
                        <Text className="text-justify text-lg leading-relaxed text-gray-300">
                            {contentPageMetadata.description}
                        </Text>

                        <View className="flex w-full items-center justify-center">
                            <Pressable
                                id="play-button"
                                className="mt-2 w-fit flex-row items-center rounded-full px-6 py-3"
                                style={{
                                    backgroundImage:
                                        'linear-gradient(90deg, #FF512F 0%, #DD2476 100%)',
                                }}
                                onPress={() => {
                                    router.push({
                                        pathname: '/fullscreen-video',
                                        params: { url: filmUrl },
                                    })
                                }}
                            >
                                <Ionicons
                                    name="play"
                                    size={24}
                                    color="white"
                                    className="mr-2"
                                />
                                <Text className="select-none text-xl font-bold text-white">
                                    Смотреть
                                </Text>
                            </Pressable>
                        </View>
                    </View>
                </ScrollView>
            </BlurView>
        </View>
    )
}

export default ContentPage

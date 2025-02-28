import { Link, Stack, useRouter } from 'expo-router'
import { ReactElement } from 'react'

import { Pressable, Text, View } from 'react-native'

const NotFoundScreen = (): ReactElement => {
    const router = useRouter()

    return (
        <View
            className="flex h-screen w-screen items-center justify-center"
            style={{
                backgroundImage:
                    'linear-gradient(to right bottom, #3c4d66, #333f53, #2a3141, #20242f, #16181f)',
            }}
        >
            <Stack.Screen options={{ title: 'Not Found.' }} />
            <View className="flex flex-1 items-center justify-center p-5 leading-7">
                <Text className="text-5xl font-bold text-white">
                    This page doesn't exist.
                </Text>
                <Text className="text-5xl font-bold text-white">
                    Страница не найдена.
                </Text>
                <Pressable onPress={() => router.push('/home')}>
                    <Text className="text-3xl text-link underline">
                        Go to home screen. {'\n'}
                    </Text>
                    <Text className="text-3xl text-link underline">
                        Перейти на главный экран.
                    </Text>
                </Pressable>
            </View>
        </View>
    )
}

export default NotFoundScreen

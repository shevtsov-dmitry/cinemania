import { Link, Stack } from 'expo-router'

import { Text, View } from 'react-native'

export default function NotFoundScreen() {
    return (
        <>
            <Stack.Screen options={{ title: 'Not Found.' }} />
            <View className="flex flex-1 items-center justify-center p-5">
                <Text className="text-xl font-bold">
                    This screen doesn't exist.
                </Text>
                <Text className="text-xl font-bold">
                    Такого экрана не существует.
                </Text>

                <Link href="/" className="mt-4 py-4">
                    <Text className="text-base underline text-link">
                        Go to home screen. {'\n'}
                    </Text>
                    <Text className="text-base underline text-link">
                        Перейти на главный экран.
                    </Text>
                </Link>
            </View>
        </>
    )
}

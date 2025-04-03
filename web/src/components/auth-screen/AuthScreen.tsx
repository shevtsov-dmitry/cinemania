import React, { ReactElement } from 'react'
import { Text, View } from 'react-native'

import LoginWithGoogleMobile from './LoginWithGoogleMobile'
import LoginWithGoogleWeb from './LoginWithGoogleWeb'
import EmailAuth from './EmailAuth'

const AuthScreen = (): ReactElement => {
    return (
        <View className="flex h-full w-full items-center justify-center">
            <View className="absolute h-full w-full bg-black opacity-40 blur" />
            <View className="flex h-1/3 w-1/4 justify-around rounded-xl border-2 border-solid border-white bg-sky-950 px-2 shadow">
                <Text className="mt-2 text-center text-3xl font-semibold text-white">
                    Логин
                </Text>
                <EmailAuth />
                {/* <LoginWithGoogleMobile /> */}
                <LoginWithGoogleWeb />
                {/* <LoginWithVk /> */}
            </View>
        </View>
    )
}

export default AuthScreen

import React, { ReactElement } from 'react'
import { Text, View } from 'react-native'

const AuthScreen = (): ReactElement => {
    return (
        <View className="flex h-full w-full items-center justify-center">
            <View className="h-3/4 w-9/12 rounded-xl bg-white shadow">
                <LoginWithGoogleMobile />
                <LoginWithGoogleWeb />
                {/* <LoginWithVk /> */}
            </View>
        </View>
    )
}

export default AuthScreen

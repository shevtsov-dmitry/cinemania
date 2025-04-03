import React , { ReactElement } from 'react'
import { View, Button, Alert, Pressable } from 'react-native'
import { Image } from 'expo-image'
import {
    GoogleSignin,
    statusCodes,
} from '@react-native-google-signin/google-signin'
import Constants from '@/src/constants/Constants'

GoogleSignin.configure({
    webClientId: `${process.env.GOOGLE_CLIENT_ID}`,
    offlineAccess: false,
})

const LoginScreen = (): ReactElement => {
    const signIn = async () => {
        try {
            await GoogleSignin.hasPlayServices()
            const userInfo = await GoogleSignin.signIn()
            const idToken = userInfo.idToken

            const response = await fetch(
                `${Constants.VERIFICATION_SERVICE_URL}/api/v1/auth/login/google`,
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(idToken),
                }
            )

            if (response.ok) {
                const token = await response.text()
                Alert.alert('Успешный вход', 'Login successful')
            } else {
                Alert.alert('Ошибка входа', 'Login failed')
            }
        } catch (error: any) {
            if (error.code === statusCodes.SIGN_IN_CANCELLED) {
                Alert.alert('Отмена попытки входа', 'Sign-in was cancelled')
            } else if (error.code === statusCodes.IN_PROGRESS) {
                Alert.alert('Ожидание...', 'Sign-in is already in progress')
            } else if (error.code === statusCodes.PLAY_SERVICES_NOT_AVAILABLE) {
                Alert.alert(
                    'Проблема с подключением',
                    'Play services not available'
                )
            } else {
                Alert.alert('Непредвиденная ошибка', error.message)
            }
        }
    }

    return (
        <View className="flex flex-1 items-center justify-center">
            <Pressable onPress={signIn}>
                <Image
                    source={require('@/images/icons/google-icon.svg')}
                    style={{ width: 16, height: 16 }}
                />
            </Pressable>
        </View>
    )
}

export default LoginScreen

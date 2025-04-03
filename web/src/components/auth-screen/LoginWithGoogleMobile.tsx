import React from 'react'
import { View, Button, Alert } from 'react-native'
import {
    GoogleSignin,
    statusCodes,
} from '@react-native-google-signin/google-signin'
import Constants from '@/src/constants/Constants'

GoogleSignin.configure({
    webClientId: `${process.env.GOOGLE_CLIENT_ID}`,
    offlineAccess: false,
})

const LoginScreen = () => {
    const signIn = async () => {
        try {
            await GoogleSignin.hasPlayServices()
            const userInfo = await GoogleSignin.signIn()
            const idToken = userInfo.idToken

            const response = await fetch(
                `http://${Constants.VERIFICATION_SERVICE_URL}/api/v1/auth/login/google`,
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
        <View
            style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}
        >
            <Button title="Войти с помощью Google" onPress={signIn} />
        </View>
    )
}

export default LoginScreen

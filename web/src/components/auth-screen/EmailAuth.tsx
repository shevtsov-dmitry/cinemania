import Constants from '@/src/constants/Constants'
import React, { useState, ReactElement } from 'react'
import { Button, TextInput, View } from 'react-native'

const EmailAuth = (): ReactElement => {
    const [email, setEmail] = useState<string>('')

    function handleLogin() {
        try {
            fetch(
                Constants.VERIFICATION_SERVICE_URL + '/api/v1/auth/login/email'
            )
        } catch (error) {
            console.error('Непредвиденная ошибка входа.', error)
            alert('Не удалось войти из за непредвиденной ошибки', error)
        }
    }

    return (
        <View className="">
            <TextInput
                value={email}
                onChange={(e) => setEmail(e.target.value)}
            />
            <Button onPress={handleLogin}>Войти</Button>
        </View>
    )
}

export default EmailAuth

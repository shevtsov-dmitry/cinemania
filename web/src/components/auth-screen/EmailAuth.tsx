import Constants from '@/src/constants/Constants'
import React, { useState, ReactElement } from 'react'
import { Button, TextInput, View, Text, Pressable } from 'react-native'

const EmailAuth = (): ReactElement => {
    const [email, setEmail] = useState<string>('')

    function handleLogin() {
        fetch(Constants.VERIFICATION_SERVICE_URL + '/api/v1/auth/login/email', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email }),
        })
            .then((response) => response.json())
            .then((data) => {
                console.log('Code sent:', data)
            })
            .catch((error) => {
                console.error('Ошибка отправки кода', error)
                alert('Ошибка отправки кода')
            })
    }

    return (
        <View className="space-y-2">
            <Text className="text-lg font-semibold text-white">Почта</Text>
            <TextInput
                className="rounded bg-neutral-100 py-2 text-xl placeholder:pl-2"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Введите email"
            />
            <Pressable
                className="w-fit rounded-xl bg-blue-500 p-2 text-white shadow"
                onPress={handleLogin}
            >
                <Text className="text-xl font-bold text-white">Войти</Text>
            </Pressable>
        </View>
    )
}

export default EmailAuth

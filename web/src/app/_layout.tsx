import FontAwesome from '@expo/vector-icons/FontAwesome'
// import {
//     DarkTheme,
//     DefaultTheme,
//     ThemeProvider,
// } from '@react-navigation/native'
import { useFonts } from 'expo-font'
import { Stack } from 'expo-router'
import * as SplashScreen from 'expo-splash-screen'
import { useEffect } from 'react'
import { useColorScheme } from 'react-native'
import '../../global.css'

export { ErrorBoundary } from 'expo-router'

export const unstable_settings = {
    initialRouteName: 'home', // Adjust as needed
}

SplashScreen.preventAutoHideAsync()

export default function RootLayout() {
    const [loaded, error] = useFonts({
        // SpaceMono: require('../../assets/fonts/SpaceMono-Regular.ttf'),
        ...FontAwesome.font,
    })

    const colorScheme = useColorScheme()

    useEffect(() => {
        if (error) throw error
    }, [error])

    useEffect(() => {
        if (loaded) {
            SplashScreen.hideAsync()
        }
    }, [loaded])

    if (!loaded) {
        return null
    }

    return (
        // <ThemeProvider
        //     value={colorScheme === 'dark' ? DarkTheme : DefaultTheme}
        // >
        <Stack screenOptions={{ headerShown: false }} />
        // </ThemeProvider>
    )
}

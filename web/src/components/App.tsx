import { Text, View } from 'react-native'
import { Stack } from 'expo-router'
import { ReactElement } from 'react'
import Home from '@/src/components/home/Home'
import TopPanel from '@/src/components/top-panel/TopPanel'
import AdminPage from './admin/AdminPage'
import ContentPage from './content-page/ContentPage'

const App = (): ReactElement => (
    <View className="min-w-100 h-dvh min-h-20 bg-neutral-800">
        <TopPanel />
        <Stack screenOptions={{ headerShown: false }}>
            <Stack.Screen name="home" component={Home} />
            <Stack.Screen name="admin" component={AdminPage} />
            <Stack.Screen name="content" component={ContentPage} />
        </Stack>
    </View>
)

export default App

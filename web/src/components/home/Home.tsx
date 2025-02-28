import React, { ReactElement } from 'react'
import { Text, View } from 'react-native'
import Preview from './Preview'
import Colors from '@/src/constants/Colors'

const Home = (): ReactElement => {
    return (
        <View
            className="min-h-screen w-screen"
            style={{
                backgroundImage: Colors.TEAL_DARK_GRADIENT_BG_IMAGE,
            }}
        >
            <Text className={'p-2 text-2xl font-bold text-white'}>Новинки</Text>
            <Preview />
            <Text className={'p-2 text-2xl font-bold text-white'}>
                Вам может понравится
            </Text>
        </View>
    )
}

export default Home

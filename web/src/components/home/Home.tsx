import React, { ReactElement } from 'react'
import { Text, View } from 'react-native'
import Preview from './Preview'

const Home = (): ReactElement => {
    return (
        <View
            className="min-h-screen w-screen"
            style={{
                backgroundImage:
                    'linear-gradient(to right bottom, #3c4d66, #333f53, #2a3141, #20242f, #16181f)',
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

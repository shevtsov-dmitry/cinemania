import React, { ReactElement } from 'react'
import { Text } from 'react-native'
import Preview from './Preview'

const Home = (): ReactElement => {
    return (
        <>
            <Text className={'p-2 text-2xl font-bold text-white'}>Новинки</Text>
            <Preview />
            <Text className={'p-2 text-2xl font-bold text-white'}>
                Вам может понравится
            </Text>
        </>
    )
}

export default Home

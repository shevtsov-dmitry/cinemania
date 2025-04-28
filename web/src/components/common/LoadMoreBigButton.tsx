import React, { ReactElement } from 'react'
import { Pressable, Text, View } from 'react-native'

const LoadMoreBigButton = (): ReactElement => (
    <View className="my-6 items-center">
        <Pressable className="rounded-2xl bg-neutral-800 px-6 py-3 opacity-90 shadow-lg active:bg-neutral-700">
            <Text className="select-none text-lg font-semibold text-white">
                Загрузить ещё
            </Text>
        </Pressable>
    </View>
)

export default LoadMoreBigButton

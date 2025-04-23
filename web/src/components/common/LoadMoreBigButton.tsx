import {Pressable, Text, View} from "react-native";
import React, {ReactElement} from "react";

const LoadMoreBigButton = (): ReactElement => (
    <View className="items-center my-6">
        <Pressable className="bg-blue-700 px-6 py-3 rounded-2xl shadow-lg active:bg-blue-800">
            <Text className="text-white text-lg font-semibold">Загрузить ещё</Text>
        </Pressable>
    </View>
)

export default LoadMoreBigButton
import { View, Text } from 'react-native'

export default function TabTwoScreen() {
    return (
        <View className="flex flex-1 items-center justify-center bg-black">
            <Text className="text-xl font-bold">Tab Two</Text>
            <View className="my-7 h-[1px] w-[80%] bg-black/10" />
        </View>
    )
}

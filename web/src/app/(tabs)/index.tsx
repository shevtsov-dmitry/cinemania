// 'use dom'

import { View, Text } from '@/src/components/Themed'

import EditScreenInfo from '@/src/components/EditScreenInfo'

//    <View className="flex flex-1 items-center justify-center">
//      <Text className="text-xl font-bold">Tab One</Text>
//      <View className="my-7 h-[1px] w-[80%] bg-black/10"/>
//      <EditScreenInfo path="src/app/(tabs)/index.tsx" />
//    </View>
export default function TabOneScreen() {
    // return <div className="bg-red-500 text-white">Tab One</div>
    return (
        <View className="flex flex-1 items-center justify-center">
            <Text className="text-xl font-bold">Tab One</Text>
            <View className="my-7 h-[1px] w-[80%] bg-black/10" />
            <EditScreenInfo path="src/app/(tabs)/index.tsx" />
        </View>
    )
}

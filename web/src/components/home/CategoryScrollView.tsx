import { FontAwesome5, Ionicons, MaterialIcons } from '@expo/vector-icons'
import React from 'react'
import { ScrollView, Text, TouchableOpacity, View } from 'react-native'

const categories = [
    {
        name: 'Фильмы',
        icon: <FontAwesome5 name="film" size={20} color="white" />,
    },
    {
        name: 'Сериалы',
        icon: <MaterialIcons name="tv" size={24} color="white" />,
    },
    {
        name: 'Мультфильмы',
        icon: <FontAwesome5 name="baby" size={20} color="white" />,
    },
    {
        name: 'Спорт',
        icon: <FontAwesome5 name="futbol" size={20} color="white" />,
    },
    {
        name: 'Документальное',
        icon: <Ionicons name="document-text" size={22} color="white" />,
    },
    {
        name: 'Фитнес',
        icon: <MaterialIcons name="fitness-center" size={22} color="white" />,
    },
    { name: 'Природа', icon: <Ionicons name="leaf" size={22} color="white" /> },
    {
        name: 'Лекции',
        icon: <Ionicons name="school" size={22} color="white" />,
    },
    {
        name: 'Кулинария',
        icon: <MaterialIcons name="restaurant-menu" size={22} color="white" />,
    },
]

const CategoryScrollView = () => {
    const handlePress = (name: string) => {
        console.log(`Category pressed: ${name}`)
        // TODO navigate, filter, etc.
    }

    return (
        <ScrollView
            horizontal
            showsHorizontalScrollIndicator={false}
            className="flex-row p-2"
        >
            {categories.map((category, index) => (
                <TouchableOpacity
                    key={index}
                    className="mr-2 flex-row items-center rounded-xl bg-neutral-900 px-4 py-2"
                    onPress={() => handlePress(category.name)}
                >
                    <View className="mr-2">{category.icon}</View>
                    <Text className="text-sm text-white">{category.name}</Text>
                </TouchableOpacity>
            ))}
        </ScrollView>
    )
}

export default CategoryScrollView

import useFormAddCreatorStore from '@/src/state/formAddCreatorState'
import useFormAddFilmStore from '@/src/state/formAddFilmState'
import { Feather } from '@expo/vector-icons'
import React, { ReactElement, useRef, useState } from 'react'
import {
    FlatList,
    Image,
    Pressable,
    Text,
    TextInput,
    TouchableOpacity,
    View,
} from 'react-native'
import Animated, {
    useAnimatedStyle,
    useSharedValue,
    withTiming,
} from 'react-native-reanimated'
import CategoryScrollView from '../home/CategoryScrollView'

const TopPanel = (): ReactElement => {
    const generalTopicsRef = useRef<FlatList | null>(null)
    const newShowsAndCollectionsRef = useRef<View | null>(null)
    const loginImageRef = useRef<View | null>(null)
    const searchImageRef = useRef<View | null>(null)
    const burgerImageRef = useRef<Image | null>(null)
    const closeImageRef = useRef<Image | null>(null)

    const [isBurgerActive, setIsBurgerActive] = useState(false)
    const [isSearchActive, setIsSearchActive] = useState(false)
    const [searchQuery, setSearchQuery] = useState('')
    const isMobile = false

    const isFormAddFilmVisible = useFormAddFilmStore(
        (state) => state.isFormAddFilmVisible
    )
    const toggleFormAddFilm = useFormAddFilmStore(
        (state) => state.toggleFormAddFilm
    )
    const toggleFormAddCreator = useFormAddCreatorStore(
        (state) => state.toggleFormAddCreator
    )

    // Animation for width transition
    const searchWidth = useSharedValue(120) // Initial width for button mode (approximate)

    const animatedSearchStyle = useAnimatedStyle(() => {
        return {
            width: withTiming(searchWidth.value, { duration: 300 }),
        }
    })

    // Function to handle button presses
    const handleSubscribePress = () => {
        console.log('Оформить подписку button pressed!')
    }

    const handleSearchPress = () => {
        setIsSearchActive(true)
        searchWidth.value = 200 // Expand width for input mode
    }

    const handleExecuteSearch = () => {
        if (searchQuery.trim()) {
            console.log('Executing search with query:', searchQuery)
        }
        setIsSearchActive(false)
        setSearchQuery('')
        searchWidth.value = 120 // Reset width for button mode
    }

    const handleLoginPress = () => {
        console.log('Login button pressed!')
    }

    const BurgerPanel = (): ReactElement => (
        <>
            <View className="fixed z-20 h-full w-full bg-gray-800 transition-all duration-300 ease-in-out">
                <View className="ml-5 mt-2.5">
                    <Text className="text-lg uppercase text-white">
                        {/*{generalTopicsLiContent()}*/}
                    </Text>
                    {/*<View>{newsAndCollectionContent()}</View>*/}
                </View>
            </View>
            <View className="fixed bottom-2.5 left-2.5 z-20">
                <Text className="text-xs text-white opacity-75">
                    © 2024 ООО «Bē commerce»
                </Text>
            </View>
        </>
    )

    const TempoAdminPanel = (): ReactElement => (
        <View className="rounded-2xl bg-neutral-700 px-5 py-2">
            <Text className="text-sm text-white">Панель администратора</Text>
            <Pressable onPress={toggleFormAddFilm}>
                <Text className="text-orange-500">Добавить новый фильм</Text>
            </Pressable>
            <Pressable onPress={toggleFormAddCreator}>
                <Text className="text-orange-500">
                    Добавить члена съёмочной группы
                </Text>
            </Pressable>
        </View>
    )

    return (
        <View>
            <View className="mx-[0.5%] flex-row items-center justify-between text-white">
                {/* Logo */}
                <TouchableOpacity>
                    <Image
                        className="h-16 w-16"
                        source={require('@/images/icons/company_logo.png')}
                        alt="company logo"
                    />
                </TouchableOpacity>

                <View className="ml-5" />
                {/* Show categories */}
                <CategoryScrollView />

                <View className="flex-row items-center">
                    <View
                        ref={newShowsAndCollectionsRef}
                        className="gap-1.25 flex-row"
                    />

                    {/* <TempoAdminPanel /> */}
                    {/* <TempoAdminPanel /> */}

                    {/* Subscribe Button */}
                    <Pressable
                        onPress={handleSubscribePress}
                        className="mx-2 rounded-full bg-gradient-to-r from-[#FF6F61] to-[#DE4A6F] px-4 py-2 shadow-lg"
                    >
                        <Text className="text-sm font-bold text-white">
                            Оформить подписку
                        </Text>
                    </Pressable>
                    <View className="gap-1.25 flex-row"></View>
                    {/* Search Section */}
                    {isSearchActive ? (
                        <Animated.View
                            style={[animatedSearchStyle]}
                            className="ml-2 mr-8 flex-row items-center rounded-full border border-gray-600 bg-[#1E2A44] shadow-md"
                        >
                            <TextInput
                                className="flex-1 px-3 py-2 text-white"
                                placeholder="Искать..."
                                placeholderTextColor="rgba(255, 255, 255, 0.7)"
                                value={searchQuery}
                                onChangeText={setSearchQuery}
                                onSubmitEditing={handleExecuteSearch}
                                autoFocus
                            />
                            <Pressable
                                onPress={handleExecuteSearch}
                                className="p-2"
                            >
                                <Feather
                                    name="arrow-right-circle"
                                    size={20}
                                    color="white"
                                />
                            </Pressable>
                        </Animated.View>
                    ) : (
                        <Pressable
                            ref={searchImageRef}
                            onPress={handleSearchPress}
                            className="mx-2 flex-row items-center rounded-full border border-gray-600 bg-[#1E2A44] px-3 py-2 shadow-md"
                        >
                            <Feather name="search" size={20} color="white" />
                            <Text className="ml-2 text-white underline opacity-70">
                                Искать...
                            </Text>
                        </Pressable>
                    )}

                    {/* Login Button */}
                    <Pressable
                        ref={loginImageRef}
                        onPress={handleLoginPress}
                        className="mx-2 flex-row items-center rounded-full border border-gray-600 bg-[#1E2A44] px-3 py-2 shadow-md"
                    >
                        <Feather name="log-in" size={20} color="white" />
                        <Text className="ml-2 text-white underline opacity-70">
                            Войти
                        </Text>
                    </Pressable>
                    {isMobile && (
                        <View>
                            <TouchableOpacity
                                onPress={() => setIsBurgerActive(true)}
                            >
                                <Image
                                    ref={burgerImageRef}
                                    className="w-5.75 h-5.75"
                                    source={require('@/images/icons/burger.svg')}
                                    alt=""
                                />
                            </TouchableOpacity>
                            <TouchableOpacity
                                onPress={() => setIsBurgerActive(false)}
                            >
                                <Image
                                    ref={closeImageRef}
                                    className="w-5.75 h-5.75"
                                    source={require('@/images/icons/close-sign.svg')}
                                    alt=""
                                />
                            </TouchableOpacity>
                        </View>
                    )}
                </View>
            </View>
            {isBurgerActive && <BurgerPanel />}
        </View>
    )
}

export default TopPanel

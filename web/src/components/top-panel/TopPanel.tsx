import React, { ReactElement, useEffect, useRef, useState } from 'react'
import {
    View,
    Text,
    Image,
    TouchableOpacity,
    FlatList,
    Pressable,
} from 'react-native'
import useFormAddFilmStore from '@/src/state/formAddFilmState'
import useFormAddCreatorStore from '@/src/state/formAddCreatorState'

// TODO use expo router instead
// import { Link, Route, Routes } from 'react-router-dom';

const TopPanel = (): ReactElement => {
    const generalTopicsRef = useRef<FlatList | null>(null)
    const newShowsAndCollectionsRef = useRef<View | null>(null)
    const loginImageRef = useRef<Image | null>(null)
    const searchImageRef = useRef<Image | null>(null)
    const burgerImageRef = useRef<Image | null>(null)
    const closeImageRef = useRef<Image | null>(null)

    const [isBurgerActive, setIsBurgerActive] = useState(false)

    const isFormAddFilmVisible = useFormAddFilmStore(
        (state) => state.isFormAddFilmVisible
    )
    const toggleFormAddFilm = useFormAddFilmStore(
        (state) => state.toggleFormAddFilm
    )
    const toggleFormAddCreator = useFormAddCreatorStore(
        (state) => state.toggleFormAddCreator
    )

    const topics = ['Фильмы', 'Сериалы', 'Мультфильмы', 'Аниме']

    /*   useEffect(() => {
    showAndHideBurgerMenu();
  }, []) */
    // function showAndHideBurgerMenu() {
    //   burgerImageRef.current.addEventListener("click", () => {
    //     burgerImageRef.current.style.display = "none";
    //     closeImageRef.current.style.display = "block";
    //     setIsBurgerActive(true);
    //   });
    //   closeImageRef.current.addEventListener("click", () => {
    //     burgerImageRef.current.style.display = "block";
    //     closeImageRef.current.style.display = "none";
    //     setIsBurgerActive(false);
    //   });
    // }

    // TODO restore commented parts
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

    const GeneralTopic = ({
        topicName,
    }: {
        topicName: string
    }): ReactElement =>
        isBurgerActive ? (
            <Text>{topicName + ' ▼'}</Text>
        ) : (
            <Text>{topicName}</Text>
        )
    // signs variants: ▼ᐁ

    return (
        <View>
            <View className="mx-[3.5%] flex-row items-center justify-between text-white">
                <TouchableOpacity>
                    <Image
                        className="mt-2.5 h-16 w-16"
                        source={require('@/images/icons/company_logo.png')}
                        alt="company logo"
                    />
                </TouchableOpacity>
                <FlatList
                    ref={generalTopicsRef}
                    className="gap-1.25 flex-row"
                    horizontal
                    data={topics}
                    renderItem={({ item, index }) => (
                        <GeneralTopic key={index} topicName={item} />
                    )}
                />
                <View
                    ref={newShowsAndCollectionsRef}
                    className="gap-1.25 flex-row"
                >
                    <View>
                        <Text>Новинки</Text>
                        <Text>Подборки</Text>
                    </View>
                </View>
                <View className="rounded-2xl bg-neutral-700 px-5 py-2">
                    <Text className="text-sm text-white">
                        Панель администратора
                    </Text>
                    <Pressable onPress={toggleFormAddFilm}>
                        <Text className="text-orange-500">
                            Добавить новый фильм
                        </Text>
                    </Pressable>
                    <Pressable onPress={toggleFormAddCreator}>
                        <Text className="text-orange-500">
                            Добавить члена съёмочной группы
                        </Text>
                    </Pressable>
                </View>
                <View className="gap-1.25 flex-row"></View>
                <View className="gap-0.25 flex-row items-center">
                    <Image
                        ref={searchImageRef}
                        className="w-5.75 h-5.75"
                        source={require('@/images/icons/search.svg')}
                        alt=""
                    />
                    <Text className="opacity-70">Искать...</Text>
                </View>
                <View className="flex-row items-center gap-0.5">
                    <Image
                        ref={loginImageRef}
                        className="w-5.75 h-5.75"
                        source={require('@/images/icons/login.svg')}
                        alt="login"
                    />
                    <Text className="text-white">Войти</Text>
                </View>
                <TouchableOpacity onPress={() => setIsBurgerActive(true)}>
                    <Image
                        ref={burgerImageRef}
                        className="w-5.75 h-5.75"
                        source={require('@/images/icons/burger.svg')}
                        alt=""
                    />
                </TouchableOpacity>
                <TouchableOpacity onPress={() => setIsBurgerActive(false)}>
                    <Image
                        ref={closeImageRef}
                        className="w-5.75 h-5.75"
                        source={require('@/images/icons/close-sign.svg')}
                        alt=""
                    />
                </TouchableOpacity>
            </View>
            {isBurgerActive && <BurgerPanel />}
        </View>
    )
}

export default TopPanel

/*     <Route */
/*         path="/add-new-film" */
/*         element={ */
/*             <div className="fixed top-0 left-0 z-50 flex items-center justify-center h-dvh w-dvw"> */
/*                 <div className="absolute h-full w-full bg-gray-800 backdrop-blur-md opacity-85 dark:backdrop-blur-lg"></div> */
/*                 <div className="z-50"> */
/*                     <FormAddFilm /> */
/*                 </div> */
/*             </div> */
/*         } */
/*     /> */
/* </Routes> */

import { useEffect, useRef, useState } from "react";
import FormAddFilm from "@/src/components/admin/form-add-film/FormAddFilm";
import useStore from "@/src/state/useStore";
import { View, Text, Image, TouchableOpacity, FlatList } from "react-native";

// TODO use expo router instead
// import { Link, Route, Routes } from 'react-router-dom';

/**
 *
 * @returns {JSX.Element}
 */
export default function Header() {
  const generalTopicsRef = useRef();
  const newShowsAndCollectionsRef = useRef();
  const loginImageRef = useRef();
  const searchImageRef = useRef();
  const burgerImageRef = useRef();
  const closeImageRef = useRef();

  const [isBurgerActive, setIsBurgerActive] = useState(false);

  const isFormAddFilmVisible = useStore((state) => state.isFormAddFilmVisible);
  const toggleFormAddFilm = useStore((state) => state.showFormAddFilm);

  const topics = ["Фильмы", "Сериалы", "Мультфильмы", "Аниме"];

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
  const BurgerPanel = () => (
    <>
      <View className="fixed z-20 h-full w-full bg-gray-800 transition-all duration-300 ease-in-out">
        <View className="ml-5 mt-2.5">
          <Text className="text-lg text-white uppercase">
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
  );

  /**
   * @param {Object} props
   * @param {string} props.topicName
   * @returns {JSX.Element}
   */
  const GeneralTopic = ({ topicName }) =>
    isBurgerActive ? <Text>{topicName + " ▼"}</Text> : <Text>{topicName}</Text>;
  // signs variants: ▼ᐁ

  return (
    <View>
      <View className="flex-row justify-between items-center mx-[3.5%] text-white">
        <TouchableOpacity>
          <Image
            className="mt-2.5 w-16 h-16"
            source={require("@/images/icons/company_logo.png")}
            alt="company logo"
          />
        </TouchableOpacity>
        <FlatList
          ref={generalTopicsRef}
          className="flex-row gap-1.25"
          horizontal
          data={topics}
          renderItem={({ item, idx }) => (
            <GeneralTopic key={idx} topicName={item} />
          )}
        />
        <View ref={newShowsAndCollectionsRef} className="flex-row gap-1.25">
          <View>
            <Text>Новинки</Text>
            <Text>Подборки</Text>
          </View>
        </View>
        <TouchableOpacity onPress={toggleFormAddFilm}>
          <Text className="text-orange-500">Добавить новый фильм</Text>
        </TouchableOpacity>
        <View className="flex-row gap-1.25"></View>
        <View className="flex-row items-center gap-0.25">
          <Image
            ref={searchImageRef}
            className="w-5.75 h-5.75"
            source={require("@/images/icons/search.svg")}
            alt=""
          />
          <Text className="opacity-70">Искать...</Text>
        </View>
        <View className="flex-row items-center gap-0.5">
          <Image
            ref={loginImageRef}
            className="w-5.75 h-5.75"
            source={require("@/images/icons/login.svg")}
            alt="login"
          />
          <Text className="text-white">Войти</Text>
        </View>
        <TouchableOpacity onPress={() => setIsBurgerActive(true)}>
          <Image
            ref={burgerImageRef}
            className="w-5.75 h-5.75"
            source={require("@/images/icons/burger.svg")}
            alt=""
          />
        </TouchableOpacity>
        <TouchableOpacity onPress={() => setIsBurgerActive(false)}>
          <Image
            ref={closeImageRef}
            className="w-5.75 h-5.75"
            source={require("@/images/icons/close-sign.svg")}
            alt=""
          />
        </TouchableOpacity>
      </View>
      {isBurgerActive && <BurgerPanel />}
    </View>
  );
}

/*     <Route */
/*         path="/add-new-film" */
/*         element={ */
/*             <div className="fixed left-0 top-0 z-50 flex h-dvh w-dvw items-center justify-center"> */
/*                 <div className="absolute h-full w-full bg-gray-800 opacity-85 backdrop-blur-md dark:backdrop-blur-lg"></div> */
/*                 <div className="z-50"> */
/*                     <FormAddFilm /> */
/*                 </div> */
/*             </div> */
/*         } */
/*     /> */
/* </Routes> */

import { useEffect, useRef, useState } from "react";
import FormAddFilm from "@/src/components/admin/form-add-film/FormAddFilm";
import useStore from "@/src/state/useStore";

// TODO use expo router instead
// import { Link, Route, Routes } from 'react-router-dom';

/**
 *
 * @returns {JSX.Element}
 */
export default function Header() {
  const topics = ["Фильмы", "Сериалы", "Мультфильмы", "Аниме"];

  const generalTopicsRef = useRef();
  const newShowsAndCollectionsRef = useRef();
  const loginImageRef = useRef();
  const searchImageRef = useRef();
  const burgerImageRef = useRef();
  const closeImageRef = useRef();

  const [isBurgerActive, setIsBurgerActive] = useState(false);

  const isFormAddFilmVisible = useStore((state) => state.isFormAddFilmVisible);
  const toggleFormAddFilm = useStore((state) => state.showFormAddFilm);

  useEffect(() => {
    showAndHideBurgerMenu();
  }, []);

  function showAndHideBurgerMenu() {
    burgerImageRef.current.addEventListener("click", () => {
      burgerImageRef.current.style.display = "none";
      closeImageRef.current.style.display = "block";
      setIsBurgerActive(true);
    });
    closeImageRef.current.addEventListener("click", () => {
      burgerImageRef.current.style.display = "block";
      closeImageRef.current.style.display = "none";
      setIsBurgerActive(false);
    });
  }

  const BurgerPanel = () => (
    <>
      <main
        id="burger-popup"
        className="fixed z-20 h-lvh w-lvw bg-neutral-800 transition-all"
      >
        <div className="ml-5 mt-2 text-[1.25em] uppercase leading-9 text-white">
          <ul className="">{generalTopicsLiContent()}</ul>
          <div>{newsAndCollectionContent()}</div>
        </div>
      </main>
      <footer className="fixed bottom-2 left-3 z-20 text-sm text-white opacity-75">
        © 2024 ООО «Bē commerce»
      </footer>
    </>
  );

  /**
   * @param {Object} props
   * @param {string} props.topicName
   * @returns {JSX.Element}
   */
  const GeneralTopic = ({ topicName }) =>
    isBurgerActive ? <li>{topicName + " ▼"}</li> : <li>{topicName}</li>;
  // signs variants: ▼ᐁ

  return (
    <>
      <header
        id="upper-header"
        className="text-amber-whiteflex ml-[3.5%] mr-[3.5%] flex items-center justify-between text-white"
      >
        {/* <Link to="/"> */}
        <img
          className="mt-2 w-16 hover:cursor-pointer"
          id="company-logo"
          src="assets/images/icons/company_logo.png"
          alt="company logo"
        />
        {/* </Link> */}
        <ul
          ref={generalTopicsRef}
          id="general-topics"
          className="flex gap-5 max-[1024px]:hidden"
        >
          {topics.map((topicName, idx) => (
            <GeneralTopic key={idx} topicName={topicName} />
          ))}
        </ul>
        <div
          ref={newShowsAndCollectionsRef}
          id="new-shows-and-collections"
          className="flex gap-5 max-[1024px]:hidden"
        >
          <div>
            <p id="new-shows">Новинки</p>
            <p id="collections">Подборки</p>
          </div>
        </div>

        {/* <Link to="/add-new-film"> */}
        <button
          className="transition-colors hover:cursor-pointer hover:text-orange-400 hover:underline"
          onClick={toggleFormAddFilm}
        >
          Добавить новый фильм
        </button>
        {/* </Link> */}

        <div className="flex w-fit items-center justify-end gap-5">
          <div id="search" className="flex items-center gap-1 text-2xl">
            <img
              ref={searchImageRef}
              id="search-icon"
              className={`w-[23px]`}
              src={"assets/images/icons/search.svg"}
              alt=""
            />
            <span
              className={"text-base underline opacity-70 max-[1024px]:hidden"}
            >
              Искать... {isFormAddFilmVisible ? "yes" : "no"}
            </span>
          </div>
          <div id="login-block" className="flex items-center gap-2">
            <img
              ref={loginImageRef}
              id="login-icon"
              className={`w-[23px] hover:cursor-pointer`}
              src={"assets/images/icons/login.svg"}
              alt="login"
            />
            <p className="cursor-pointer select-none text-white max-[1024px]:hidden">
              Войти
            </p>
          </div>
          <img
            ref={burgerImageRef}
            className={`hidden w-[23px] scale-125 hover:cursor-pointer max-[1024px]:block`}
            src={"assets/images/icons/burger.svg"}
            alt=""
          />
          <img
            ref={closeImageRef}
            className={`hidden w-[23px] scale-75 hover:cursor-pointer min-[1024px]:hidden`}
            src={"assets/images/icons/close-sign.svg"}
            alt=""
          />
        </div>
      </header>
      {isBurgerActive && <BurgerPanel />}

      {/* <Routes> */}
      {/*     <Route */}
      {/*         path="/add-new-film" */}
      {/*         element={ */}
      {/*             <div className="fixed left-0 top-0 z-50 flex h-dvh w-dvw items-center justify-center"> */}
      {/*                 <div className="absolute h-full w-full bg-gray-800 opacity-85 backdrop-blur-md dark:backdrop-blur-lg"></div> */}
      {/*                 <div className="z-50"> */}
      {/*                     <FormAddFilm /> */}
      {/*                 </div> */}
      {/*             </div> */}
      {/*         } */}
      {/*     /> */}
      {/* </Routes> */}
    </>
  );
}

import { useEffect, useRef, useState } from "react";
import FormAddFilm from "@/src/components/admin/form-add-film/FormAddFilm";

// TODO use expo router instead
// import { Link, Route, Routes } from 'react-router-dom';

export default function Header() {
  const generalTopicsRef = useRef();
  const newShowsAndCollectionsRef = useRef();
  const loginImageRef = useRef();
  const searchImageRef = useRef();
  const burgerImageRef = useRef();
  const closeImageRef = useRef();

  const [burgerActive, setBurgerActive] = useState(false);

  useEffect(() => {
    showAndHideBurgerMenu();
  }, []);

  function showAndHideBurgerMenu() {
    burgerImageRef.current.addEventListener("click", () => {
      burgerImageRef.current.style.display = "none";
      closeImageRef.current.style.display = "block";
      setBurgerActive(true);
    });
    closeImageRef.current.addEventListener("click", () => {
      burgerImageRef.current.style.display = "block";
      closeImageRef.current.style.display = "none";
      setBurgerActive(false);
    });
  }

  const showOpenedBurgerPanel = () => {
    if (!burgerActive) {
      return null;
    } else {
      return (
        <>
          <main
            id="burger-popup"
            className="h-lvh w-lvw fixed z-20 bg-neutral-800 transition-all"
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
    }
  };

  function generalTopicsLiContent() {
    // signs variants: ▼ᐁ
    const topics = ["Фильмы", "Сериалы", "Мультфильмы", "Аниме"];
    return burgerActive ? (
      <>
        {topics.map((string, index) => (
          <li key={index}>{string.concat(" ▼")}</li>
        ))}
      </>
    ) : (
      <>
        {topics.map((string, index) => (
          <li key={index}>{string}</li>
        ))}
      </>
    );
  }

  function newsAndCollectionContent() {
    return (
      <div>
        <p id="new-shows">Новинки</p>
        <p id="collections">Подборки</p>
      </div>
    );
  }

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
          src="icons/company_logo.png"
          alt="company logo"
        />
        {/* </Link> */}
        <ul
          ref={generalTopicsRef}
          id="general-topics"
          className="flex gap-5 max-[1024px]:hidden"
        >
          {generalTopicsLiContent()}
        </ul>
        <div
          ref={newShowsAndCollectionsRef}
          id="new-shows-and-collections"
          className="flex gap-5 max-[1024px]:hidden"
        >
          {newsAndCollectionContent()}
        </div>

        {/* <Link to="/add-new-film"> */}
        <p className="transition-colors hover:cursor-pointer hover:text-orange-400 hover:underline">
          Добавить новый фильм
        </p>
        {/* </Link> */}

        <div className="flex w-fit items-center justify-end gap-5">
          <div id="search" className="flex items-center gap-1 text-2xl">
            <img
              ref={searchImageRef}
              id="search-image"
              className={`w-[23px]`}
              src={"icons/search.png"}
              alt=""
            />
            <span
              className={"text-base underline opacity-70 max-[1024px]:hidden"}
            >
              Искать...
            </span>
          </div>
          <div id="login-block" className="flex items-center gap-2">
            <img
              ref={loginImageRef}
              id="login-icon"
              className={`w-[23px] hover:cursor-pointer`}
              src={"icons/login.png"}
              alt="login"
            />
            <p className="cursor-pointer select-none text-white max-[1024px]:hidden">
              Войти
            </p>
          </div>
          <img
            ref={burgerImageRef}
            className={`hidden w-[23px] scale-125 hover:cursor-pointer max-[1024px]:block`}
            src={"icons/burger.png"}
            alt=""
          />
          <img
            ref={closeImageRef}
            className={`hidden w-[23px] scale-75 hover:cursor-pointer min-[1024px]:hidden`}
            src={"icons/close.png"}
            alt=""
          />
        </div>
      </header>
      {showOpenedBurgerPanel()}

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

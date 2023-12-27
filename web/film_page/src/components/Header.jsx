import { useEffect, useRef } from 'react'

export function Header() {
    const generalTopicsRef = useRef()
    const newShowsAndCollectionsRef = useRef()

    return (
        <>
            <header
                id="upper-header"
                className="text-amber-white ml-[3.5%] mr-[3.5%] flex items-center justify-between text-white"
            >
                <img
                    className="mt-2 w-[6.5%]"
                    id="company-logo"
                    src="icons/company_logo.png"
                    alt="company logo"
                />
                <ul
                    ref={generalTopicsRef}
                    id="general-topics"
                    className="flex gap-5 max-[1024px]:hidden"
                >
                    <li className="topic">Фильмы</li>
                    <li className="topic">Сериалы</li>
                    <li className="topic">Мультфильмы</li>
                    <li className="topic">Аниме</li>
                </ul>
                <div
                    ref={newShowsAndCollectionsRef}
                    id="new-shows-and-collections"
                    className="flex gap-5 max-[1024px]:hidden"
                >
                    <p id="new-shows" className="">
                        Новинки
                    </p>
                    <p id="collections">Подборки</p>
                </div>
                <div className="flex items-center gap-5">
                    <div
                        id="search"
                        className="flex items-center gap-1 text-2xl"
                    >
                        🔍
                        <span
                            className={
                                'text-base underline opacity-70 max-[1024px]:hidden'
                            }
                        >
                            Искать...
                        </span>
                    </div>
                    <img
                        className=" w-[6.5%] hover:cursor-pointer"
                        src="icons/moon.png"
                        alt="switch light/dark modes"
                    />
                    <div id="login-block" className="flex items-center">
                        <img
                            id="login-icon"
                            className="mr-2 w-[17%] hover:cursor-pointer"
                            src="icons/login.png"
                            alt="login"
                        />
                        <p className="mr-1 text-white">Войти</p>
                    </div>
                    {/*<p>досмотреть</p>*/}
                </div>
            </header>
        </>
    )
}

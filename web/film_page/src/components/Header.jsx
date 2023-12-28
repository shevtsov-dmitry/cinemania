import { useEffect, useRef } from 'react'

const STYLE = {
    IMAGE_WIDTH: 23,
    LOGO_IMAGE_WIDTH: 50,
}

export function Header() {
    const generalTopicsRef = useRef()
    const newShowsAndCollectionsRef = useRef()
    const loginImageRef = useRef()
    const searchImageRef = useRef()
    const burgerImageRef = useRef()
    const closeImageRef = useRef()

    useEffect(() => {
        burgerImageRef.current.addEventListener('click', () => {
            burgerImageRef.current.style.display = 'none'
            closeImageRef.current.style.display = 'block'
        })

        closeImageRef.current.addEventListener('click', () => {
            burgerImageRef.current.style.display = 'block'
            closeImageRef.current.style.display = 'none'
        })
    }, [])

    return (
        <>
            <header
                id="upper-header"
                className="text-amber-whiteflex ml-[3.5%] mr-[3.5%] flex items-center justify-between text-white"
            >
                <img
                    className="mt-2 w-[6%]"
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
                <div className="flex w-fit items-center justify-end gap-5">
                    <div
                        id="search"
                        className="flex items-center gap-1 text-2xl"
                    >
                        <img
                            ref={searchImageRef}
                            id="search-image"
                            className={`w-[${STYLE.IMAGE_WIDTH}px]`}
                            src={'icons/search.png'}
                            alt=""
                        />
                        <span
                            className={
                                'text-base underline opacity-70 max-[1024px]:hidden'
                            }
                        >
                            Искать...
                        </span>
                    </div>
                    <div id="login-block" className="flex items-center gap-2">
                        <img
                            ref={loginImageRef}
                            id="login-icon"
                            className={`w-[${STYLE.IMAGE_WIDTH}px] hover:cursor-pointer`}
                            src={'icons/login.png'}
                            alt="login"
                        />
                        <p className="select-none text-white max-[1024px]:hidden cursor-pointer">
                            Войти
                        </p>
                    </div>
                    <img
                        ref={burgerImageRef}
                        className={`w-[${STYLE.IMAGE_WIDTH}px] hidden scale-125 hover:cursor-pointer max-[1024px]:block`}
                        src={'icons/burger.png'}
                        alt=""
                    />
                    <img
                        ref={closeImageRef}
                        className={`w-[${STYLE.IMAGE_WIDTH}px] hidden scale-75 hover:cursor-pointer min-[1024px]:hidden`}
                        src={'icons/close.png'}
                        alt=""
                    />
                    {/*<p>досмотреть</p>*/}
                </div>
            </header>
        </>
    )
}

export function Header() {
    return (
        <>
            <header
                id="upper-header"
                className="text-amber-white flex h-10 items-center justify-between bg-slate-200"
            >
                <img
                    className="h-9 w-9"
                    src="https://cdn-icons-png.flaticon.com/128/7269/7269735.png"
                    alt="switch light/dark modes"
                />
                <img
                    className="h-10 w-10"
                    id="company-logo"
                    src="https://cdn-icons-png.flaticon.com/128/3621/3621439.png"
                    alt="company logo"
                />
                {/*<div id="search">🔍 Искать...</div>*/}
                <div id="login-block" className="flex items-center">
                    <img
                        id="login-icon"
                        className="mr-2 h-9 w-9"
                        src="https://cdn-icons-png.flaticon.com/128/2609/2609282.png"
                    />
                    <p className="mr-1">Войти</p>
                </div>
                {/*<p>досмотреть</p>*/}
            </header>
            <header
                id="lower-header"
                className="flex justify-between text-white"
            >
                <ul id="general-topics" className="flex">
                    <li className="topic">Фильмы</li>
                    <li className="topic">Сериалы</li>
                    <li className="topic">Мультфильмы</li>
                    <li className="topic">Аниме</li>
                </ul>
                <div id="new-shows-and-collections">
                    <p id="new-shows">Новинки</p>
                    <p id="collections">Подборки</p>
                </div>
            </header>
        </>
    )
}

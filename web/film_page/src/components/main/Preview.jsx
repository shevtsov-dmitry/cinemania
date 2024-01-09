const SIZE = {
    POSTER_HEIGHT: 'h-80',
}

export function Preview() {
    return (
        <>
            <h3 className={'p-2 text-2xl font-bold text-white'}>Новинки</h3>
            <div id="previews-sequence-block">
                <div className="no-scrollbar relative overflow-x-scroll p-2">
                    <ul className="flex w-fit gap-4">
                        {fillBlockWithPosters()}
                    </ul>
                </div>
                <div
                    className={
                        `arrows-holder w-dvw absolute mt-[-20.5rem] ` +
                        SIZE.POSTER_HEIGHT
                    }
                >
                    <div className="centered-arrow left-8">
                        <img
                            src={'icons/left-arrow.png'}
                            className={'arrow'}
                            alt={'scroll left'}
                        />
                    </div>
                    <div className="centered-arrow right-0">
                        <img
                            src={'icons/right-arrow.png'}
                            className={'arrow'}
                            alt={'scroll right'}
                        />
                    </div>
                </div>
            </div>
        </>
    )
}

function fillBlockWithPosters() {
    const content = []
    for (let i = 0; i < 15; i++) {
        content.push(
            <li
                key={i}
                className={
                    `z-10 w-60 rounded-3xl bg-indigo-900 transition-all ${SIZE.POSTER_HEIGHT} ` +
                    'hover:scale-105 hover:cursor-pointer '
                }
            ></li>
        )
    }
    return content
}

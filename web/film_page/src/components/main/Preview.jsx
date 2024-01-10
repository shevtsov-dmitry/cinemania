import {useRef} from "react";

export function Preview() {
    const blockRef = useRef()

    const ARROW_SCROLL_DISTANCE = 800
    function scrollLeft() {
        if(blockRef.current) {
            blockRef.current.scrollLeft -= ARROW_SCROLL_DISTANCE;
        }
    }
    function scrollRight() {
        if(blockRef.current) {
            blockRef.current.scrollLeft += ARROW_SCROLL_DISTANCE;
        }
    }

    return (
        <>
            <h3 className={'p-2 text-2xl font-bold text-white'}>Новинки</h3>
            <div id="previews-sequence-block">
                <div ref={blockRef}
                     className="no-scrollbar relative overflow-x-scroll p-2 scroll-smooth">
                    <ul className="flex w-fit gap-4">
                        {fillBlockWithPosters()}
                    </ul>
                </div>
                <div
                    className={`arrows-holder w-dvw absolute -mt-[20.5rem] hidden h-[20rem] md:block`}
                >
                    <div className="centered-arrow left-8" onClick={scrollLeft}>
                        <img
                            src={'icons/left-arrow.png'}
                            className={'arrow'}
                            alt={'scroll left'}
                        />
                    </div>
                    <div className="centered-arrow right-0" onClick={scrollRight}>
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
                    `z-10 h-80 w-60 rounded-3xl bg-indigo-900 transition-all ` +
                    'hover:scale-105 hover:cursor-pointer '
                }
            ></li>
        )
    }
    return content
}

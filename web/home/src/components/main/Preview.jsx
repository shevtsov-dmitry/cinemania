import { useEffect, useRef, useState } from 'react'
import { SideScrollArrow } from '../common/util/SideScrollArrow/SideScrollArrow'

export function Preview() {
    const scrollableBlockRef = useRef()
    const rightArrowRef = useRef()
    const leftArrowRef = useRef()
    const arrowsHolder = useRef()
    const [isBlockHovered, setIsBlockHovered] = useState(false)

    const ARROW_SCROLL_DISTANCE = 800
    const Arrow = new SideScrollArrow(scrollableBlockRef)

    const scrollLeft = () => Arrow.scrollLeft(ARROW_SCROLL_DISTANCE)
    const scrollRight = () => Arrow.scrollRight(ARROW_SCROLL_DISTANCE)
    const hideArrowsLeaningScreen = () =>
        Arrow.hideArrowsLeaningScreen(leftArrowRef, rightArrowRef)
    const hideShowArrowsOnHover = () =>
        Arrow.hideShowArrowsOnHover(isBlockHovered, arrowsHolder)

    useEffect(() => {
        // init
        leftArrowRef.current.style.visibility = 'hidden'
    }, [])

    useEffect(() => {
        hideShowArrowsOnHover()
    }, [isBlockHovered])

    useEffect(() => {
        const blockElement = scrollableBlockRef.current

        if (blockElement) {
            blockElement.addEventListener('scroll', hideArrowsLeaningScreen)
        }

        return () => {
            if (blockElement) {
                blockElement.removeEventListener(
                    'scroll',
                    hideArrowsLeaningScreen
                )
            }
        }
    }, [])

    return (
        <>
            <h3 className={'p-2 text-2xl font-bold text-white'}>Новинки</h3>
            <div
                id="previews-sequence-block"
                onMouseEnter={() => {
                    setIsBlockHovered(true)
                }}
                onMouseLeave={() => {
                    setIsBlockHovered(false)
                }}
            >
                <div
                    ref={scrollableBlockRef}
                    className="no-scrollbar relative overflow-x-scroll scroll-smooth p-2"
                >
                    <ul className="flex w-fit gap-4">
                        {fillBlockWithPosters()}
                    </ul>
                </div>
                <div
                    ref={arrowsHolder}
                    className={`arrows-holder w-dvw absolute -mt-[20.5rem] h-[20rem] `}
                >
                    <div className="centered-arrow left-8" onClick={scrollLeft}>
                        <img
                            ref={leftArrowRef}
                            src={'icons/left-arrow.png'}
                            className={'arrow max-md:w-0 max-md:bg-transparent'}
                            alt={'scroll left'}
                        />
                    </div>
                    <div
                        className="centered-arrow right-0"
                        onClick={scrollRight}
                    >
                        <img
                            ref={rightArrowRef}
                            src={'icons/right-arrow.png'}
                            className={'arrow max-md:w-0 max-md:bg-transparent'}
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

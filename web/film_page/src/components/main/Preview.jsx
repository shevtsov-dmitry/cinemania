import { useEffect, useRef, useState } from 'react'
import {SideScrollArrow} from "../common/util/SideScrollArrow/SideScrollArrow";

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


    function hideArrowsLeaningScreen() {
        const block = scrollableBlockRef.current

        if (block) {
            const isScrollAtMin = block.scrollLeft === 0
            const isScrollAtMax =
                block.scrollLeft >= block.scrollWidth - block.clientWidth

            if (isScrollAtMin) {
                leftArrowRef.current.style.visibility = 'hidden'
            } else {
                leftArrowRef.current.style.visibility = 'visible'
            }

            if (isScrollAtMax) {
                rightArrowRef.current.style.visibility = 'hidden'
            } else {
                rightArrowRef.current.style.visibility = 'visible'
            }
        }
    }

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
            // Remove the scroll event listener on component unmount
            if (blockElement) {
                blockElement.removeEventListener(
                    'scroll',
                    hideArrowsLeaningScreen
                )
            }
        }
    }, [])

    function hideShowArrowsOnHover() {
        let style = arrowsHolder.current.style
        isBlockHovered ? (style.display = 'block') : (style.display = 'none')
    }

    return (
        <>
            <h3 className={'p-2 text-2xl font-bold text-white'}>Новинки</h3>
            <div
                id="previews-sequence-block"
                onMouseEnter={() => {
                    console.log('SO... SO... ')
                    setIsBlockHovered(true)
                }}
                onMouseLeave={() => {
                    console.log('LEFT')
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
                    className={`arrows-holder w-dvw absolute -mt-[20.5rem] h-[20rem]`}
                >
                    <div className="centered-arrow left-8" onClick={scrollLeft}>
                        <img
                            ref={leftArrowRef}
                            src={'icons/left-arrow.png'}
                            className={'arrow'}
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

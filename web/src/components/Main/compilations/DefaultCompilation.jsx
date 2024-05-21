import { useEffect, useRef, useState } from 'react'
import { SideScrollArrow } from '../../common/util/SideScrollArrow'

export function DefaultCompilation(props) {
    // *** EDGE SCREEN ARROWS
    const scrollableBlockRef = useRef()
    const rightArrowRef = useRef()
    const leftArrowRef = useRef()
    const arrowsHolder = useRef()

    const [isPostersBlockHovered, setIsPostersBlockHovered] = useState(false)

    const Arrow = new SideScrollArrow(scrollableBlockRef)
    const ARROW_SCROLL_DISTANCE = 800
    const scrollLeft = () => Arrow.scrollLeft(ARROW_SCROLL_DISTANCE)
    const scrollRight = () => Arrow.scrollRight(ARROW_SCROLL_DISTANCE)
    const hideArrowsLeaningScreen = () =>
        Arrow.hideArrowsLeaningScreen(leftArrowRef, rightArrowRef)
    const hideShowArrowsOnHover = () =>
        Arrow.hideShowArrowsOnHover(isPostersBlockHovered, arrowsHolder)

    // useEffect(() => {
    //     if (!isPlayerOpened) {
    //         leftArrowRef.current.style.visibility = 'hidden'
    //     }
    // }, [])

    useEffect(() => {
        hideShowArrowsOnHover()
    }, [isPostersBlockHovered])

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

    const initSize = 30 // get it from props or other way like fetch
    const compilation = new Array(initSize)
    for (let i = 0; i < initSize; i++) {
        compilation.push(<Element />)
    }

    return (
        <div
            ref={scrollableBlockRef}
            onMouseEnter={() => setIsPostersBlockHovered(true)}
            onMouseLeave={() => setIsPostersBlockHovered(false)}
            className="no-scrollbar relative overflow-x-scroll scroll-smooth p-2"
        >
            <div className="flex w-fit gap-4 overflow-scroll bg-fuchsia-500">
                {compilation}
            </div>

            <div
                ref={arrowsHolder}
                className={`arrows-holder absolute -mt-[20.5rem] h-[20rem] w-dvw `}
            >
                <div className="centered-arrow left-8" onClick={scrollLeft}>
                    <img
                        ref={leftArrowRef}
                        src={'icons/left-arrow.png'}
                        className={'arrow max-md:w-0 max-md:bg-transparent'}
                        alt={'scroll left'}
                    />
                </div>
                <div className="centered-arrow right-0" onClick={scrollRight}>
                    <img
                        ref={rightArrowRef}
                        src={'icons/right-arrow.png'}
                        className={'arrow max-md:w-0 max-md:bg-transparent'}
                        alt={'scroll right'}
                    />
                </div>
            </div>
        </div>
    )
}

function Element() {
    return <div className="h-44 w-72 overflow-hidden rounded bg-sky-100"></div>
}

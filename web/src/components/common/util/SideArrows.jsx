import { useEffect, useRef, useState } from 'react'

/** Custom react component, which adds side arrows to collection, making it interactive and scrollable.
 * requires @param scrollableBlockRef collection
 * NOTE! Should be contained inside @param scrollableBlockRef
 */
export function SideArrows(props) {
    const [isPostersBlockHovered, setIsPostersBlockHovered] = useState(false)

    const rightArrowRef = useRef()
    const leftArrowRef = useRef()
    const arrowsHolderRef = useRef()
    const scrollableBlockRef = props.scrollableBlockRef

    const ARROW_SCROLL_DISTANCE = 800

    let Arrow
    if (scrollableBlockRef !== undefined) {
        Arrow = new SideScrollArrow(scrollableBlockRef)
    }
    const scrollLeft = () => Arrow.scrollLeft(ARROW_SCROLL_DISTANCE)
    const scrollRight = () => Arrow.scrollRight(ARROW_SCROLL_DISTANCE)

    const hideArrowsLeaningScreen = () =>
        Arrow.hideArrowsLeaningScreen(leftArrowRef, rightArrowRef)

    const hideShowArrowsOnHover = () =>
        Arrow.hideShowArrowsOnHover(isPostersBlockHovered, arrowsHolderRef)

    // useEffect(() => {
    //     if (!isPlayerOpened) {
    //         leftArrowRef.current.style.visibility = 'hidden'
    //     }
    // }, [])
    //

    useEffect(() => {
        // console.log(isPostersBlockHovered)
        hideShowArrowsOnHover()
    }, [isPostersBlockHovered])

    useEffect(() => {
        if (scrollableBlockRef === undefined) return
        scrollableBlockRef.current.addEventListener('mouseenter', () => {
            scrollableBlockRef.current.style.backgroundColor = 'red'
            setIsPostersBlockHovered(true)
        })

        scrollableBlockRef.current.addEventListener('mouseleave', () => {
            scrollableBlockRef.current.style.backgroundColor = 'blue'
            setIsPostersBlockHovered(false)
        })

        let blockElement = scrollableBlockRef
        blockElement = blockElement.current
        blockElement.addEventListener('scroll', hideArrowsLeaningScreen)
        // blockElement.removeEventListener('scroll', hideArrowsLeaningScreen)
    }, [scrollableBlockRef])

    return (
        <div
            ref={arrowsHolderRef}
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
            <div
                className="centered-arrow right-0"
                onClick={scrollRight}
                // onMouseEnter
            >
                <img
                    ref={rightArrowRef}
                    src={'icons/right-arrow.png'}
                    className={'arrow max-md:w-0 max-md:bg-transparent'}
                    alt={'scroll right'}
                />
            </div>
        </div>
    )
}

class SideScrollArrow {
    scrollableBlockRef

    constructor(scrollableBlockRef) {
        this.scrollableBlockRef = scrollableBlockRef
    }
    scrollLeft(distance) {
        this.scrollableBlockRef.current.scrollLeft -= distance
    }
    scrollRight(distance) {
        this.scrollableBlockRef.current.scrollLeft += distance
    }

    hideArrowsLeaningScreen(leftArrowRef, rightArrowRef) {
        const block = this.scrollableBlockRef.current
        if (block === undefined) return

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

    hideShowArrowsOnHover(isBlockHovered, arrowsHolder) {
        if (arrowsHolder.current === undefined) return
        const style = arrowsHolder.current.style
        isBlockHovered ? (style.display = 'block') : (style.display = 'none')
    }
}

import { useEffect, useRef, useState } from 'react'

/** Custom react component, which adds side arrows to collection, making it interactive and scrollable.
 * requires @property scrollableDivRef collection reference.
 */
export function SideArrows(props) {
    const [isPostersBlockHovered, setIsPostersBlockHovered] = useState(true)

    const rightArrowRef = useRef()
    const leftArrowRef = useRef()
    const arrowsHolderRef = useRef()
    const scrollableDivRef = props.scrollableDivRef

    const ARROW_SCROLL_DISTANCE = 800

    let Arrow
    if (scrollableDivRef !== undefined) {
        Arrow = new SideScrollArrows(
            scrollableDivRef,
            leftArrowRef,
            rightArrowRef
        )
    }

    const scrollLeft = () => Arrow.scrollLeft(ARROW_SCROLL_DISTANCE)
    const scrollRight = () => Arrow.scrollRight(ARROW_SCROLL_DISTANCE)
    const hideArrowsLeaningScreen = () => Arrow.hideArrowsLeaningScreen()
    const hideShowArrowsOnHover = () =>
        Arrow.hideShowArrowsOnHover(isPostersBlockHovered, arrowsHolderRef)

    // useEffect(() => {
    //     if (!isPlayerOpened) {
    //         leftArrowRef.current.style.visibility = 'hidden'
    //     }
    // }, [])
    //

    useEffect(() => {
        hideShowArrowsOnHover()
    }, [isPostersBlockHovered])

    useEffect(() => {
        if (scrollableDivRef === undefined) return
        scrollableDivRef.current.addEventListener('mouseenter', () => {
            scrollableDivRef.current.style.backgroundColor = 'red'
            setIsPostersBlockHovered(true)
        })

        scrollableDivRef.current.addEventListener('mouseleave', () => {
            scrollableDivRef.current.style.backgroundColor = 'blue'
            setIsPostersBlockHovered(false)
        })

        let blockElement = scrollableDivRef
        blockElement = blockElement.current
        blockElement.addEventListener('scroll', hideArrowsLeaningScreen)
        // blockElement.removeEventListener('scroll', hideArrowsLeaningScreen)
    }, [scrollableDivRef])

    return (
        <div
            ref={arrowsHolderRef}
            className={`absolute h-fit w-dvw bg-green-400`}
        >
            <img
                ref={leftArrowRef}
                src={'icons/left-arrow.png'}
                className={'arrow absolute left-5 z-20'}
                onClick={scrollLeft}
                alt={'scroll compilation left'}
            />
            <img
                ref={rightArrowRef}
                src={'icons/right-arrow.png'}
                className={'arrow absolute right-5 z-20'}
                onClick={scrollRight}
                alt={'scroll compilation right'}
            />
        </div>
    )
}

class SideScrollArrows {
    rightArrowRef
    leftArrowRef
    scrollableDivRef

    constructor(scrollableDivRef, leftArrowRef, rightArrowRef) {
        this.scrollableDivRef = scrollableDivRef
        this.rightArrowRef = rightArrowRef
        this.leftArrowRef = leftArrowRef
    }
    scrollLeft(distance) {
        this.scrollableDivRef.current.scrollLeft -= distance
    }
    scrollRight(distance) {
        this.scrollableDivRef.current.scrollLeft += distance
    }

    hideArrowsLeaningScreen() {
        const block = this.scrollableDivRef.current
        if (block === undefined) return

        const isScrollAtMin = block.scrollLeft === 0
        const isScrollAtMax =
            block.scrollLeft >= block.scrollWidth - block.clientWidth

        if (isScrollAtMin) {
            this.leftArrowRef.current.style.visibility = 'hidden'
        } else {
            this.leftArrowRef.current.style.visibility = 'visible'
        }

        if (isScrollAtMax) {
            this.rightArrowRef.current.style.visibility = 'hidden'
        } else {
            this.rightArrowRef.current.style.visibility = 'visible'
        }
    }

    hideShowArrowsOnHover(isBlockHovered, arrowsHolder) {
        if (arrowsHolder.current === undefined) return
        const style = arrowsHolder.current.style
        isBlockHovered ? (style.display = 'flex') : (style.display = 'none')
    }
}
